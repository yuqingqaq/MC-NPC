package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import system.TaskManager;
import system.UIScreenManager;

import java.util.ArrayList;
import java.util.List;

public class TaskHUD {
    private final Minecraft minecraft;
    private final List<HUDTask> hudTasks = new ArrayList<>();

    public TaskHUD(Minecraft minecraft) {
        this.minecraft = minecraft;
        initializeTasks(); // 初始化任务
    }

    // 初始化任务，只运行一次，避免任务列表为空
    private void initializeTasks() {
        List<AdaptiveTaskModel> tasks = TaskManager.getInstance().getTasks();
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }

        // 仅初始化包含 IN_PROGRESS 或 COMPLETED 子任务的 HUDTask
        for (AdaptiveTaskModel task : tasks) {
            boolean hasValidSubTasks = task.getSubTasks().stream().anyMatch(
                    subTask -> subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS
                            || subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED);

            if (hasValidSubTasks) {
                hudTasks.add(new HUDTask(minecraft, task));
            }
        }
    }

    // 更新任务列表，动态添加新任务或移除不符合条件的任务
    private void updateTasks() {
        List<AdaptiveTaskModel> tasks = TaskManager.getInstance().getTasks();
        if (tasks == null || tasks.isEmpty()) {
            hudTasks.clear(); // 如果没有任务清空列表
            return;
        }

        // 临时列表来存储 HUDTask
        List<HUDTask> updatedHudTasks = new ArrayList<>();

        // 遍历所有任务，检查是否需要更新到 HUD
        for (AdaptiveTaskModel task : tasks) {
            boolean hasValidSubTasks = task.getSubTasks().stream().anyMatch(
                    subTask -> subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS
                            || subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED);

            if (hasValidSubTasks) {
                // 检查当前 HUDTask 是否已经存在
                HUDTask existingHudTask = hudTasks.stream()
                        .filter(hudTask -> hudTask.getTask().equals(task))
                        .findFirst()
                        .orElse(null);

                if (existingHudTask != null) {
                    updatedHudTasks.add(existingHudTask); // 保留已有的 HUDTask
                } else {
                    updatedHudTasks.add(new HUDTask(minecraft, task)); // 如果不存在则添加新 HUDTask
                }
            }
        }

        // 替换 HUD 列表为最新的任务列表
        hudTasks.clear();
        hudTasks.addAll(updatedHudTasks);
    }

    public void render(PoseStack poseStack) {
        if (!UIScreenManager.getInstance().isHUDVisible()) {
            return;
        }

        int y = 50;
        if (hudTasks.isEmpty()) {
            // 如果没有任务符合条件，显示 "暂无任务"
            minecraft.font.draw(poseStack, new TextComponent("暂无任务"), 10, y, 0xFFFFFF);
        } else {
            // 渲染所有符合条件的 HUDTask
            for (HUDTask hudTask : hudTasks) {
                hudTask.render(poseStack, 10, y);
                y += hudTask.getHeight(); // 根据任务的高度动态调整 y 坐标
                y += 10;
            }
        }
    }

    public void tick() {
        // 定期更新任务状态
        updateTasks();
    }

    public void toggleAllTasks() {
        // First determine if we should expand or collapse all
        // If any task is collapsed, we'll expand all tasks
        boolean shouldExpandAll = false;
        for (HUDTask hudTask : hudTasks) {
            if (!hudTask.isExpanded()) { // You'll need to add a getter for isExpanded
                shouldExpandAll = true;
                break;
            }
        }

        // Now set all tasks to the same state
        for (HUDTask hudTask : hudTasks) {
            // Set all tasks to the target state rather than toggling them individually
            if (hudTask.isExpanded() != shouldExpandAll) {
                hudTask.toggleExpand();
            }
        }
    }
}