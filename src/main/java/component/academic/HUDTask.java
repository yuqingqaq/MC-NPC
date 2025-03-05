package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AcademicTaskModel;
import model.SubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

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
                    String taskText = subTask.getTitle(); // 只显示任务标题
                    minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, 0xFFFFA500);
                    y += 10; // 调整行间距
                }
            }
        } else {
            // 展开状态，显示所有子任务
            for (SubTaskModel subTask : task.getSubTasks()) {
                int color;
                String taskText;

                switch (subTask.getStatus()) {
                    case COMPLETED:
                        color = 0xFFADFF2F; // 浅绿色
                        taskText = subTask.getTitle();
                        break;
                    case IN_PROGRESS:
                        color = 0xFFFFA500; // 橙色
                        taskText = subTask.getTitle() + " " + subTask.getEstimatedTime();
                        break;
                    default:
                        color = 0xFFAAAAAA; // 灰色
                        taskText = subTask.getTitle();
                        break;
                }

                minecraft.font.draw(poseStack, new TextComponent(taskText), x + 4, y, color);
                y += 12; // 调整行间距
            }
        }
    }

    public void toggleExpand() {
        isExpanded = !isExpanded;
    }

    public void updateTime() {
        for (SubTaskModel subTask : task.getSubTasks()) {
            if (subTask.getStatus() == SubTaskModel.TaskStatus.IN_PROGRESS) {
                String[] timeParts = subTask.getEstimatedTime().split(":");
                int minutes = Integer.parseInt(timeParts[0]);
                int seconds = Integer.parseInt(timeParts[1]);

                if (minutes > 0 || seconds > 0) {
                    if (seconds == 0) {
                        minutes--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }
                    subTask.setEstimatedTime(String.format("%02d:%02d", minutes, seconds));
                } else {
                    subTask.setStatus(SubTaskModel.TaskStatus.COMPLETED);
                }
            }
        }
    }
} 