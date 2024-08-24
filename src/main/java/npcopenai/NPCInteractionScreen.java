package npcopenai;

import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import model.NPCModel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class NPCInteractionScreen extends Screen {
    private EditBox inputField;
    private Button sendButton;
    private NPCModel currentNPC;
    private List<String> chatHistory;

    public NPCInteractionScreen(NPCModel npc) {
        super(new TextComponent("NPC Interaction: " + npc.getNPCName()));
        this.currentNPC = npc;
        this.chatHistory = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();
        int centerY = this.height / 2;
        int centerX = this.width / 2;

        this.inputField = new EditBox(this.font, centerX - 150, centerY + 60, 300, 20, new TextComponent("Enter Message"));
        this.addWidget(this.inputField);

        this.sendButton = this.addRenderableWidget(new Button(centerX - 40, centerY + 90, 80, 20, new TextComponent("Send"), button -> {
            sendChatMessage();
        }));
    }

    private void sendChatMessage() {
        String message = inputField.getValue().trim();
        if (!message.isEmpty()) {
            String response = GameController.getInstance().interactWithNPC(currentNPC, message);
            this.minecraft.player.sendMessage(new TextComponent("You: " + message), this.minecraft.player.getUUID());
            inputField.setValue(""); // Clear input field after sending
            this.minecraft.player.sendMessage(new TextComponent(currentNPC.getNPCName() + ": " + response), this.minecraft.player.getUUID());
            // Add messages to chat history
            chatHistory.add("You: " + message);
            chatHistory.add(currentNPC.getNPCName() + ": " + response);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.inputField.render(poseStack, mouseX, mouseY, partialTicks);

        drawCenteredString(poseStack, this.font, "NPC Interaction", this.width / 2, 20, 0xFFFFFF);

        // Render chat history
        int yOffset = 40;
        for (String line : chatHistory) {
            drawString(poseStack, this.font, line, 10, yOffset, 0xFFFFFF);
            yOffset += 10;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}