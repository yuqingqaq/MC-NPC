package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AcademicTaskModel;
import model.SubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import utils.TimeUtil;

public class HUDTask {
    private final Minecraft minecraft;
    private final AcademicTaskModel task;
    private boolean isExpanded = false; // 是否展开

    public HUDTask(Minecraft minecraft, AcademicTaskModel task) {
        this.minecraft = minecraft;
        this.task = task;
    }

    public int getHeight() {
        // 计算高度
        int height = 15; // 标题的高度
        if (isExpanded) {
            for (SubTaskModel subTask : task.getSubTasks()) {
                height += 12; // 每个子任务的高度
            }
        } else {
            // 在收起状态下，显示进行中的子任务
            for (SubTaskModel subTask : task.getSubTasks()) {
                if (subTask.getStatus() == SubTaskModel.TaskStatus.IN_PROGRESS) {
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
            for (SubTaskModel subTask : task.getSubTasks()) {
                if (subTask.getStatus() == SubTaskModel.TaskStatus.IN_PROGRESS) {
                    String taskText = subTask.getTitle();
                    String formattedTime = TimeUtil.formatTime(subTask.getRemainingTime());
                    taskText += " " + formattedTime;
                    minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, 0xFFFFA500);
                    y += 10;
                }
            }
        } else {
            // 展开状态，显示所有子任务
            for (SubTaskModel subTask : task.getSubTasks()) {
                int color;
                String taskText;

                switch (subTask.getStatus()) {
                    case COMPLETED:
                        color = 0xFFADFF2F;
                        taskText = subTask.getTitle();
                        break;
                    case IN_PROGRESS:
                        color = 0xFFFFA500;
                        taskText = subTask.getTitle() + " " + TimeUtil.formatTime(subTask.getRemainingTime());
                        break;
                    default:
                        color = 0xFFAAAAAA;
                        taskText = subTask.getTitle() + " " + subTask.getEstimatedTime();
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

    public AcademicTaskModel getTask() {
        return task;
    }
}