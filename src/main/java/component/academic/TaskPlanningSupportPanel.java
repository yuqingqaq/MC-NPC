package component.academic;
import com.mojang.blaze3d.vertex.PoseStack;

import component.HintScrollPanel;
import model.AdaptiveSubTaskModel;
import model.NPCModel;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import system.TaskManager;
import controller.GameController;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;

public class TaskPlanningSupportPanel extends AbstractWidget implements Widget {
    private final List<AdaptiveSubTaskModel> subTasks; // 子任务列表
    private final List<Button> taskButtons; // 每个子任务对应的按钮
    private final int panelWidth;
    private final int panelHeight;

    private int draggingTaskIndex = -1; // 当前正在拖动的子任务索引
    private int dragY = 0; // 当前拖动的 Y 坐标
    private Button startButton; // 开始行动按钮
    private Button aiSortButton; // AI辅助排序按钮
    private NPCModel sortingAgent; // 排序辅助Agent
    private HintScrollPanel hintPanel; // 建议面板
    private List<String> hintHistory; // 建议历史

    public TaskPlanningSupportPanel(int x, int y, int width, int height, List<AdaptiveSubTaskModel> initialSubTasks) {
        // 将宽度扩大1.5倍
        super(x, y, width, height, new TextComponent("Task Planning Panel"));
        this.subTasks = new ArrayList<>(initialSubTasks); // 初始化子任务列表
        this.taskButtons = new ArrayList<>();
        this.panelWidth = width;
        this.panelHeight = height;
        this.sortingAgent = createSortingAgent(); // 创建排序辅助Agent
        this.hintHistory = new ArrayList<>();
        init();
    }

    /**
     * 创建排序辅助Agent
     */
    private NPCModel createSortingAgent() {
        return new NPCModel("任务排序Agent");
    }

    // 初始化子任务按钮和开始行动按钮
    private void init() {
        int buttonHeight = 20; // 每个子任务按钮的高度
        int buttonSpacing = 5; // 按钮之间的间距
        int startY = this.y + 20; // 第一个按钮的起始 Y 坐标

        // 设置面板布局 - 左侧任务列表宽度占3/5，右侧建议面板占2/5
        int leftPanelWidth = (this.panelWidth * 3) / 5; // 左侧面板宽度（子任务列表）
        int rightPanelWidth = (this.panelWidth * 2) / 5; // 右侧面板宽度（AI建议面板）

        taskButtons.clear(); // 清空按钮列表

        // 创建左侧任务按钮 - 更靠左放置
        for (int i = 0; i < subTasks.size(); i++) {
            final int index = i;  // 创建一个 final 变量
            int buttonY = startY + i * (buttonHeight + buttonSpacing);
            AdaptiveSubTaskModel subTask = subTasks.get(i);

            // 创建子任务按钮，居左放置
            Button button = new Button(
                    this.x + 20,
                    buttonY,
                    leftPanelWidth - 40, // 减小宽度给两侧留出更多空间
                    buttonHeight,
                    new TextComponent(subTask.getTitle() + " (" + subTask.getEstimatedTime() + ")"),
                    btn -> draggingTaskIndex = index
            );

            taskButtons.add(button);
        }

        // 创建AI辅助排序按钮，放在右侧面板底部
        this.aiSortButton = new Button(
                this.x + leftPanelWidth + 10,
                this.y + this.panelHeight - 30,
                rightPanelWidth - 20,
                buttonHeight,
                new TextComponent("AI辅助排序"),
                btn -> aiSortTasks()
        );

        // 创建开始行动按钮，放在左侧面板底部
        this.startButton = new Button(
                this.x + 70,
                this.y + this.panelHeight - 5,
                leftPanelWidth - 40,
                buttonHeight,
                new TextComponent("开始行动"),
                btn -> startTask()
        );

        // 创建右侧建议面板 - 确保面板在右侧区域内部
        this.hintPanel = new HintScrollPanel(
                Minecraft.getInstance(),
                rightPanelWidth - 10, // 宽度略小于右侧区域
                this.panelHeight - 70, // 高度留出顶部标题和底部按钮的空间
                this.y + 30, // 上边距
                this.x + leftPanelWidth, // 左边距
                4, // 边框厚度
                5, // 内边距
                hintHistory
        );
    }


