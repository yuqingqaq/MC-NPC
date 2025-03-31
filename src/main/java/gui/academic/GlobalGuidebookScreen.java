package gui.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.gui.components.Button;
import system.TaskManager;
import controller.GameController;

import java.util.List;

public class GlobalGuidebookScreen extends Screen {
    private AdaptiveTaskModel currentTask;
    private int leftPanelWidth = 200;
    private int contentX;
    private Button startButton;

    public GlobalGuidebookScreen(AdaptiveTaskModel task) {
        super(new TextComponent("Global Guidebook"));
        this.currentTask = task;
    }

    @Override
    protected void init() {
        super.init();
        contentX = leftPanelWidth + 20;

        // 添加开始按钮
        if (canStartTask()) {
            startButton = addRenderableWidget(new Button(
                contentX, height - 40, 100, 20,
                new TextComponent("开始任务"),
                button -> startCurrentTask()
            ));
        }
    }

    private boolean canStartTask() {
        if (currentTask == null) return false;

        // 如果是SRL任务且未制定策略，不允许开始
        if (GameController.getInstance().isSRLQuestAvailable() && 
            !TaskManager.getInstance().isStrategyPlanned()) {
            return false;
        }

        // 检查是否有正在进行的子任务
        return !TaskManager.getInstance().hasInProgressSubTasks(currentTask);
    }

    private void startCurrentTask() {
        if (currentTask == null) return;

        List<AdaptiveSubTaskModel> subTasks = getVisibleSubTasks();
        if (!subTasks.isEmpty()) {
            AdaptiveSubTaskModel firstSubTask = subTasks.get(0);
            TaskManager.getInstance().startSubTask(firstSubTask, currentTask.getSubTasks());
            this.onClose(); // 关闭界面
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        
        // 渲染左侧面板背景
        fill(poseStack, 0, 0, leftPanelWidth, height, 0x80000000);
        
        // 渲染任务标题
        if (currentTask != null) {
            font.draw(poseStack, currentTask.getTitle(), contentX, 20, 0xFFFFFF);
            font.draw(poseStack, "描述: " + currentTask.getDescription(), contentX, 40, 0xAAAAAA);
            
            // SRL任务且未制定策略的情况
            if (GameController.getInstance().isSRLQuestAvailable() && 
                !TaskManager.getInstance().isStrategyPlanned()) {
                String message = "需要先制定学习策略！";
                String hint = "请打开 SRLQuest 进行任务规划。";
                font.draw(poseStack, message, contentX, 80, 0xFFFF0000);
                font.draw(poseStack, hint, contentX, 95, 0xFFAAAAAA);
                return;
            }

            // 渲染子任务列表
            renderSubTasks(poseStack, contentX, 80);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderSubTasks(PoseStack poseStack, int x, int y) {
        List<AdaptiveSubTaskModel> subTasks = getVisibleSubTasks();
        
        if (subTasks.isEmpty()) {
            font.draw(poseStack, "没有可用的子任务", x, y, 0xAAAAAA);
            return;
        }

        font.draw(poseStack, "子任务列表:", x, y, 0xFFFFFF);
        y += 20;

        for (AdaptiveSubTaskModel subTask : subTasks) {
            int color;
            String prefix = "  ";
            
            switch (subTask.getStatus()) {
                case COMPLETED:
                    color = 0xFFADFF2F;
                    prefix += "✓ ";
                    break;
                case IN_PROGRESS:
                    color = 0xFFFFA500;
                    prefix += "➤ ";
                    break;
                default:
                    color = 0xFFAAAAAA;
                    prefix += "• ";
                    break;
            }

            font.draw(poseStack, prefix + subTask.getTitle(), x, y, color);
            y += 15;
        }

        // 在非SRL模式下，如果还有更多任务未显示，显示提示信息
        if (!GameController.getInstance().isSRLQuestAvailable() && 
            subTasks.size() < currentTask.getSubTasks().size()) {
            font.draw(poseStack, "完成当前任务后解锁更多任务...", x, y + 5, 0xFFAAAAAA);
        }
    }

    private List<AdaptiveSubTaskModel> getVisibleSubTasks() {
        if (currentTask == null) return List.of();
        return TaskManager.getInstance().getSortedSubTasks(currentTask);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
} 