package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import model.AcademicTaskModel;
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
        if (tasks == null) {
            return; // 如果任务列表为 null，直接返回
        }
        for (AcademicTaskModel task : tasks) {
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
            for (HUDTask hudTask : hudTasks) {
                hudTask.updateTime();
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