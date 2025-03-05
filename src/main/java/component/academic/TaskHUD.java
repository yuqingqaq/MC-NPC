package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AcademicTaskModel;
import model.SubTaskModel;
import net.minecraft.client.Minecraft;
import system.TaskManager;
import system.UIScreenManager;

import java.util.ArrayList;
import java.util.List;

public class TaskHUD {
    private final Minecraft minecraft;
    private final List<HUDTask> hudTasks = new ArrayList<>();
    private long lastUpdateTime;

    public TaskHUD(Minecraft minecraft) {
        this.minecraft = minecraft;
        initializeTasks();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    private void initializeTasks() {
        List<AcademicTaskModel> tasks = TaskManager.getInstance().getTasks();
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }
        for (AcademicTaskModel task : tasks) {
            System.out.println("Initializing task: " + task.getTitle());
            for (SubTaskModel subTask : task.getSubTasks()) {
                System.out.println("SubTask: " + subTask.getTitle() + ", estimatedTime: " + subTask.getEstimatedTime());
            }
            hudTasks.add(new HUDTask(minecraft, task));

        }

    }

    public void render(PoseStack poseStack) {
        if (!UIScreenManager.getInstance().isHUDVisible()) {
            return;
        }

        int y = 50;
        for (HUDTask hudTask : hudTasks) {
            hudTask.render(poseStack, 10, y);
            y += hudTask.getHeight(); // 根据任务的高度动态调整 y 坐标
            y +=10;
        }
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= 1000) { // 每秒更新一次
            System.out.println("Updating tasks at: " + currentTime);
            for (HUDTask hudTask : hudTasks) {
                AcademicTaskModel academicTask = hudTask.getTask();
                for (SubTaskModel subTask : academicTask.getSubTasks()) {
                    if (subTask.getStatus() == SubTaskModel.TaskStatus.IN_PROGRESS) {
                        System.out.println("Updating time for subtask: " + subTask.getTitle());
                        TaskManager.getInstance().updateSubTaskTime(subTask);
                    }
                }
            }
            lastUpdateTime = currentTime;
        }
    }

    public void toggleAllTasks() {
        for (HUDTask hudTask : hudTasks) {
            hudTask.toggleExpand();
        }
    }
}