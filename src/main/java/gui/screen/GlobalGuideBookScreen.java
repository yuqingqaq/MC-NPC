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
    private int currentTab = 1;  // 0=校园导览, 1=SRL任务, 2=游戏控制

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
        
        // 获取任务列表
        List<AdaptiveTaskModel> tasks = taskManager.getTasks();
        if (!tasks.isEmpty()) {
            // 尝试找到"Agent研究"任务，如果没有则使用第一个任务
            this.currentSRLTask = tasks.stream()
                    .filter(task -> "Agent研究".equals(task.getTitle()))
                    .findFirst()
                    .orElse(tasks.get(0));

            // 初始化子任务列表
            if (currentSRLTask != null) {
                // 如果SRLQuest可用，使用它的排序逻辑
                if (srlQuestAvailable) {
                    this.sortedSubTasks = taskManager.getSortedSubTasks(currentSRLTask);
                } else {
                    // 如果SRLQuest不可用，直接使用任务的子任务列表
                    this.sortedSubTasks = new ArrayList<>(currentSRLTask.getSubTasks());
                }
            }
        }

        // 如果SRLQuest可用，获取SRL阶段管理器
            this.srlStageManager = GameController.getInstance().getSRLStageManager();
            if (srlStageManager != null) {
                this.currentSRLStage = srlStageManager.getCurrentStage();
            
        }
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);
        
        // 根据SRLQuest是否可用获取排序后的任务列表
        if (currentSRLTask != null) {
            this.sortedSubTasks = taskManager.getSortedSubTasks(currentSRLTask);
            
            
            // 确保selectedTaskIndex在有效范围内
            if (selectedTaskIndex < 0 || selectedTaskIndex >= sortedSubTasks.size()) {
                // 找到第一个未完成的任务作为默认选中
                for (int i = 0; i < sortedSubTasks.size(); i++) {
                    if (sortedSubTasks.get(i).getStatus() != AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                        selectedTaskIndex = i;
                        break;
                    }
                }
                // 如果所有任务都已完成或没有任务，选择第一个
                if (selectedTaskIndex < 0 && !sortedSubTasks.isEmpty()) {
                    selectedTaskIndex = 0;
                }
            }
        }
        
        // 跳过已完成的校园导览阶段
        if (stageManager.isStageCompleted("INTRO") &&
                currentDisplayStage != null &&
                "INTRO".equals(currentDisplayStage.getId())) {
            currentDisplayStage = determineLastCompletedStage();
        }

        // 如果SRL任务可用，跳过已完成的SRL阶段
        if (srlStageManager != null && currentSRLStage != null &&
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
  

    private void initSRLTaskButtons() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        // 如果SRLQuest可用且当前是SRL的INTRO或END阶段，添加相应的导航按钮
        if (srlStageManager != null && currentSRLStage != null && 
                ("INTRO".equals(currentSRLStage.getId()) || "END".equals(currentSRLStage.getId()))) {
            
            List<SRLStageInfo> stages = srlStageManager.getAllStages();
            int currentIndex = stages.indexOf(currentSRLStage);
            
            // 左导航按钮
            if (currentIndex > 0) {
                final int prevIndex = currentIndex - 1;
                this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight,
                        new TextComponent("< 上一页"), button -> {
                    currentSRLStage = stages.get(prevIndex);
                    this.init();
                }));
            }

            // 右导航按钮
            if (currentIndex < stages.size() - 1) {
                final int nextIndex = currentIndex + 1;
                this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight,
                        new TextComponent("下一页 >"), button -> {
                    
                    if ("INTRO".equals(currentSRLStage.getId())) {
                        srlStageManager.markStageCompleted("INTRO");
                    }
                    currentSRLStage = stages.get(nextIndex);
                    this.init();
                }));
            }
            
            // 如果是INTRO阶段，添加开始学习按钮
            if ("INTRO".equals(currentSRLStage.getId())) {
                this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 30, 100, buttonHeight,
                        new TextComponent("开始学习"), button -> {
                    // 跳过INTRO阶段，进入第一个非INTRO的学习阶段
                    for (SRLStageInfo stage : stages) {
                        if (!"INTRO".equals(stage.getId()) && !"END".equals(stage.getId())) {
                            currentSRLStage = stage;
                            break;
                        }
                    }
                    srlStageManager.markStageCompleted("INTRO");
                    this.init();
                }));
            }
            
            return;
        }

        // 使用taskManager.getSortedSubTasks获取的顺序渲染按钮
        if (currentSRLTask != null && !sortedSubTasks.isEmpty()) {
            // 计算当前显示的任务索引
            if (selectedTaskIndex < 0) selectedTaskIndex = 0;
            
            // 如果有上一个任务，添加左导航按钮
            if (selectedTaskIndex > 0) {
                final int prevIndex = selectedTaskIndex - 1;
                this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight,
                        new TextComponent("< 上一任务"), button -> {
                    selectedTaskIndex = prevIndex;
                    this.init();
                }));
            }

            // 如果有下一个任务，添加右导航按钮
            if (selectedTaskIndex < sortedSubTasks.size() - 1) {
                final int nextIndex = selectedTaskIndex + 1;
                this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight,
                        new TextComponent("下一任务 >"), button -> {
                    selectedTaskIndex = nextIndex;
                    this.init();
                }));
            }
        }

        // 如果选中了专家访谈或写作指导任务，添加"前往任务"按钮
        if (currentSRLTask != null && selectedTaskIndex >= 0 && selectedTaskIndex < sortedSubTasks.size()) {
            AdaptiveSubTaskModel selectedSubTask = sortedSubTasks.get(selectedTaskIndex);
            String taskTitle = selectedSubTask.getTitle();

            // 只为特定任务提供前往按钮
            if ("专家访谈".equals(taskTitle) || "找到论文写作辅导员".equals(taskTitle)) {
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
        } else if ("找到论文写作辅导员".equals(taskTitle)) {
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



    private void renderSRLTasks(PoseStack poseStack) {
        yOffset = 35;

        if (currentSRLTask == null) {
            drawCenteredString(poseStack, this.font, "没有可用的SRL任务",
                    this.width / 2, yOffset, 0xFFFF5555);
            return;
        }

        // 如果当前是SRL的INTRO或END阶段，显示相应内容
        if (srlStageManager != null && currentSRLStage != null && 
                ("INTRO".equals(currentSRLStage.getId()) || "END".equals(currentSRLStage.getId()))) {
            // 渲染阶段标题
            drawCenteredString(poseStack, this.font, currentSRLStage.getTitle(),
                    this.width / 2, yOffset, 0xFFFFAA00);
            yOffset += 20;

            // 渲染阶段内容
            String content = currentSRLStage.getContent();
            List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);
            for (ColoredText line : contentLines) {
                drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
                yOffset += 10;
            }
            
            // 如果是END阶段且有outcome，显示outcome
            if ("END".equals(currentSRLStage.getId()) && 
                    currentSRLStage.getOutcome() != null && 
                    !currentSRLStage.getOutcome().isEmpty()) {
                yOffset += 15;
                renderGenericOutcome(poseStack, currentSRLStage.getOutcome());
            }
            
            return;
        }

        // 只有当SRLQuest可用且任务列表为空时，才显示策略提示
        if (sortedSubTasks.isEmpty() && srlQuestAvailable) {
            String message = "需要先制定学习策略！";
            String hint = "请打开 SRLQuest 的策略面板进行任务规划。";
 
            drawCenteredString(poseStack, this.font, message,  this.width / 2, height / 2 - 20, 0xFFFFAA00);
            drawCenteredString(poseStack, this.font, hint,  this.width / 2, height / 2, 0xFFAAAAAA);

            return;
        } 
        // 如果SRLQuest不可用，但任务列表为空，显示一般提示
        else if (sortedSubTasks.isEmpty()) {
            drawCenteredString(poseStack, this.font, "暂无可用任务",
                    this.width / 2, height / 2 - 10, 0xFFAAAAAA);
            return;
        }

        // 绘制主任务标题
        drawCenteredString(poseStack, this.font, currentSRLTask.getTitle(),
                this.width / 2 - 30, yOffset, 0xFFFFAA00);

        // 渲染任务进度情况
        int completedTasks = 0;
        for (AdaptiveSubTaskModel subTask : sortedSubTasks) {
            if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                completedTasks++;
            }
        }
        String progressText = String.format("进度: %d/%d", completedTasks, sortedSubTasks.size());
        drawCenteredString(poseStack, this.font, progressText,
                this.width / 2 + 30, yOffset, 0xFFFFFFFF);
        yOffset += 20;

        // 渲染当前选中的子任务
        if (!sortedSubTasks.isEmpty()) {
            // 确保selectedTaskIndex有效
            if (selectedTaskIndex < 0 || selectedTaskIndex >= sortedSubTasks.size()) {
                selectedTaskIndex = 0;
            }
            
            // 渲染当前选中的子任务
            renderSelectedSubTask(poseStack, sortedSubTasks.get(selectedTaskIndex));
        }
    }
    
    // 新方法：渲染选中的子任务
    private void renderSelectedSubTask(PoseStack poseStack, AdaptiveSubTaskModel subTask) {

        // 绘制任务图标
        ResourceLocation icon = subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED ? COMPLETED : PENDING;
        RenderSystem.setShaderTexture(0, icon);
        blit(poseStack, this.width / 2 - 140, yOffset - 4, 0, 0, 16, 16, 16, 16);

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

        drawString(poseStack, this.font, subTask.getTitle(), this.width / 2 - 120, yOffset, statusColor);
        yOffset += 20;

        // 查找对应的SRL阶段
        SRLStageInfo relatedStage = null;
        if (srlStageManager != null) {
            List<SRLStageInfo> stages = srlStageManager.getAllStages();
            for (SRLStageInfo stage : stages) {
                if (subTask.getSubTaskId().equals(stage.getRelatedTaskId())) {
                    relatedStage = stage;
                    break;
                }
            }
        }

        // 如果找到关联的阶段，显示阶段内容
        if (relatedStage != null) {
            
            // 渲染阶段内容
            String content = relatedStage.getContent();
            List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);
            for (ColoredText line : contentLines) {
                drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
                yOffset += 10;
            }
            yOffset += 10;
        } else {
            // 如果没有关联阶段，显示任务描述
            List<ColoredText> descLines = TextUtils.wrapText(subTask.getDescription(), (int) (this.width / 1.3f), true);
            for (ColoredText line : descLines) {
                drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
                yOffset += 10;
            }
            yOffset += 10;
        }

        
        // 如果任务已完成，显示任务成果
        if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
            renderSubTaskCompletionContent(poseStack, subTask, relatedStage);
        }
        else{
            // 显示任务对应的Hint内容
            renderTaskHint(poseStack, subTask, relatedStage);
        }

    }
    
    // 新方法：根据当前任务和关联阶段渲染提示内容
    private void renderTaskHint(PoseStack poseStack, AdaptiveSubTaskModel subTask, SRLStageInfo relatedStage) {
        // 如果有关联阶段，使用阶段信息来确定提示内容
        String hintTitle;
        String hintContent;
        int hintColor = 0xFFFFAA00;
        
        if (relatedStage != null) {
            // 根据阶段ID或顺序来确定合适的提示内容
            switch (relatedStage.getId()) {
                case "KNOWLEDGE_MAP":
                    hintTitle = "在当前场景中找到需要学习的知识海报，右键点击海报学习";
                    hintContent = "阅读海报后会获得题目，右键地面即可作答；物品栏中有知识图谱总览，可以随时查看你的学习进度！";
                    break;
                    
                case "LITERATURE_EXPEL":
                case "LITERATURE_PROMPT":
                    hintTitle = "在场景中找到需要学习的文献，右键点击地面即可打开阅读界面，";
                    hintContent = "编辑完成后记得保存哦！";
                    break;
                case "EXPERT_INTERVIEW":
                    hintTitle = "找到王教授，跟他聊聊你的报告选题和想法吧！";
                    hintContent = "右键点击王教授即可开始对话，记得保存你们的对话内容哦！";
                    break;
                case "WRITING_GUIDANCE":
                    hintTitle = "找到论文写作辅导员";
                    hintContent = "可以使用传送功能哦！";
                    break;
                case "REPORT_OUTLINE":
                    hintTitle = "找到论文写作大师，在那里完成你的报告大纲吧！";
                    hintContent = "写作中可以随时向大师提问，大师会根据你的进度给出建议，记得保存你的写作内容哦！";
                    break;
                    
                case "END":
                    hintTitle = "任务完成！";
                    hintContent = "恭喜你完成了所有Agent研究任务！";
                    hintColor = 0xFF55FF55;
                    break;
                    
                default:
                    // 默认提示
                    hintTitle = "Agent研究任务";
                    hintContent = "按照指引完成各项任务，逐步掌握Agent领域的知识和技能。";
            }
        } else {
            // 如果没有关联阶段，根据任务标题或ID来确定提示内容
            switch (subTask.getTitle()) {
                case "完成知识图谱学习":
                    hintTitle = "在当前场景中找到需要学习的知识海报，右键点击海报学习";
                    hintContent = "阅读海报后会获得题目，右键地面即可作答；物品栏中有知识图谱总览，可以随时查看你的学习进度！"; 
                    break;
                    
                case "文献搜集-ExpeL":
                case "文献搜集-Agent Prompt":
                    hintTitle = "在场景中找到需要学习的文献，右键点击地面即可打开阅读界面，";
                    hintContent = "编辑完成后记得保存哦！";
                    break;
                    
                case "专家访谈":
                    hintTitle = "找到王教授，跟他聊聊你的报告选题和想法吧！";
                    hintContent = "右键点击王教授即可开始对话，记得保存你们的对话内容哦！";
                    break;
                case "找到论文写作辅导员":
                    hintTitle = "找到论文写作辅导员";
                    hintContent = "可以使用传送功能哦！";
                    break;
                    
                case "报告大纲撰写":
                    hintTitle = "找到论文写作大师，在那里完成你的报告大纲吧！";
                    hintContent = "写作中可以随时向大师提问，大师会根据你的进度给出建议，记得保存你的写作内容哦！";
                    break;
                    
                default:
                    // 默认提示
                    hintTitle = "Agent研究任务";
                    hintContent = "按照指引完成各项任务，逐步掌握Agent领域的知识和技能。";
            }
        }
        
        // 渲染提示标题
        drawString(poseStack, this.font, "提示: " + hintTitle, this.width / 2 - 120, yOffset, hintColor);
        yOffset += 15;

        // 渲染提示内容
        List<ColoredText> hintLines = TextUtils.wrapText(hintContent, (int) (this.width / 1.3f), true);
        for (ColoredText line : hintLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, 0xFFAAAAAA);
            yOffset += 10;
        }
        yOffset += 5;
    }
    
    // 新方法：渲染子任务完成内容
    private void renderSubTaskCompletionContent(PoseStack poseStack, AdaptiveSubTaskModel subTask, SRLStageInfo relatedStage) {
        yOffset += 10;
        
        // 1. 首先显示子任务的outcome

        
        String taskOutcome = subTask.getOutcome();
        if (taskOutcome != null && !taskOutcome.isEmpty()) {
            // 使用通用渲染函数显示子任务outcome
            drawString(poseStack, this.font, "任务成果:", this.width / 2 - 120, yOffset, 0xFF55FF55);
            yOffset += 15;
            renderGenericOutcome(poseStack, taskOutcome, 0xFFAAAAAA);
        } else {
            drawString(poseStack, this.font, "任务已完成", this.width / 2 - 120, yOffset, 0xFF55FF55);
            yOffset += 15;
        }
        
        // 2. 然后显示阶段的outcome（如果有）
        if (relatedStage != null && relatedStage.getOutcome() != null && !relatedStage.getOutcome().isEmpty()) {
            // 添加分隔线
            yOffset += 5;
            fill(poseStack, this.width / 2 - 120, yOffset, this.width / 2 + 120, yOffset + 1, 0x55FFFFFF);
            yOffset += 10;
            
            drawString(poseStack, this.font, "阶段成果:", this.width / 2 - 120, yOffset, 0xFFFFAA00);
            yOffset += 15;
            
            // 使用通用渲染函数显示阶段outcome
            renderGenericOutcome(poseStack, relatedStage.getOutcome());
        }
    }
    
    // 通用的outcome渲染函数，可以指定文本颜色
    private void renderGenericOutcome(PoseStack poseStack, String outcome, int textColor) {
        List<ColoredText> outcomeLines = TextUtils.wrapText(outcome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outcomeLines) {
            // 使用指定的颜色而不是line.color
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, textColor);
            yOffset += 10;
        }
    }
    
    // 通用的outcome渲染函数，使用文本自带颜色
    private void renderGenericOutcome(PoseStack poseStack, String outcome) {
        List<ColoredText> outcomeLines = TextUtils.wrapText(outcome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outcomeLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }
    }
    
    private void renderCompletionContent(PoseStack poseStack) {
        yOffset += 10;

        String outCome = currentDisplayStage.getOutcome();
        renderGenericOutcome(poseStack, outCome);
        yOffset += 10;
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
        if (srlStageManager != null) {
            // 将状态从"未访问"标记为"已访问"，但不标记为"已完成"
            // 这样在下次打开指南时，用户还能看到INTRO内容，但知道已经看过
            srlStageManager.markStageVisited("INTRO");
            
            // 重置当前选中的标签为SRL任务（便于下次打开）
            currentTab = 1;
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