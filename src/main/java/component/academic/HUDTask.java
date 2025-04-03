package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import system.TaskManager;
import system.TimeManager;
import controller.GameController;

import java.util.List;

public class HUDTask {
    private final Minecraft minecraft;
    private final AdaptiveTaskModel task;
    private boolean isExpanded = false; // 是否展开

    public HUDTask(Minecraft minecraft, AdaptiveTaskModel task) {
        this.minecraft = minecraft;
        this.task = task;
    }

    public int getHeight() {
        // 计算高度
        int height = 15; // 标题的高度
        if (isExpanded) {
            // 使用排序后的子任务列表
            List<AdaptiveSubTaskModel> sortedSubTasks = TaskManager.getInstance().getSortedSubTasks(task);
            height += sortedSubTasks.size() * 12; // 每个子任务的高度
        } else {
            // 在收起状态下，显示进行中的子任务
            // 使用排序后的子任务列表
            List<AdaptiveSubTaskModel> sortedSubTasks = TaskManager.getInstance().getSortedSubTasks(task);
            for (AdaptiveSubTaskModel subTask : sortedSubTasks) {
                if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS) {
                    height += 10; // 进行中的子任务的高度
                }
            }
        }
        return height;
    }

    public void render(PoseStack poseStack, int x, int y) {
        String title = isExpanded ? task.getTitle() + " ▼" : task.getTitle() + " ▶";
        minecraft.font.draw(poseStack, new TextComponent(title), x, y, 0xFFFFFF);
        y += 15;

        // 获取排序后的子任务列表
        List<AdaptiveSubTaskModel> sortedSubTasks = TaskManager.getInstance().getSortedSubTasks(task);

        // 检查是否是SRL任务且需要制定策略
        if (GameController.getInstance().isSRLQuestAvailable() && !TaskManager.getInstance().isStrategyPlanned()) {
            //String message = "需要先制定学习策略！";
            //minecraft.font.draw(poseStack, new TextComponent(message), x + 4, y, 0xFFFF0000);
            return;
        }

        // 在收起状态下仍然显示进行中的子任务
        if (!isExpanded) {
            for (AdaptiveSubTaskModel subTask : sortedSubTasks) {
                if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS) {
                    String taskText = subTask.getTitle();
                    // 只在SRL模式下添加倒计时
                    if (GameController.getInstance().isSRLQuestAvailable()) {
                        String formattedTime = TimeManager.getInstance().formatTime(subTask.getRemainingTime());
                        taskText += " " + formattedTime;
                    }
                    minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, 0xFFFFA500);
                    y += 10;
                }
            }
        } else {
            // 展开状态，显示所有可见的子任务
            if (!GameController.getInstance().isSRLQuestAvailable()) {
                // 非SRL模式：显示已完成的任务和第一个未完成的任务
                for (AdaptiveSubTaskModel subTask : sortedSubTasks) {
                    int color;
                    String taskText = subTask.getTitle();

                    if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                        color = 0xFFADFF2F; // 已完成任务显示绿色
                    } else {
                        color = 0xFFFFA500; // 当前任务显示橙色
                        // 移除这里的倒计时显示，因为是非SRL模式
                        // taskText += " " + TimeManager.getInstance().formatTime(subTask.getRemainingTime());
                    }

                    minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, color);
                    y += 12;
                }

                // 如果还有更多任务，显示提示信息
                if (sortedSubTasks.size() < task.getSubTasks().size()) {
                    minecraft.font.draw(poseStack, new TextComponent("解锁更多任务..."), x + 4, y, 0xFFAAAAAA);
                }
            } else {
                // SRL模式：显示所有任务
                for (AdaptiveSubTaskModel subTask : sortedSubTasks) {
                    int color;
                    String taskText = subTask.getTitle();

                    switch (subTask.getStatus()) {
                        case COMPLETED:
                            color = 0xFFADFF2F;
                            break;
                        case IN_PROGRESS:
                            color = 0xFFFFA500;
                            // 在SRL模式下保留倒计时显示
                            taskText += " " + TimeManager.getInstance().formatTime(subTask.getRemainingTime());
                            break;
                        default:
                            color = 0xFFAAAAAA;
                            break;
                    }

                    minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, color);
                    y += 12;
                }
            }
        }
    }

    public void toggleExpand() {
        isExpanded = !isExpanded;
    }

    public AdaptiveTaskModel getTask() {
        return task;
    }

    public boolean isExpanded() {
        return isExpanded;
    }
}