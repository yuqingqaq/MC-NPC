package gui.academic.writing;

import com.mojang.blaze3d.vertex.PoseStack;
import component.HintScrollPanel;
import controller.GameController;
import model.NPCModel;
import model.AdaptiveSubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import component.TextEditorWidget;
import system.TaskManager;
import system.UIScreenManager;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEditorScreen extends Screen {
    protected EditBox titleField;             // 标题输入框
    protected TextEditorWidget bodyField;     // 正文输入框
    protected EditBox questionField;          // 用户问题输入框
    protected Button getAdviceButton;         // 获取建议按钮
    protected Button saveButton;              // 保存按钮 (新增)
    protected HintScrollPanel advicePanel;    // 建议展示面板
    protected HintScrollPanel outcomePanel;   // 任务收获展示面板
    protected List<String> adviceHistory;     // GPT 返回的建议历史
    protected List<String> outcomeHistory;    // 子任务收获历史

    protected boolean showingAdvicePanel = true; // 当前显示的是建议面板还是收获面板

    protected NPCModel currentNPC;            // 当前 NPC 对象

    // 要保存到的子任务标题 (新增)
    protected String targetSubTaskTitle;

    // 定义导航栏的区域
    protected int outcomesTabX;
    protected int adviceTabX;
    protected int tabY;
    protected int tabWidth;
    protected int tabHeight;

    public AbstractEditorScreen(NPCModel npc, String screenTitle) {
        super(new TextComponent(screenTitle));
        this.currentNPC = npc;
        this.adviceHistory = new ArrayList<>();
        this.outcomeHistory = new ArrayList<>();
    }

    // 增加一个可以设置目标子任务的构造函数
    public AbstractEditorScreen(NPCModel npc, String screenTitle, String targetSubTaskTitle) {
        this(npc, screenTitle);
        this.targetSubTaskTitle = targetSubTaskTitle;
    }

    @Override
    protected void init() {
        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        int centerY = this.height / 2;

        // 屏幕宽度三等分
        int leftPanelWidth = (this.width * 3) / 5;
        int rightPanelWidth = (this.width * 2) / 5;

        // 导航栏设置
        tabWidth = rightPanelWidth / 2;
        tabHeight = 20;
        tabY = 10;
        // 整体向左移动10像素，并且调换顺序
        outcomesTabX = this.width - rightPanelWidth - 10;
        adviceTabX = outcomesTabX + tabWidth;

        // 标题输入框
        this.titleField = new EditBox(this.font, 20, centerY - 100, leftPanelWidth - 40, 20, new TextComponent(getTitlePlaceholder()));
        this.addWidget(this.titleField);

        // 正文输入框
        this.bodyField = new TextEditorWidget(20, centerY - 70, leftPanelWidth - 40, 135);
        this.addRenderableWidget(this.bodyField);

        // 添加保存按钮 (新增)
        this.saveButton = this.addRenderableWidget(new Button(
                leftPanelWidth - 100,
                centerY + 70,  // 放在正文输入框下方，问题输入框上方
                80,
                20,
                new TextComponent("保存"),
                button -> saveContent()
        ));

        // 用户问题输入框
        this.questionField = new EditBox(this.font, 40, centerY + 90, leftPanelWidth - 30, 20, new TextComponent(getQuestionPlaceholder()));
        this.addWidget(this.questionField);

        // 获取建议按钮 (保持不变)
        this.getAdviceButton = this.addRenderableWidget(new Button(
                leftPanelWidth + 20,
                centerY + 90,
                100,
                20,
                new TextComponent("获取写作建议"),
                button -> generateAdvice()
        ));

        // 建议展示面板和收获展示面板的共同配置
        Minecraft mc = Minecraft.getInstance();
        int panelHeight = this.height - 55;
        int panelTop = tabY + tabHeight + 5; // 导航栏下方留些空间
        int panelLeft = outcomesTabX; // 面板与导航栏左对齐
        int panelBorder = 5;
        int scrollBarWidth = 5;

        // 建议展示面板
        this.advicePanel = new HintScrollPanel(mc, rightPanelWidth, panelHeight, panelTop, panelLeft, panelBorder, scrollBarWidth, adviceHistory);

        // 收获展示面板
        this.outcomePanel = new HintScrollPanel(mc, rightPanelWidth, panelHeight, panelTop, panelLeft, panelBorder, scrollBarWidth, outcomeHistory);

        // 初始加载子任务收获数据
        loadSubTaskOutcomes();

        // 添加关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 5, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));

        // 如果有目标子任务，加载已有内容 (新增)
        if (targetSubTaskTitle != null) {
            loadExistingOutcome();
        }
    }

    // 加载已有的outcome到编辑器 (新增)
    protected void loadExistingOutcome() {
        AdaptiveSubTaskModel subTask = TaskManager.getInstance().findSubTaskByTitle(targetSubTaskTitle);
        if (subTask != null) {
            String outcome = subTask.getOutcome();
            if (outcome != null && !outcome.isEmpty()) {
                bodyField.setText(outcome);

                // 如果有标题格式，尝试提取标题
                if (outcome.startsWith("标题： ")) {
                    int endOfTitle = outcome.indexOf("\n\n");
                    if (endOfTitle > 7) { // "Title: ".length() = 7
                        String title = outcome.substring(7, endOfTitle);
                        titleField.setValue(title);

                        // 提取正文
                        if (outcome.length() > endOfTitle + 2) {
                            String body = outcome.substring(endOfTitle + 2);
                            bodyField.setText(body);
                        }
                    }
                }
            }
        }
    }

    // 保存内容的方法 (新增)
    protected void saveContent() {
        if (targetSubTaskTitle == null || targetSubTaskTitle.isEmpty()) {
            // 如果没有设置目标子任务，只保存内容，不完成任务
            saveContentOnly();
        } else {
            // 如果有目标子任务，保存内容并完成任务
            saveContentAndCompleteTask();
        }
    }

    // 只保存内容，不完成任务 (新增)
    protected void saveContentOnly() {
        String title = this.titleField.getValue().trim();
        String body = this.bodyField.getText().trim();

        if (body.isEmpty()) {
            Minecraft.getInstance().player.displayClientMessage(
                    new TextComponent("内容为空！"), true
            );
            return;
        }

        // 构建要保存的内容
        StringBuilder contentBuilder = new StringBuilder();
        if (!title.isEmpty()) {
            contentBuilder.append("Title: ").append(title).append("\n\n");
        }
        contentBuilder.append(body);

        // 可以在这里实现自定义的保存逻辑
        Minecraft.getInstance().player.displayClientMessage(
                new TextComponent("Content saved."), false
        );
    }

    // 保存内容并完成任务 (新增)
    protected void saveContentAndCompleteTask() {
        String title = this.titleField.getValue().trim();
        String body = this.bodyField.getText().trim();

        if (body.isEmpty()) {
            Minecraft.getInstance().player.displayClientMessage(
                    new TextComponent("Content is empty! Please write something before saving."), true
            );
            return;
        }

        // 构建要保存的内容
        StringBuilder contentBuilder = new StringBuilder();
        if (!title.isEmpty()) {
            contentBuilder.append("Title: ").append(title).append("\n\n");
        }
        contentBuilder.append(body);

        String content = contentBuilder.toString();

        // 查找目标子任务
        AdaptiveSubTaskModel subTask = TaskManager.getInstance().findSubTaskByTitle(targetSubTaskTitle);

        if (subTask != null) {
            // 保存内容到子任务
            subTask.setOutcome(content);

            // 将子任务标记为完成
            TaskManager.getInstance().completeSubTask(subTask);

            Minecraft.getInstance().player.displayClientMessage(
                    new TextComponent("Content saved and task completed: " + targetSubTaskTitle), false
            );

            // 刷新任务面板
            loadSubTaskOutcomes();
        } else {
            Minecraft.getInstance().player.displayClientMessage(
                    new TextComponent("Could not find task: " + targetSubTaskTitle), true
            );
        }
    }

    private void loadSubTaskOutcomes() {
        outcomeHistory.clear(); // 清空之前的内容

        StringBuilder outcomesBuilder = getSubTaskOutcomes(true); // 排除当前编辑的子任务
        if (outcomesBuilder.length() > 0) {
            // 将构建器的内容按行分割，添加到历史记录中
            String[] lines = outcomesBuilder.toString().split("\n");
            for (String line : lines) {
                outcomeHistory.add(line);
            }
        }

        this.outcomePanel.refreshPanel();
    }

    // 获取子任务列表
    protected List<AdaptiveSubTaskModel> getSubTasks() {
        // 从TaskManager获取当前任务的子任务
        System.out.println("TaskManager.getInstance().getCurrentActiveTask() " + TaskManager.getInstance().getCurrentActiveTask());
        if (TaskManager.getInstance().getCurrentActiveTask() != null) {
            System.out.println("TaskManager.getInstance().getCurrentActiveTask().getTitle() " + TaskManager.getInstance().getCurrentActiveTask().getTitle());
            return TaskManager.getInstance().getCurrentActiveTask().getSubTasks();
        }
        return new ArrayList<>();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);

    }
    /**
     * 获取所有已完成子任务的outcomes（可选排除当前编辑的子任务）
     * @param excludeCurrentTask 是否排除当前正在编辑的子任务
     * @return 包含子任务标题和outcome的字符串构建器
     */
    protected StringBuilder getSubTaskOutcomes(boolean excludeCurrentTask) {
        StringBuilder builder = new StringBuilder();
        List<AdaptiveSubTaskModel> subTasks = getSubTasks();
        boolean hasOutcomes = false;

        if (subTasks != null && !subTasks.isEmpty()) {
            for (AdaptiveSubTaskModel subTask : subTasks) {
                // 如果需要排除当前任务，且当前子任务是正在编辑的，则跳过
                if (excludeCurrentTask && targetSubTaskTitle != null &&
                        targetSubTaskTitle.equals(subTask.getTitle())) {
                    continue;
                }

                String outcome = subTask.getOutcome();
                if (outcome != null && !outcome.isEmpty()) {
                    builder.append("任务: ").append(subTask.getTitle()).append("\n");
                    builder.append("任务成果: ").append(outcome).append("\n");
                    builder.append("----------------------").append("\n\n");
                    hasOutcomes = true;
                }
            }
        }

        // 如果没有outcomes，返回空构建器
        if (!hasOutcomes) {
            return new StringBuilder();
        }

        return builder;
    }

    private void generateAdvice() {
        String title = this.titleField.getValue().trim();
        String body = this.bodyField.getText().trim();
        String question = this.questionField.getValue().trim();

        if (!title.isEmpty() || !body.isEmpty() || !question.isEmpty()) {
            // 获取子任务outcomes作为参考
            StringBuilder referenceBuilder = getSubTaskOutcomes(true); // 排除当前编辑的子任务

            String markdownContent = buildPlainTextContent(title, body, question);
            String chatHistoryMarkdown = "```\n" + currentNPC.getChatHistoryAsString() + "\n```";

            // 组合完整内容
            String fullContent = chatHistoryMarkdown;
            if (referenceBuilder.length() > 0) {
                fullContent += "\n\n前置任务成果:\n" + referenceBuilder.toString();
            }
            fullContent += "\n" + markdownContent;

            String advice = GameController.getInstance().interactWithExpert(currentNPC, fullContent);

            adviceHistory.add(advice);
            adviceHistory.add("----------------------");
            adviceHistory.add("");

            this.advicePanel.refreshPanel();
        }
    }

    private String buildPlainTextContent(String title, String body, String question) {
        StringBuilder builder = new StringBuilder();
        if (!title.isEmpty()) {
            builder.append("Title: ").append(title).append("\n\n");
        }
        if (!body.isEmpty()) {
            builder.append("Body: \n").append(body).append("\n\n");
        }
        if (!question.isEmpty()) {
            builder.append("Question: ").append(question).append("\n");
        }
        return builder.toString();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 绘制导航栏背景
        fill(poseStack, outcomesTabX, tabY, outcomesTabX + tabWidth, tabY + tabHeight, 0x80000000);
        fill(poseStack, adviceTabX, tabY, adviceTabX + tabWidth, tabY + tabHeight, 0x80000000);

        // 绘制选中的导航栏
        if (showingAdvicePanel) {
            fill(poseStack, adviceTabX, tabY, adviceTabX + tabWidth, tabY + tabHeight, 0x80404040);
        } else {
            fill(poseStack, outcomesTabX, tabY, outcomesTabX + tabWidth, tabY + tabHeight, 0x80404040);
        }

        // 绘制导航栏文字 - 调换位置
        drawCenteredString(poseStack, this.font, "成果", outcomesTabX + tabWidth/2, tabY + 6, showingAdvicePanel ? 0xAAAAAA : 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "建议", adviceTabX + tabWidth/2, tabY + 6, showingAdvicePanel ? 0xFFFFFF : 0xAAAAAA);

        super.render(poseStack, mouseX, mouseY, partialTicks);

        this.titleField.render(poseStack, mouseX, mouseY, partialTicks);
        this.questionField.render(poseStack, mouseX, mouseY, partialTicks);
        this.bodyField.render(poseStack, mouseX, mouseY, partialTicks);

        // 根据当前状态渲染相应的面板
        if (showingAdvicePanel) {
            this.advicePanel.render(poseStack, mouseX, mouseY, partialTicks);
        } else {
            this.outcomePanel.render(poseStack, mouseX, mouseY, partialTicks);
        }

        drawCenteredString(poseStack, this.font, getScreenTitle(), this.width / 2 - 100, 5, 0xFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (showingAdvicePanel) {
            return this.advicePanel.mouseScrolled(mouseX, mouseY, scroll);
        } else {
            return this.outcomePanel.mouseScrolled(mouseX, mouseY, scroll);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了导航栏
        if (mouseY >= tabY && mouseY <= tabY + tabHeight) {
            if (mouseX >= outcomesTabX && mouseX <= outcomesTabX + tabWidth) {
                showingAdvicePanel = false;
                return true;
            } else if (mouseX >= adviceTabX && mouseX <= adviceTabX + tabWidth) {
                showingAdvicePanel = true;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // 刷新子任务信息（如果任务状态变化时调用）
    public void refreshTaskOutcomes() {
        loadSubTaskOutcomes();
    }

    // 设置目标子任务 (新增)
    public void setTargetSubTaskTitle(String targetSubTaskTitle) {
        this.targetSubTaskTitle = targetSubTaskTitle;
        if (targetSubTaskTitle != null) {
            loadExistingOutcome();
        }
    }

    // 获取屏幕标题
    protected abstract String getScreenTitle();

    // 获取标题输入框的占位符
    protected abstract String getTitlePlaceholder();

    // 获取问题输入框的占位符
    protected abstract String getQuestionPlaceholder();
}