package gui.academic;

import com.mojang.blaze3d.vertex.PoseStack;

import component.academic.TaskDetailPanel;
import component.academic.TaskOverviewPanel;
import controller.GameController;
import model.AdaptiveTaskModel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import system.TaskManager;
import system.UIScreenManager;
import system.UITaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskOverviewScreen extends Screen {
    private List<AdaptiveTaskModel> adaptiveTasks;
    private AdaptiveTaskModel currentTask;
    private TaskOverviewPanel taskPanel;
    private TaskDetailPanel detailPanel;
    private Map<String, List<String>> categoryTasks = new HashMap<>();

    public TaskOverviewScreen() {
        super(new TextComponent("任务总览"));
    }

    @Override
    protected void init() {
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);
        adaptiveTasks = GameController.getInstance().getAdaptiveTasks();
        
        // 构建分类任务数据
        for (AdaptiveTaskModel task : adaptiveTasks) {
            String category = task.getCategory();
            categoryTasks.computeIfAbsent(category, k -> new ArrayList<>())
                    .add(task.getTitle());
        }

        // 设置初始任务（如果有的话）
        if (!adaptiveTasks.isEmpty()) {
            if(UITaskManager.getInstance().getCurrentTaskInOverview() == null){
                currentTask = adaptiveTasks.get(0);
                UITaskManager.getInstance().setCurrentTaskInOverview(currentTask); // 设置当前任务
            }
            else{
                currentTask = UITaskManager.getInstance().getCurrentTaskInOverview();
            }
        }

        // 创建左侧任务列表面板
        int leftPanelWidth = 150;
        taskPanel = new TaskOverviewPanel(
            minecraft,
            leftPanelWidth,
            height - 30,
            25,
            10,
            1,
            4,
            categoryTasks,
            this::onTaskSelected,
            currentTask.getTitle()
        );
        
        // 创建右侧详情面板
        detailPanel = new TaskDetailPanel(
            minecraft,
            width - leftPanelWidth - 10,
            height - 30,
            25,
            leftPanelWidth + 10,
            currentTask
        );
        
        addRenderableWidget(taskPanel);
        addRenderableWidget(detailPanel);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        
        // 渲染标题栏背景
        fill(poseStack, 0, 0, width, 20, 0x80000000);
        
        // 渲染标题
        drawCenteredString(poseStack, font, "任务总览", width / 2, 6, 0xFFFFFF);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void onTaskSelected(String taskTitle) {
        currentTask = adaptiveTasks.stream()
                .filter(task -> task.getTitle().equals(taskTitle))
                .findFirst()
                .orElse(null);
        detailPanel.setTask(currentTask);
        UITaskManager.getInstance().setCurrentTaskInOverview(currentTask);
        TaskManager.getInstance().setCurrentTaskInOverview(currentTask);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (taskPanel.isMouseOver(mouseX, mouseY)) {
            return taskPanel.mouseScrolled(mouseX, mouseY, delta);
        } else if (detailPanel.isMouseOver(mouseX, mouseY)) {
            return detailPanel.mouseScrolled(mouseX, mouseY, delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        super.onClose();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 返回 false 以隐藏鼠标
    }
}