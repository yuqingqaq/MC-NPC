package component.adaptive;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import system.TimeManager;

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
            for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                height += 12; // 每个子任务的高度
            }
        } else {
            // 在收起状态下，显示进行中的子任务
            for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
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

        // 在收起状态下仍然显示进行中的子任务
        if (!isExpanded) {
            for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS) {
                    String taskText = subTask.getTitle();
                    String formattedTime = TimeManager.getInstance().formatTime(subTask.getRemainingTime()); // 使用 TimeManager 格式化时间
                    taskText += " " + formattedTime;
                    minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, 0xFFFFA500);
                    y += 10;
                }
            }
        } else {
            // 展开状态，显示所有子任务
            for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                int color;
                String taskText;

                switch (subTask.getStatus()) {
                    case COMPLETED:
                        color = 0xFFADFF2F;
                        taskText = subTask.getTitle();
                        break;
                    case IN_PROGRESS:
                        color = 0xFFFFA500;
                        taskText = subTask.getTitle() + " " + TimeManager.getInstance().formatTime(subTask.getRemainingTime());
                        break;
                    default:
                        color = 0xFFAAAAAA;
                        taskText = subTask.getTitle() ;
                        break;
                }

                minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, color);
                y += 12;
            }
        }
    }

    public void toggleExpand() {
        isExpanded = !isExpanded;
    }

    public AdaptiveTaskModel getTask() {
        return task;
    }
}