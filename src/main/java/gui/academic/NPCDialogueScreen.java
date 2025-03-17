package gui.adaptive;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;

public class NPCDialogueScreen extends Screen {
    private final Minecraft minecraft;
    private EditBox inputBox;
    private List<String> dialogueHistory = new ArrayList<>();

    public NPCDialogueScreen(Minecraft minecraft) {
        super(new TextComponent("与NPC对话"));
        this.minecraft = minecraft;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 添加对话输入框
        inputBox = new EditBox(this.font, centerX - 100, centerY + 20, 200, 20, new TextComponent("输入对话"));
        this.addWidget(inputBox);

        // 添加发送按钮
        this.addRenderableWidget(new Button(centerX - 100, centerY + 50, 200, 20, new TextComponent("发送消息"), button -> {
            String message = inputBox.getValue();
            if (!message.isEmpty()) {
                dialogueHistory.add("玩家: " + message);
                dialogueHistory.add("NPC: " + getNPCResponse(message));
                inputBox.setValue("");
            }
        }));

        // 返回按钮
        this.addRenderableWidget(new Button(centerX - 100, centerY + 80, 200, 20, new TextComponent("返回"), button -> {
            minecraft.setScreen(new StrategyScreen(minecraft));
        }));
    }

    private String getNPCResponse(String message) {
        // 模拟NPC的回复
        return "这是NPC的回复";
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, "与NPC对话", this.width / 2, 20, 0xFFFFFF);
        
        int y = 40;
        for (String line : dialogueHistory) {
            drawString(poseStack, this.font, line, 10, y, 0xFFFFFF);
            y += 10;
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}