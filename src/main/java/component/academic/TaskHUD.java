package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AcademicTaskModel;
import model.SubTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import system.TaskManager;
import system.UIScreenManager;

public class TaskHUD {
    private final Minecraft minecraft;
    private boolean isExpanded = true; // 是否展开
    private long lastUpdateTime; // 上次更新时间

    public TaskHUD(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    // 渲染任务信息
    public void render(PoseStack poseStack) {
        // 检查 HUD 是否可见
        if (!UIScreenManager.getInstance().isHUDVisible()) {
            return; 
        }

        AcademicTaskModel currentTask = TaskManager.getInstance().getCurrentTask();
        if (currentTask == null) return; // 如果没有当前任务，直接返回

        int taskY = 50; // 调整任务起始Y坐标
        int taskX = 10; // 调整任务起始X坐标

        String title = isExpanded ? currentTask.getTitle() + " ▼" : currentTask.getTitle() + " ▶";
        this.minecraft.font.draw(poseStack, new TextComponent(title), taskX, taskY, 0xFFFFFF);
        taskY += 15;

        if (isExpanded) {
            for (SubTaskModel subTask : currentTask.getSubTasks()) {
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

                // 缩进任务内容
                this.minecraft.font.draw(poseStack, new TextComponent(taskText), taskX + 4, taskY, color);
                taskY += 12; // 调整行间距
            }
        }
    }

    // 每帧更新
    public void tick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= 1000) { // 每秒更新一次
            AcademicTaskModel currentTask = TaskManager.getInstance().getCurrentTask();
            if (currentTask != null) { // 检查当前任务是否存在
                for (SubTaskModel subTask : currentTask.getSubTasks()) {
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
            lastUpdateTime = currentTime;
        }
    }

    // 切换展开/收起状态
    public void toggleExpand() {
        isExpanded = !isExpanded;
    }
}