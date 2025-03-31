package gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import controller.GameController;
import model.AdaptiveSubTaskModel;
import model.AdaptiveTaskModel;
import model.NPCModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import system.StageManager;
import system.StageManager.StageInfo;
import system.SRLStageManager;
import system.SRLStageManager.SRLStageInfo;
import system.TaskManager;
import system.UIScreenManager;
import system.UITaskManager;

import java.util.*;

public class GlobalGuideBookScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");
    private static final ResourceLocation PENDING = new ResourceLocation("npcopenai", "textures/item/to_do.png");

    // 预定义的NPC数据 [名称, ID]
    private static final String[][] NPC_DATA = {
            {"论文写作专家", "13"},
            {"王教授", "14"}
    };

    // 校园导览系统
    private final StageManager stageManager;
    private StageInfo currentDisplayStage;
    private static final Map<String, Boolean> VISITED_NPCS = new HashMap<>();

    // SRL教学系统相关状态
    private final TaskManager taskManager;
    private SRLStageManager srlStageManager;
    private SRLStageInfo currentSRLStage;
    private AdaptiveTaskModel currentSRLTask;
    private boolean srlQuestAvailable;  // 标记SRLQuest是否可用
    private int currentTab = 0;  // 0=校园导览, 1=SRL任务, 2=游戏控制

    // 界面状态
    private int yOffset = 0;
    private int selectedTaskIndex = -1;
    private List<AdaptiveSubTaskModel> sortedSubTasks = new ArrayList<>();

    // 按钮
    private Button campusTourButton;
    private Button srlTasksButton;
    private Button controlsButton;
    private Button findCoinButton;
    private Button goToTaskButton; // 仅用于前往任务地点，不进行完成操作

    public GlobalGuideBookScreen() {
        super(new TextComponent("游戏指南"));
        this.stageManager = GameController.getInstance().getStageManager();
        this.currentDisplayStage = stageManager.getCurrentStage();

        // 检测SRLQuest是否可用
        this.srlQuestAvailable = GameController.getInstance().isSRLQuestAvailable();

        // 无论SRLQuest是否可用，都初始化基本任务管理
        this.taskManager = TaskManager.getInstance();
        List<AdaptiveTaskModel> tasks = taskManager.getTasks();
        if (!tasks.isEmpty()) {
            this.currentSRLTask = tasks.stream()
                    .filter(task -> "Agent研究".equals(task.getTitle()))
                    .findFirst()
                    .orElse(tasks.get(0));

            // 获取任务列表，但如果有SRLQuest，排序可能由它处理
            this.sortedSubTasks = new ArrayList<>(currentSRLTask.getSubTasks());
        }

        // 如果SRLQuest可用，获取SRL阶段管理器和排序后的任务
        if (srlQuestAvailable) {
            this.srlStageManager = GameController.getInstance().getSRLStageManager();
            if (srlStageManager != null) {
                this.currentSRLStage = srlStageManager.getCurrentStage();
            }

            // 如果SRLQuest可用，获取它排序后的任务列表
            if (currentSRLTask != null) {
                this.sortedSubTasks = taskManager.getSortedSubTasks(currentSRLTask);
            }
        }
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        // 跳过已完成的校园导览阶段
        if (stageManager.isStageCompleted("INTRO") &&
                currentDisplayStage != null &&
                "INTRO".equals(currentDisplayStage.getId())) {
            currentDisplayStage = determineLastCompletedStage();
        }

        // 如果SRL任务可用，跳过已完成的SRL阶段
        if (srlQuestAvailable && srlStageManager != null && currentSRLStage != null &&
                "INTRO".equals(currentSRLStage.getId()) &&
                srlStageManager.isStageCompleted("INTRO")) {
            currentSRLStage = determineLastCompletedSRLStage();
        }

        // 添加顶部选项卡按钮 - 校园导览, SRL任务(中间), 游戏控制(右侧)
        int buttonWidth = width / 3 - 10;
        int buttonHeight = 20;

        campusTourButton = this.addRenderableWidget(new Button(5, 5, buttonWidth, buttonHeight,
                new TextComponent("校园导览"), button -> {
            currentTab = 0;
            this.init();
        }));

        srlTasksButton = this.addRenderableWidget(new Button(buttonWidth + 10, 5, buttonWidth, buttonHeight,
                new TextComponent("SRL任务"), button -> {
            currentTab = 1;
            this.init();
        }));

        controlsButton = this.addRenderableWidget(new Button(2 * buttonWidth + 15, 5, buttonWidth, buttonHeight,
                new TextComponent("游戏控制"), button -> {
            currentTab = 2;
            this.init();
        }));

        // 根据当前选项卡渲染不同内容的按钮
        if (currentTab == 0) {
            initCampusTourButtons();
        } else if (currentTab == 1) {
            initSRLTaskButtons();
        }

        // 关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 30, 20, 20,
                new TextComponent("X"), button -> onClose()));
    }

    private void initCampusTourButtons() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        // 获取所有阶段
        List<StageInfo> stages = stageManager.getAllStages();
        int currentIndex = stages.indexOf(currentDisplayStage);

        // 左导航按钮
        if (currentIndex > 0) {
            this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight,
                    new TextComponent("< 上一页"), button -> {
                currentDisplayStage = stages.get(currentIndex - 1);
                this.init();
            }));
        }

        // 右导航按钮
        if (currentIndex < stages.size() - 1) {
            this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight,
                    new TextComponent("下一页 >"), button -> {
                currentDisplayStage = stages.get(currentIndex + 1);
                this.init();
            }));
        }

        // 添加寻找金币按钮，但仅在非INTRO和非END阶段，并且玩家已经访问过该阶段的NPC时显示
        if (currentDisplayStage != null &&
                !"INTRO".equals(currentDisplayStage.getId()) &&
                !"END".equals(currentDisplayStage.getId()) &&
                Boolean.TRUE.equals(VISITED_NPCS.get(currentDisplayStage.getId())) &&
                currentDisplayStage.getCoinLocation() != null &&
                !currentDisplayStage.getCoinLocation().isEmpty()) {

            findCoinButton = this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 30, 100, buttonHeight,
                    new TextComponent("找碎片"), button -> {
                teleportToCoin(currentDisplayStage.getCoinLocation());
            }));
        }
    }

    private void initSRLTaskButtons() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        // 如果有SRL阶段管理，添加导航按钮
        if (srlQuestAvailable && srlStageManager != null && currentSRLStage != null) {
            List<SRLStageInfo> stages = srlStageManager.getAllStages();
            int currentIndex = stages.indexOf(currentSRLStage);

            // 左导航按钮
            if (currentIndex > 0) {
                this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight,
                        new TextComponent("< 上一页"), button -> {
                    currentSRLStage = stages.get(currentIndex - 1);
                    this.init();
                }));
            }

            // 右导航按钮
            if (currentIndex < stages.size() - 1) {
                this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight,
                        new TextComponent("下一页 >"), button -> {
                    currentSRLStage = stages.get(currentIndex + 1);
                    this.init();
                }));
            }
        }

        // 如果选中了专家访谈或写作指导任务，添加"前往任务"按钮
        if (currentSRLTask != null && selectedTaskIndex >= 0 && selectedTaskIndex < sortedSubTasks.size()) {
            AdaptiveSubTaskModel selectedSubTask = sortedSubTasks.get(selectedTaskIndex);
            String taskTitle = selectedSubTask.getTitle();

            // 只为特定任务提供前往按钮
            if ("专家访谈".equals(taskTitle) || "寻找论文写作辅导员".equals(taskTitle)) {
                String npcId = getNpcIdForTask(taskTitle);

                if (npcId != null) {
                    goToTaskButton = this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 30, 100, 20,
                            new TextComponent("前往任务"), button -> {
                        teleportToNPC(npcId);
                    }));
                }
            }
        }
    }

    // 根据任务标题获取对应的NPC ID
    private String getNpcIdForTask(String taskTitle) {
        if ("专家访谈".equals(taskTitle)) {
            return "14"; // 王教授的ID
        } else if ("寻找论文写作辅导员".equals(taskTitle)) {
            return "13"; // 论文写作专家的ID
        }
        return null;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);

        // 强调当前选中选项卡
        int selectedY = 5;
        int selectedHeight = 20;
        int selectedX;

        switch (currentTab) {
            case 0: // 校园导览
                selectedX = 5;
                break;
            case 1: // SRL任务
                selectedX = width / 3 + 5;
                break;
            case 2: // 游戏控制
                selectedX = 2 * width / 3 + 5;
                break;
            default:
                selectedX = 5;
        }

        int selectedWidth = width / 3 - 10;
        fill(poseStack, selectedX, selectedY + selectedHeight, selectedX + selectedWidth, selectedY + selectedHeight + 2, 0xFFFFAA00);

        // 根据当前选项卡渲染不同内容
        if (currentTab == 0) {
            renderCampusTour(poseStack);
        } else if (currentTab == 1) {
            renderSRLTasks(poseStack);
        } else if (currentTab == 2) {
            renderControls(poseStack);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderCampusTour(PoseStack poseStack) {
        yOffset = 35; // 初始化y偏移量(考虑顶部选项卡)

        if (currentDisplayStage == null) {
            drawCenteredString(poseStack, this.font, "当前没有阶段信息",
                    this.width / 2, yOffset, 0xFFFF0000);
            return;
        }

        // 渲染标题
        drawCenteredString(poseStack, this.font, currentDisplayStage.getTitle(),
                this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 20;

        // 渲染内容
        String content = currentDisplayStage.getContent();
        List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);

        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }

        yOffset += 10;

        // 如果当前阶段不是INTRO或END，渲染NPC信息
        if (!"INTRO".equals(currentDisplayStage.getId()) && !"END".equals(currentDisplayStage.getId())) {
            renderNPCInfo(poseStack);
        }

        // 如果当前阶段已完成，显示完成内容
        if (stageManager.isStageCompleted(currentDisplayStage.getId()) &&
                currentDisplayStage.getOutcome() != null &&
                !currentDisplayStage.getOutcome().isEmpty()) {

            renderCompletionContent(poseStack);
        }

        // 如果是最后一个阶段（END），添加学习任务提示
        if ("END".equals(currentDisplayStage.getId())) {
            // 检查是否所有阶段都已完成
            boolean allStagesCompleted = true;
            List<StageInfo> stages = stageManager.getAllStages();
            for (StageInfo stage : stages) {
                if (!stageManager.isStageCompleted(stage.getId())) {
                    allStagesCompleted = false;
                    break;
                }
            }

            if (allStagesCompleted) {
                yOffset += 15;
                drawString(poseStack, this.font, "恭喜你完成校园导览！现在你可以开始你的学习任务了。",
                        this.width / 2 - 170, yOffset, 0xFF55AAFF);
                yOffset += 15;
                drawString(poseStack, this.font, "点击上方的'SRL任务'标签探索Agent研究任务。",
                        this.width / 2 - 170, yOffset, 0xFF55AAFF);
            }
        }
    }

    private void renderControls(PoseStack poseStack) {
        yOffset = 35;

        // 渲染标题
        drawCenteredString(poseStack, this.font, "Minecraft 控制指南",
                this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 25;

        // 基本控制说明
        List<String[]> controls = Arrays.asList(
                new String[]{"移动", "W, A, S, D 键"},
                new String[]{"跳跃", "空格键"},
                new String[]{"潜行", "Shift 键"},
                new String[]{"打开物品栏", "E 键"},
                new String[]{"扔出物品", "Q 键"},
                new String[]{"与NPC互动", "右键点击"},
                new String[]{"切换第一/第三人称", "F5 键"},
                new String[]{"切换快捷栏物品", "鼠标滚轮/数字键"},
                new String[]{"打开/关闭任务界面", "选中任务书右键地面"}
        );

        // 渲染控制说明
        int leftColumn = this.width / 2 - 150;
        int rightColumn = this.width / 2 + 20;

        drawString(poseStack, this.font, "基本控制:", leftColumn, yOffset, 0xFFFFFFFF);
        yOffset += 15;

        for (String[] control : controls) {
            drawString(poseStack, this.font, control[0] + ":", leftColumn, yOffset, 0xFFFFFFFF);
            drawString(poseStack, this.font, control[1], rightColumn, yOffset, 0xFFAAAAAA);
            yOffset += 15;
        }

        yOffset += 10;
        drawString(poseStack, this.font, "游戏技巧:", leftColumn, yOffset, 0xFFFFFFFF);
        yOffset += 15;

        List<String> tips = Arrays.asList(
                "探索校园可以获得校徽碎片，收集所有碎片完成校园导览",
                "在SRL任务界面可以查看和管理你的学习任务"
        );

        for (String tip : tips) {
            List<ColoredText> tipLines = TextUtils.wrapText(tip, (int) (this.width / 1.5f), true);
            for (ColoredText line : tipLines) {
                drawString(poseStack, this.font, line.text, leftColumn, yOffset, 0xFFAAAAAA);
                yOffset += 15;
            }
        }
    }

    private void renderSRLTasks(PoseStack poseStack) {
        yOffset = 35;

        if (currentSRLTask == null) {
            drawCenteredString(poseStack, this.font, "没有可用的SRL任务",
                    this.width / 2, yOffset, 0xFFFF5555);
            return;
        }

        // 如果有SRL阶段管理器且启用了SRLQuest，使用导览模式
        if (srlQuestAvailable && srlStageManager != null && currentSRLStage != null) {
            renderSRLStageContent(poseStack);
        } else {
            // 否则使用基本任务列表模式
            renderBasicTaskView(poseStack);
        }
    }

    private void renderBasicTaskView(PoseStack poseStack) {
        // 渲染SRL任务标题
        drawCenteredString(poseStack, this.font, currentSRLTask.getTitle(),
                this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 20;

        // 渲染SRL任务描述
        List<ColoredText> descLines = TextUtils.wrapText(currentSRLTask.getDescription(), (int) (this.width / 1.3f), true);
        for (ColoredText line : descLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 150, yOffset, line.color);
            yOffset += 10;
        }
        yOffset += 10;

        // 渲染目标
        drawString(poseStack, this.font, "目标: " + currentSRLTask.getTarget(),
                this.width / 2 - 150, yOffset, 0xFFFFFFFF);
        yOffset += 20;

        // 渲染子任务列表
        drawString(poseStack, this.font, "子任务:", this.width / 2 - 150, yOffset, 0xFFFFFFFF);
        yOffset += 15;

        int completedTasks = 0;
        for (int i = 0; i < sortedSubTasks.size(); i++) {
            AdaptiveSubTaskModel subTask = sortedSubTasks.get(i);
            if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                completedTasks++;
            }

            // 绘制选中背景
            if (i == selectedTaskIndex) {
                fill(poseStack, this.width / 2 - 155, yOffset - 2, this.width / 2 + 155, yOffset + 12, 0x80AAAAAA);
            }

            // 绘制任务图标
            ResourceLocation icon = subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED ? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 150, yOffset - 4, 0, 0, 16, 16, 16, 16);

            // 绘制任务标题
            int statusColor;
            switch (subTask.getStatus()) {
                case COMPLETED:
                    statusColor = 0xFF55FF55; // 已完成:绿色
                    break;
                case IN_PROGRESS:
                    statusColor = 0xFFFFAA00; // 进行中:橙色
                    break;
                default:
                    statusColor = 0xFFFFFFFF; // 未开始:白色
            }

            drawString(poseStack, this.font, subTask.getTitle(), this.width / 2 - 130, yOffset, statusColor);

            // 为每个子任务添加点击区域
            int finalI = i;
            this.addRenderableWidget(new Button(this.width / 2 - 155, yOffset - 5, 310, 20, new TextComponent(""), button -> {
                selectedTaskIndex = finalI;
                this.init();
            }) {
                @Override
                public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
                    // 透明按钮，不需要绘制内容
                }
            });

            yOffset += 20;
        }

        // 显示整体任务进度
        yOffset += 10;
        String progressText = String.format("总进度: %d/%d", completedTasks, sortedSubTasks.size());
        drawString(poseStack, this.font, progressText, this.width / 2 - 150, yOffset, 0xFFFFFFFF);
        yOffset += 20;
    }

    // 渲染SRL阶段内容（需要SRLQuest支持）
    private void renderSRLStageContent(PoseStack poseStack) {
        // 渲染标题
        drawCenteredString(poseStack, this.font, currentSRLStage.getTitle(),
                this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 20;

        // 渲染内容
        String content = currentSRLStage.getContent();
        List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);

        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }

        yOffset += 10;

        // 如果不是INTRO或END阶段，显示与该阶段关联的任务
        if (!"INTRO".equals(currentSRLStage.getId()) && !"END".equals(currentSRLStage.getId())) {
            renderRelatedTask(poseStack);
        }

        // 如果当前阶段已完成，显示完成内容
        if (srlStageManager.isStageCompleted(currentSRLStage.getId()) &&
                currentSRLStage.getOutcome() != null &&
                !currentSRLStage.getOutcome().isEmpty()) {

            renderSRLCompletionContent(poseStack);
        }

        // 如果是END阶段，显示总结信息
        if ("END".equals(currentSRLStage.getId())) {
            renderSRLSummary(poseStack);
        }
    }

    // 渲染与当前SRL阶段关联的任务
    private void renderRelatedTask(PoseStack poseStack) {
        if (currentSRLTask == null || sortedSubTasks.isEmpty()) {
            return;
        }

        String taskId = currentSRLStage.getRelatedTaskId();
        if (taskId == null || taskId.isEmpty()) {
            return;
        }

        // 查找关联的子任务
        AdaptiveSubTaskModel relatedSubTask = null;
        for (AdaptiveSubTaskModel subTask : sortedSubTasks) {  // 使用排序后的子任务列表
            if (taskId.equals(subTask.getSubTaskId())) {
                relatedSubTask = subTask;
                break;
            }
        }

        if (relatedSubTask == null) {
            return;
        }

        // 渲染任务信息
        drawString(poseStack, this.font, "当前任务:", this.width / 2 - 120, yOffset, 0xFFFFFFFF);
        yOffset += 15;

        // 绘制任务图标
        ResourceLocation icon = relatedSubTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED ? COMPLETED : PENDING;
        RenderSystem.setShaderTexture(0, icon);
        blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);

        // 绘制任务标题
        int statusColor;
        switch (relatedSubTask.getStatus()) {
            case COMPLETED:
                statusColor = 0xFF55FF55; // 已完成:绿色
                break;
            case IN_PROGRESS:
                statusColor = 0xFFFFAA00; // 进行中:橙色
                break;
            default:
                statusColor = 0xFFFFFFFF; // 未开始:白色
        }

        drawString(poseStack, this.font, relatedSubTask.getTitle(), this.width / 2 - 100, yOffset, statusColor);
        yOffset += 20;

        // 绘制任务描述
        List<ColoredText> descLines = TextUtils.wrapText(relatedSubTask.getDescription(), (int) (this.width / 1.3f), true);
        for (ColoredText line : descLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, 0xFFAAAAAA);
            yOffset += 10;
        }
        yOffset += 10;

        // 显示任务状态
        String statusText = "状态: ";
        switch (relatedSubTask.getStatus()) {
            case COMPLETED:
                statusText += "已完成";
                statusColor = 0xFF55FF55;
                break;
            case IN_PROGRESS:
                statusText += "进行中";
                statusColor = 0xFFFFAA00;
                break;
            default:
                statusText += "未开始";
                statusColor = 0xFFFFFFFF;
        }
        drawString(poseStack, this.font, statusText, this.width / 2 - 120, yOffset, statusColor);
        yOffset += 15;

        // 提示如何完成任务
        drawString(poseStack, this.font, "提示: 使用SRLQuest任务书可以开始和完成此任务",
                this.width / 2 - 120, yOffset, 0xFF55AAFF);
        yOffset += 15;

        // 设置选中的任务为当前关联的任务
        int taskIndex = sortedSubTasks.indexOf(relatedSubTask);
        if (taskIndex != selectedTaskIndex) {
            selectedTaskIndex = taskIndex;
        }
    }

    // 渲染SRL阶段完成内容
    private void renderSRLCompletionContent(PoseStack poseStack) {
        yOffset += 10;
        drawString(poseStack, this.font, "任务成果:", this.width / 2 - 120, yOffset, 0xFF55FF55);
        yOffset += 15;

        String outCome = currentSRLStage.getOutcome();
        List<ColoredText> outComeLines = TextUtils.wrapText(outCome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outComeLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }
    }

    // 渲染SRL最终总结
    private void renderSRLSummary(PoseStack poseStack) {
        int completedTasks = 0;
        for (AdaptiveSubTaskModel subTask : sortedSubTasks) {
            if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                completedTasks++;
            }
        }

        // 显示任务完成情况
        yOffset += 10;
        String progressText = String.format("任务完成: %d/%d", completedTasks, sortedSubTasks.size());
        drawString(poseStack, this.font, progressText, this.width / 2 - 120, yOffset, 0xFFFFFFFF);
        yOffset += 20;

        // 如果全部完成，显示祝贺信息
        if (completedTasks == sortedSubTasks.size()) {
            drawString(poseStack, this.font, "恭喜你完成了所有Agent研究任务！",
                    this.width / 2 - 120, yOffset, 0xFF55FF55);
            yOffset += 15;
            drawString(poseStack, this.font, "你已经成功掌握了SRL和Agent领域的基础知识。",
                    this.width / 2 - 120, yOffset, 0xFF55FF55);
        } else {
            drawString(poseStack, this.font, "你已经完成了部分Agent研究任务。",
                    this.width / 2 - 120, yOffset, 0xFFFFAA00);
            yOffset += 15;
            drawString(poseStack, this.font, "继续努力，完成剩余的任务以获得更全面的学习体验。",
                    this.width / 2 - 120, yOffset, 0xFFFFAA00);
        }

        // 提示使用SRLQuest获取更详细的学习报告
        yOffset += 20;
        drawString(poseStack, this.font, "提示: 使用SRLQuest任务书可以查看详细的学习报告和反思",
                this.width / 2 - 120, yOffset, 0xFF55AAFF);
    }

    // 根据完成的任务数量和SRLQuest可用性提供不同的阶段提示
    private void renderTaskStageHint(PoseStack poseStack, int completedTasks) {
        String hintTitle;
        String hintContent;
        int hintColor = 0xFFFFAA00;

        if (completedTasks == 0) {
            hintTitle = "开始你的研究";
            if (srlQuestAvailable) {
                hintContent = "点击任务列表中的子任务，了解详情并使用SRLQuest开始你的第一个任务：知识图谱学习。";
            } else {
                hintContent = "从完成知识图谱学习开始，建立Agent领域的基础认知框架。";
            }
        } else if (completedTasks < 3) {
            hintTitle = "基础知识积累";
            if (srlQuestAvailable) {
                hintContent = "你正在积累基础知识。继续使用SRLQuest完成文献搜集任务，为后续研究打下基础。";
            } else {
                hintContent = "继续完成文献搜集任务，了解ExpeL和Agent Prompt相关知识。";
            }
        } else if (completedTasks < 5) {
            hintTitle = "深入探索";
            if (srlQuestAvailable) {
                hintContent = "是时候寻找专家意见了！找到王教授，进行专家访谈，获取领域专家的指导。";
            } else {
                hintContent = "寻找王教授和论文写作辅导员，获取专业指导和建议。";
            }
        } else if (completedTasks < sortedSubTasks.size()) {
            hintTitle = "总结与创新";
            if (srlQuestAvailable) {
                hintContent = "你已经收集了丰富的资料和见解，现在可以使用SRLQuest开始撰写报告大纲，整合你的发现。";
            } else {
                hintContent = "基于收集的资料和获得的指导，撰写一份结构清晰的报告大纲。";
            }
        } else {
            hintTitle = "任务完成！";
            hintContent = "恭喜你完成了所有Agent研究任务！你现在已经掌握了相关领域的基础知识和研究方法。";
            hintColor = 0xFF55FF55;
        }

        // 渲染提示标题
        drawString(poseStack, this.font, hintTitle, this.width / 2 - 150, yOffset, hintColor);
        yOffset += 15;

        // 渲染提示内容
        List<ColoredText> hintLines = TextUtils.wrapText(hintContent, (int) (this.width / 1.3f), true);
        for (ColoredText line : hintLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 150, yOffset, 0xFFFFFFFF);
            yOffset += 10;
        }
    }

    private void renderNPCInfo(PoseStack poseStack) {
        // 获取当前阶段关联的NPC位置列表
        String npcLocationsStr = currentDisplayStage.getNpcLocation();
        if (npcLocationsStr == null || npcLocationsStr.isEmpty()) {
            return;
        }

        // 分割多个NPC位置（如果有的话）
        List<String> npcLocations = Arrays.asList(npcLocationsStr.split(";"));
        boolean allTasksCompleted = true;

        // 获取所有NPC
        List<NPCModel> allNpcs = GameController.getInstance().getNpcs();

        // 遍历当前阶段的所有NPC位置
        for (String location : npcLocations) {
            // 查找该位置的NPC
            NPCModel npc = null;
            int npcIndex = -1;

            for (int i = 0; i < allNpcs.size(); i++) {
                if (allNpcs.get(i).getLocation().equalsIgnoreCase(location.trim())) {
                    npc = allNpcs.get(i);
                    npcIndex = i;
                    break;
                }
            }

            if (npc == null) {
                continue;  // 如果找不到对应的NPC，跳过
            }

            boolean npcTasksCompleted = npc.areAllTasksCompleted();

            drawString(poseStack, this.font, npc.getLocation(), this.width / 2 - 100, yOffset, 0xFFFFFF);

            ResourceLocation icon = npcTasksCompleted ? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);

            final int finalNpcIndex = npcIndex;
            this.addRenderableWidget(new Button(this.width / 2 + 90, yOffset - 5, 60, 20, new TextComponent("Go"), button -> {
                teleportToNPC(String.valueOf(finalNpcIndex));
                // 标记该阶段的NPC已访问
                VISITED_NPCS.put(currentDisplayStage.getId(), true);
            }));

            yOffset += 25;
            if (!npcTasksCompleted) {
                allTasksCompleted = false;
            }
        }
    }

    private void renderCompletionContent(PoseStack poseStack) {
        yOffset += 10;

        String outCome = currentDisplayStage.getOutcome();
        List<ColoredText> outComeLines = TextUtils.wrapText(outCome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outComeLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }
        yOffset += 10;
    }

    // 传送到指定ID的NPC
    private void teleportToNPC(String npcId) {
        if (this.minecraft.player != null) {
            this.minecraft.player.chat("/findnpc " + npcId);
            System.out.println("Teleporting to NPC: " + npcId);
            onClose();
        }
    }

    private void teleportToCoin(String locationStr) {
        if (this.minecraft.player != null) {
            String[] coords = locationStr.split(",");
            if (coords.length == 3) {
                try {
                    int x = Integer.parseInt(coords[0].trim()) + 5;
                    int y = Integer.parseInt(coords[1].trim());
                    int z = Integer.parseInt(coords[2].trim());

                    // 使用命令传送玩家到该阶段的金币位置
                    this.minecraft.player.chat("/tp @s " + x + " " + y + " " + z);
                    onClose(); // 关闭界面

                    // 显示提示信息
                    this.minecraft.player.displayClientMessage(
                            new TextComponent("已传送到金币位置"),
                            false
                    );
                } catch (NumberFormatException e) {
                    this.minecraft.player.displayClientMessage(
                            new TextComponent("坐标格式错误: " + locationStr),
                            false
                    );
                }
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        // 标记校园导览INTRO阶段为已完成
        stageManager.markStageCompleted("INTRO");

        // 如果SRL任务可用，并且SRL阶段管理器存在，标记SRL INTRO阶段为已访问
        if (srlQuestAvailable && srlStageManager != null) {
            srlStageManager.markStageVisited("INTRO");
        }

        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private StageInfo determineLastCompletedStage() {
        List<StageInfo> stages = stageManager.getAllStages();
        for (StageInfo stage : stages) {
            if (!stageManager.isStageCompleted(stage.getId())) {
                return stage;
            }
        }
        // 如果所有阶段都已完成，返回END阶段
        return stageManager.getStageById("END");
    }

    private SRLStageInfo determineLastCompletedSRLStage() {
        if (srlStageManager == null) return null;

        List<SRLStageInfo> stages = srlStageManager.getAllStages();
        for (SRLStageInfo stage : stages) {
            if (!srlStageManager.isStageCompleted(stage.getId())) {
                return stage;
            }
        }
        // 如果所有阶段都已完成，返回END阶段
        return srlStageManager.getStageById("END");
    }

    // 重置所有NPC访问状态（可用于测试）
    public static void resetAllVisitedStatus() {
        VISITED_NPCS.clear();
    }

    // 标记指定阶段的NPC已访问（可供外部调用）
    public static void markStageVisited(String stageId) {
        VISITED_NPCS.put(stageId, true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // 处理SRL任务列表的滚动
        if (currentTab == 1 && mouseY > 100) {
            // 这里可以添加SRL任务列表的滚动逻辑
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}