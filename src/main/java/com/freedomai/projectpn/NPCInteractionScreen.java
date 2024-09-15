package com.freedomai.projectpn;
import com.freedomai.projectpn.component.ChatScrollPanel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.freedomai.projectpn.controller.GameController;
import com.freedomai.projectpn.model.NPCModel;
import net.minecraft.client.Minecraft;
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
    private ChatScrollPanel chatPanel;

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

        this.inputField = new EditBox(this.font, centerX - 150, centerY + 65, 300, 20, new TextComponent("Enter Message"));
        this.addWidget(this.inputField);

        this.sendButton = this.addRenderableWidget(new Button(centerX - 40, centerY + 95, 80, 20, new TextComponent("Send"), button -> {
            sendChatMessage();
        }));

        Minecraft mc = Minecraft.getInstance();

        // 计算 ScrollPanel 的顶部位置
        int panelTop = centerY - 87; // ScrollPanel 的顶部位置

        // ScrollPanel 的其他参数
        int panelWidth = 300;  // 面板宽度
        int panelHeight = 140; // 面板高度
        int panelLeft = 60;    // 面板左侧位置
        int panelBorder = 5;   // 面板边框大小
        int scrollBarWidth = 10; // 滚动条宽度

        // 在 NPCInteractionScreen 的 init 方法中
        this.chatPanel = new ChatScrollPanel(mc, panelWidth, panelHeight, panelTop, panelLeft, panelBorder, scrollBarWidth, chatHistory);

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

            this.chatPanel.refreshPanel();

        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.inputField.render(poseStack, mouseX, mouseY, partialTicks);
        this.chatPanel.render(poseStack, mouseX, mouseY, partialTicks); // Render ScrollPanel
        drawCenteredString(poseStack, this.font, "NPC Interaction", this.width / 2, 20, 0xFFFFFF);

//        // Render chat history
//        int yOffset = 40;
//        for (String line : chatHistory) {
//            drawString(poseStack, this.font, line, 10, yOffset, 0xFFFFFF);
//            yOffset += 10;
//        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}