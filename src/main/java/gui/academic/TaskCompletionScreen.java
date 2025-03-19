package gui.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import component.academic.NavigationBar;
import component.academic.ScrollableTextBox;
import component.academic.NoteModule;
import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;
import model.NPCModel;
import system.UIScreenManager;
import system.TaskManager;
import controller.GameController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskCompletionScreen extends Screen {
    private final Minecraft minecraft;
    private AdaptiveTaskModel task;
    private NavigationBar navigationBar;
    private ScrollableTextBox outcomeBox;
    private ScrollableTextBox summaryBox;
    private NoteModule noteModule;
    private NPCModel summaryAgent; // 任务总结Agent

    public TaskCompletionScreen(Minecraft minecraft) {
        super(new TextComponent("任务完成情况"));
        this.minecraft = minecraft;
        this.task = task;
        System.out.println("TaskCompletionScreen");

        // 初始化任务总结Agent
        this.summaryAgent = createSummaryAgent();
    }

    @Override
    protected void init() {
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        // 检查任务是否为空
        if (this.task == null) {
            // 尝试从TaskManager获取当前任务
            this.task = TaskManager.getInstance().getCurrentTaskInOverview();

            if (this.task == null) {
                // 处理任务为空的情况
                UIScreenManager.getInstance().switchToTaskOverviewScreen();
                return;
            }
        }

        // 初始化导航栏
        List<String> strategies = task.getStrategies();
        this.navigationBar = new NavigationBar(minecraft, strategies, button -> {
            // 切换策略时的逻辑
            System.out.println("切换到策略：" + strategies.get(navigationBar.getSelectedIndex()));
        });

        // 初始化左侧面板，用于显示所有子任务的outcomes（只读）
        this.outcomeBox = new ScrollableTextBox(minecraft, 10, 60, this.width / 2 - 20, this.height - 90);

        // 载入所有子任务的outcomes
        loadSubTaskOutcomes();

        // 初始化右上角的任务总结文本框
        this.summaryBox = new ScrollableTextBox(minecraft, this.width / 2 + 10, 70, this.width / 2 - 20, this.height / 2 - 45);

        // 生成并设置任务总结
        generateTaskSummary();

        // 初始化笔记模块（可编辑）- 放在右下角
        this.noteModule = new NoteModule(minecraft, this.width / 2 + 10, this.height / 2 + 55, this.width / 2 - 20, this.height / 2 - 75);
        this.noteModule.setText(task.getNotes()); // 显示玩家保存的笔记
    }

    /**
     * 创建任务总结Agent
     */
    private NPCModel createSummaryAgent() {
        NPCModel npcModel = new NPCModel("任务总结Agent");
        return npcModel;
    }

    private void generateTaskSummary() {
        // 收集所有子任务的outcomes
        StringBuilder allOutcomes = new StringBuilder();
        allOutcomes.append("任务名称: ").append(task.getTitle()).append("\n\n");
        allOutcomes.append("任务描述: ").append(task.getDescription()).append("\n\n");
        allOutcomes.append("子任务完成内容:\n");

        List<AdaptiveSubTaskModel> subTasks = TaskManager.getInstance().getSortedSubTasks(task);
        boolean hasCompletedContent = false;

        if (subTasks != null && !subTasks.isEmpty()) {
            for (AdaptiveSubTaskModel subTask : subTasks) {
                String outcome = subTask.getOutcome();
                if (outcome != null && !outcome.isEmpty()) {
                    allOutcomes.append("- ").append(subTask.getTitle()).append(":\n");
                    allOutcomes.append(outcome).append("\n\n");
                    hasCompletedContent = true;
                }
            }
        }

        // 如果没有任务完成内容，添加提示信息
        if (!hasCompletedContent) {
            allOutcomes.append("暂无任务完成内容。\n\n");
        }

        // 使用任务总结Agent生成总结
        String prompt = "请基于以下任务完成情况，生成一个简洁明了的任务总结。总结应该包括任务的主要成果、关键发现和结论。如果任务暂无完成内容，请提供适当的初始阶段评估。\n\n" +
                allOutcomes.toString();

        String summary = GameController.getInstance().interactWithExpert(summaryAgent, prompt);

        // 设置到summaryBox
        this.summaryBox.setText(summary);

        // 创建Map并保存到任务模型中
        Map<String, String> summaryMap = new HashMap<>();
        summaryMap.put("总结", summary);
        task.setSummary(summaryMap);
    }

    /**
     * 从TaskManager获取当前任务的所有子任务outcomes并显示在左侧面板
     */
    private void loadSubTaskOutcomes() {
        StringBuilder outcomesBuilder = new StringBuilder();

        // 使用TaskManager中的getSortedSubTasks方法获取子任务列表
        List<AdaptiveSubTaskModel> subTasks = TaskManager.getInstance().getSortedSubTasks(task);
        boolean hasOutcomes = false;

        if (subTasks != null && !subTasks.isEmpty()) {
            for (AdaptiveSubTaskModel subTask : subTasks) {
                String outcome = subTask.getOutcome();
                if (outcome != null && !outcome.isEmpty()) {
                    outcomesBuilder.append("   ").append(subTask.getTitle()).append("\n\n");
                    outcomesBuilder.append(outcome).append("\n\n");
                    hasOutcomes = true;
                }
            }
        }

        if (!hasOutcomes) {
            outcomesBuilder.append("暂无任务完成内容。");
        }

        // 设置outcomes到左侧文本框
        this.outcomeBox.setText(outcomesBuilder.toString());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 渲染标题
        drawCenteredString(poseStack, this.font, "任务完成情况", this.width / 2, 10, 0xFFFFFF);

        // 渲染导航栏
        this.navigationBar.render(poseStack, 10, 40);

        // 渲染右上角总结标题
        drawString(poseStack, this.font, "任务总结", this.width / 2 + 10, 55, 0xFFFFFF);

        // 渲染右下角笔记标题
        drawString(poseStack, this.font, "笔记", this.width / 2 + 10, this.height / 2 + 40, 0xFFFFFF);

        // 渲染左侧outcomes文本框
        this.outcomeBox.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染右上角任务总结文本框
        this.summaryBox.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染右下角笔记模块
        this.noteModule.render(poseStack, mouseX, mouseY, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 处理导航栏的点击
        if (this.navigationBar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理左侧outcomes文本框的滚动和选择 (但不编辑)
        if (this.outcomeBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理右上角任务总结文本框
        if (this.summaryBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理笔记模块的交互
        if (this.noteModule.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // 处理左侧outcomes文本框的滚动
        if (mouseX < this.width / 2) {
            this.outcomeBox.mouseScrolled(mouseX, mouseY, delta);
            return true;
        } else {
            // 处理右侧区域的滚动
            if (mouseY < this.height / 2) {
                // 右上角任务总结
                this.summaryBox.mouseScrolled(mouseX, mouseY, delta);
            }
            return true;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // summaryBox是只读的

        // 处理笔记模块的键盘输入
        if (this.noteModule.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // summaryBox是只读的

        // 处理笔记模块的字符输入
        if (this.noteModule.charTyped(codePoint, modifiers)) {
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 界面不会暂停游戏
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.task != null) {
            // 保存玩家的笔记到任务模型中
            task.setNotes(this.noteModule.getText());
        }

        UIScreenManager.getInstance().switchToTaskOverviewScreen();
    }
}