package gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.Minecraft;

public class StrategyScreen extends Screen {
    private final Minecraft minecraft;
    private String selectedStrategy = "";

    public StrategyScreen(Minecraft minecraft) {
        super(new TextComponent("策略沙盘"));
        this.minecraft = minecraft;
    }

    @Override
    protected void init() {
        // 添加策略选项按钮
        this.addRenderableWidget(new Button(20, 40, 100, 20, new TextComponent("问问NPC"), button -> {
            selectedStrategy = "问问NPC";
            // 切换到对话面板
            minecraft.setScreen(new NPCDialogueScreen(minecraft));
        }));

        this.addRenderableWidget(new Button(20, 70, 100, 20, new TextComponent("自行规划"), button -> {
            selectedStrategy = "自行规划";
            // 切换到任务拆解面板
            minecraft.setScreen(new TaskPlanningScreen(minecraft));
        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, "策略沙盘", this.width / 2, 20, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
} 