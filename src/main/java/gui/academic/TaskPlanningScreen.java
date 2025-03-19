package gui.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskPlanningScreen extends Screen {
    private final Minecraft minecraft;
    private EditBox taskBox;
    private List<String> tasks = new ArrayList<>();

    public TaskPlanningScreen(Minecraft minecraft) {
        super(new TextComponent("任务拆解"));
        this.minecraft = minecraft;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 添加任务输入框
        taskBox = new EditBox(this.font, centerX - 100, centerY + 20, 200, 20, new TextComponent("输入任务"));
        this.addWidget(taskBox);

        // 添加排序按钮
        this.addRenderableWidget(new Button(centerX - 100, centerY + 50, 200, 20, new TextComponent("添加任务"), button -> {
            String task = taskBox.getValue();
            if (!task.isEmpty()) {
                tasks.add(task);
                taskBox.setValue("");
            }
        }));

        this.addRenderableWidget(new Button(centerX - 100, centerY + 80, 200, 20, new TextComponent("排序任务"), button -> {
            Collections.sort(tasks);
        }));

        // 返回按钮
        this.addRenderableWidget(new Button(centerX - 100, centerY + 110, 200, 20, new TextComponent("返回"), button -> {
            minecraft.setScreen(new StrategyScreen(minecraft));
        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, "任务拆解", this.width / 2, 20, 0xFFFFFF);
        
        int y = 40;
        for (String task : tasks) {
            drawString(poseStack, this.font, task, 10, y, 0xFFFFFF);
            y += 10;
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}