    /**
     * AI辅助排序的逻辑
     */
    private void aiSortTasks() {
        if (subTasks.isEmpty()) {
            return;
        }

        // 构建提示词，包含所有子任务的信息
        StringBuilder prompt = new StringBuilder();
        prompt.append("我正在进行自我调节学习 (SRL) 任务策略规划阶段" +
                "请根据帮我对以下子任务进行排序，以便最有效率地完成整个任务, 同时锻炼我的SRL思维。\n\n");
        prompt.append("请考虑以下因素：\n");
        prompt.append("A. 任务之间的逻辑依赖关系\n");
        prompt.append("B. 资源利用效率\n");
        prompt.append("C. 时间和难度的平衡\n\n");
        prompt.append("子任务列表：\n");

        for (int i = 0; i < subTasks.size(); i++) {
            AdaptiveSubTaskModel task = subTasks.get(i);
            prompt.append((i + 1) + ". " + task.getTitle() + "\n");
            prompt.append("   描述: " + task.getDescription() + "\n");
            prompt.append("   预计时间: " + task.getEstimatedTime() + "\n\n");
        }

        prompt.append("请严格按照以下格式回复：\n\n");
        prompt.append("a. 最佳排序顺序：\n");
        prompt.append("<START>\n");
        prompt.append("按最佳顺序排列的任务编号，用逗号分隔。例如：3,1,5,2,4\n");
        prompt.append("<END>\n\n");
        prompt.append("b. 排序理由：\n");
        prompt.append("解释为什么这种排序最合理\n\n");
        prompt.append("c. 任务完成建议：\n");
        prompt.append("针对这个排序提供的任务完成建议\n\n");
        prompt.append("注意：务必使用<START>和<END>标签来标记排序顺序！");

        // 清空历史记录并添加提示信息
        hintHistory.clear();
        hintHistory.add("正在生成排序建议...");
        hintPanel.refreshPanel();

        // 调用Agent获取排序建议
        String response = GameController.getInstance().interactWithExpert(sortingAgent, prompt.toString());

        // 用户不需要看到技术标记，先处理一下回复
        String cleanResponse = cleanResponseForDisplay(response);

        // 解析Agent返回的排序建议
        List<Integer> newOrder = parseAgentResponse(response);

        // 更新历史记录
        hintHistory.clear();
        hintHistory.add("");
        hintHistory.add(cleanResponse);
        hintPanel.refreshPanel();

        // 检查解析结果
        String errorMessage = "";
        boolean success = false;

        if (newOrder.isEmpty()) {
            errorMessage = "未能找到任何有效的排序序号。";
        } else if (newOrder.size() < subTasks.size()) {
            errorMessage = "解析到的排序序号数量(" + newOrder.size() + ")少于任务数量(" + subTasks.size() + ")。";
        } else if (newOrder.size() > subTasks.size()) {
            errorMessage = "解析到的排序序号数量(" + newOrder.size() + ")多于任务数量(" + subTasks.size() + ")。";
        } else {
            // 确保所有任务索引都在有效范围内
            boolean allValid = true;
            List<Integer> invalidIndices = new ArrayList<>();

            for (Integer index : newOrder) {
                if (index < 1 || index > subTasks.size()) {
                    allValid = false;
                    invalidIndices.add(index);
                }
            }

            if (!allValid) {
                errorMessage = "存在无效的任务序号: " + invalidIndices + "。有效范围是1到" + subTasks.size() + "。";
            } else {
                // 检查是否有重复的索引
                boolean hasDuplicate = false;
                List<Integer> duplicateIndices = new ArrayList<>();
                for (int i = 0; i < newOrder.size(); i++) {
                    for (int j = i + 1; j < newOrder.size(); j++) {
                        if (newOrder.get(i).equals(newOrder.get(j)) && !duplicateIndices.contains(newOrder.get(i))) {
                            hasDuplicate = true;
                            duplicateIndices.add(newOrder.get(i));
                        }
                    }
                }

                if (hasDuplicate) {
                    errorMessage = "排序序号中存在重复: " + duplicateIndices + "出现多次。";
                } else {
                    // 所有检查都通过，可以排序
                    List<AdaptiveSubTaskModel> sortedSubTasks = new ArrayList<>();
                    for (int index : newOrder) {
                        sortedSubTasks.add(subTasks.get(index - 1));
                    }

                    // 应用排序结果
                    subTasks.clear();
                    subTasks.addAll(sortedSubTasks);
                    init(); // 重新初始化UI

                    hintPanel.refreshPanel();
                    success = true;
                }
            }
        }

        // 如果排序未成功，显示详细的错误信息
        if (!success) {
            hintHistory.add("");
            hintHistory.add("无法完成自动排序");
            hintHistory.add("原因: " + errorMessage);
            hintHistory.add("");
            hintHistory.add("您仍可参考上方建议手动排序，或再次点击\"AI辅助排序\"按钮获取新的建议。");
            hintPanel.refreshPanel();

            // 输出调试信息
            System.out.println("AI排序失败: " + errorMessage);
            System.out.println("解析的排序序号: " + newOrder);
            System.out.println("原始回复: " + response);
        }
    }

    /**
     * 清理回复中的技术标记，使其对用户更友好
     */
    private String cleanResponseForDisplay(String response) {
        // 删除<START>和<END>标记
        String cleanedResponse = response.replaceAll("<START>|<END>", "");

        // 尝试查找排序部分，替换为任务名称
        try {
            String startTag = "<START>";
            String endTag = "<END>";

            if (response.contains(startTag) && response.contains(endTag)) {
                int startIndex = response.indexOf(startTag) + startTag.length();
                int endIndex = response.indexOf(endTag);

                if (startIndex < endIndex) {
                    String orderSection = response.substring(startIndex, endIndex).trim();

                    // 处理提取到的部分，只保留数字和逗号
                    String cleanOrderSection = orderSection.replaceAll("[^0-9,]", "");
                    String[] numberStrings = cleanOrderSection.split(",");

                    // 构建任务名称列表
                    StringBuilder taskNamesList = new StringBuilder();
                    for (String numStr : numberStrings) {
                        if (!numStr.trim().isEmpty()) {
                            int taskIndex = Integer.parseInt(numStr.trim()) - 1;
                            if (taskIndex >= 0 && taskIndex < subTasks.size()) {
                                if (taskNamesList.length() > 0) {
                                    taskNamesList.append(" → ");
                                }
                                taskNamesList.append(subTasks.get(taskIndex).getTitle());
                            }
                        }
                    }

                    // 替换原始的排序部分
                    if (taskNamesList.length() > 0) {
                        cleanedResponse = cleanedResponse.replace(orderSection, taskNamesList.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("处理显示回复时出错: " + e.getMessage());
        }

        return cleanedResponse;
    }

    /**
     * 解析Agent的回复，提取排序顺序
     */
    private List<Integer> parseAgentResponse(String response) {
        List<Integer> result = new ArrayList<>();

        try {
            // 首先尝试使用标记提取
            String startTag = "<START>";
            String endTag = "<END>";

            if (response.contains(startTag) && response.contains(endTag)) {
                int startIndex = response.indexOf(startTag) + startTag.length();
                int endIndex = response.indexOf(endTag);

                if (startIndex < endIndex) {
                    String orderSection = response.substring(startIndex, endIndex).trim();
                    System.out.println("提取到的排序部分: " + orderSection);

                    // 处理提取到的部分，只保留数字和逗号
                    String cleanOrderSection = orderSection.replaceAll("[^0-9,]", "");
                    String[] numbers = cleanOrderSection.split(",");

                    for (String num : numbers) {
                        if (!num.trim().isEmpty()) {
                            result.add(Integer.parseInt(num.trim()));
                        }
                    }

                    // 如果使用标记提取成功，直接返回
                    if (!result.isEmpty()) {
                        System.out.println("使用标记提取成功: " + result);
                        return result;
                    }
                }
            }

            // 标记提取失败，尝试其他方法
            System.out.println("标记提取失败，尝试其他方法");

            // 尝试查找"最佳排序顺序"行
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.contains("排序") && line.contains("顺序")) {
                    // 提取这一行中的所有数字和逗号
                    String numbers = line.replaceAll("[^0-9,]", "");
                    String[] numArray = numbers.split(",");

                    List<Integer> lineResult = new ArrayList<>();
                    for (String num : numArray) {
                        if (!num.trim().isEmpty()) {
                            lineResult.add(Integer.parseInt(num.trim()));
                        }
                    }

                    // 如果找到了足够的数字
                    if (lineResult.size() == subTasks.size()) {
                        System.out.println("从包含'排序顺序'的行提取成功: " + lineResult);
                        return lineResult;
                    }
                }
            }

            // 如果还是失败，尝试更宽松的方法
            // 找到所有包含数字和逗号的行
            for (String line : lines) {
                if (line.matches(".*\\d+.*") && line.contains(",")) {
                    // 找到了一个包含数字和逗号的行
                    String cleanLine = line.replaceAll("[^0-9,]", "");
                    String[] numbers = cleanLine.split(",");

                    List<Integer> lineResult = new ArrayList<>();
                    for (String num : numbers) {
                        if (!num.trim().isEmpty()) {
                            lineResult.add(Integer.parseInt(num.trim()));
                        }
                    }

                    // 如果找到了足够的数字
                    if (lineResult.size() == subTasks.size()) {
                        System.out.println("从包含数字和逗号的行提取成功: " + lineResult);
                        return lineResult;
                    }
                }
            }

            // 如果仍然失败，尝试从所有数字中提取
            List<Integer> allNumbers = new ArrayList<>();
            for (String line : lines) {
                // 使用正则表达式提取所有独立的数字
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b([1-9][0-9]*)\\b");
                java.util.regex.Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    if (number >= 1 && number <= subTasks.size() && !allNumbers.contains(number)) {
                        allNumbers.add(number);
                    }
                }

                // 如果找到了足够的数字，立即返回
                if (allNumbers.size() == subTasks.size()) {
                    System.out.println("从所有数字中提取成功: " + allNumbers);
                    return allNumbers;
                }
            }

            // 如果找到了一些数字但不是全部，也返回它们
            if (!allNumbers.isEmpty()) {
                System.out.println("部分提取成功: " + allNumbers);
                return allNumbers;
            }

        } catch (Exception e) {
            System.out.println("解析Agent回复时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // 开始行动的逻辑
    private void startTask() {
        // 获取排序后的子任务列表
        if (!subTasks.isEmpty()) {
            AdaptiveSubTaskModel firstSubTask = subTasks.get(0); // 获取第一个子任务

            // 如果是SRL任务，标记策略已规划
            if (GameController.getInstance().isSRLQuestAvailable()) {
                TaskManager.getInstance().setStrategyPlanned(true);
                System.out.println("SRL策略规划已完成");
            }

            // 调用 TaskManager 的方法来开始任务，并传入排序后的子任务列表
            TaskManager.getInstance().startSubTask(firstSubTask, subTasks);
            System.out.println("开始子任务: " + firstSubTask.getTitle());
        }
    }

    // 计算鼠标释放时的目标索引
    private int calculateTargetIndex(int mouseY) {
        int leftPanelWidth = (this.panelWidth * 3) / 5; // 左侧面板宽度
        int startY = this.y + 30; // 第一个按钮的起始 Y 坐标
        int buttonHeight = 20; // 按钮高度
        int buttonSpacing = 5; // 按钮间距
        int totalHeight = buttonHeight + buttonSpacing; // 每个按钮占用的总高度

        int relativeY = mouseY - startY;
        return Math.min(subTasks.size() - 1, Math.max(0, relativeY / totalHeight));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingTaskIndex != -1) {
            dragY = (int) mouseY; // 更新拖动的 Y 坐标
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingTaskIndex != -1) {
            // 鼠标释放时，计算目标索引，并重新排序任务
            int targetIndex = calculateTargetIndex((int) mouseY);
            AdaptiveSubTaskModel draggedSubTask = subTasks.remove(draggingTaskIndex); // 移除拖动的子任务
            subTasks.add(targetIndex, draggedSubTask); // 插入到目标位置

            // 重置拖动状态
            draggingTaskIndex = -1;
            dragY = 0;

            // 重新初始化按钮
            init();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染背景面板
        fill(poseStack, this.x, this.y, this.x + this.panelWidth, this.y + this.panelHeight, 0x33000000); // 半透明背景

        int leftPanelWidth = (this.panelWidth * 3) / 5; // 左侧面板宽度

        // 渲染左侧标题
        Minecraft.getInstance().font.draw(
                poseStack,
                "拖动子任务以排序",
                this.x + 20,
                this.y + 10,
                0xFFFFFF
        );

        // 渲染右侧标题
        Minecraft.getInstance().font.draw(
                poseStack,
                "AI排序建议",
                this.x + leftPanelWidth + 10,
                this.y + 10,
                0xFFFFFF
        );

        // 渲染子任务按钮
        for (int i = 0; i < taskButtons.size(); i++) {
            if (i == draggingTaskIndex) continue; // 跳过正在拖动的子任务
            taskButtons.get(i).render(poseStack, mouseX, mouseY, partialTicks);
        }

        // 渲染右侧建议面板
        this.hintPanel.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染开始行动按钮
        startButton.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染AI辅助排序按钮
        aiSortButton.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染拖动的子任务
        if (draggingTaskIndex != -1) {
            AdaptiveSubTaskModel draggingSubTask = subTasks.get(draggingTaskIndex);

            // 获取文字宽度
            int textWidth = Minecraft.getInstance().font.width(draggingSubTask.getTitle() + " (" + draggingSubTask.getEstimatedTime() + ")");

            // 调整拖动子任务文字的渲染位置，使其显示在鼠标左侧
            Minecraft.getInstance().font.draw(
                    poseStack,
                    draggingSubTask.getTitle() + " (" + draggingSubTask.getEstimatedTime() + ")",
                    mouseX - textWidth - 5, // 将文字向左偏移文字宽度和额外的 5 像素
                    mouseY - 5, // 文字相对于鼠标位置的 Y 坐标
                    0xFFFFFF // 白色文字
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查AI辅助排序按钮是否被点击
        if (aiSortButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 检查是否有任务按钮被点击
        for (Button taskButton : taskButtons) {
            if (taskButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        // 检查是否点击了开始行动按钮
        if (startButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int leftPanelWidth = (this.panelWidth * 3) / 5; // 左侧面板宽度

        // 如果鼠标在右侧面板，将滚动事件传递给建议面板
        if (mouseX >= this.x + leftPanelWidth) {
            return hintPanel.mouseScrolled(mouseX, mouseY, scroll);
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        // 暂时不需要实现旁白支持
    }
}