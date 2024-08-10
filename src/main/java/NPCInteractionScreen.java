import com.mojang.blaze3d.matrix.MatrixStack;
import controller.GameController;
import model.NPCModel;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.list.ExtendedList;

import java.util.ArrayList;
import java.util.List;

public class NPCInteractionScreen extends Screen {
    private TextFieldWidget inputField;
    private Button sendButton;
//    private CustomTaskList<TaskEntry> taskList;
    private NPCModel currentNPC;
    private List<String> chatHistory;

    public NPCInteractionScreen(NPCModel npc) {
        super(new StringTextComponent("NPC Interaction: " + npc.getNPCName()));
        this.currentNPC = npc;
        this.chatHistory = new ArrayList<>();

    }

    @Override
    protected void init() {
        super.init();
        int centerY = this.height / 2;
        int centerX = this.width / 2;

        this.inputField = new TextFieldWidget(this.font, centerX - 150, centerY + 60, 300, 20, new StringTextComponent("Enter Message"));
        this.addWidget(this.inputField);

        this.sendButton = this.addButton(new Button(centerX - 40, centerY + 90, 80, 20, new StringTextComponent("Send"), button -> {
            sendChatMessage();
        }));
//
//        this.taskList = new CustomTaskList<>(this.minecraft, this.width, this.height - 100, 100, this.height - 50, 30);
//        currentNPC.getTasks().forEach(task -> this.taskList.addPublicEntry(new TaskEntry(task.getDescription())));
//        this.addWidget(this.taskList);
    }

    private void sendChatMessage() {
        String message = inputField.getValue().trim();
        if (!message.isEmpty()) {
            NPCModel npc = currentNPC; // Assuming this is already set
            String response = GameController.getInstance().interactWithNPC(npc, message);
            this.minecraft.player.sendMessage(new StringTextComponent("You: " + message), this.minecraft.player.getUUID());
            inputField.setValue(""); // Clear input field after sending
            this.minecraft.player.sendMessage(new StringTextComponent(npc.getNPCName() + ": " + response), this.minecraft.player.getUUID());
            // Add messages to chat history
            chatHistory.add("You: " + message);
            chatHistory.add(npc.getNPCName() + ": " + response);
        }
    }

    private String simulateNPCResponse(String input) {
        // Simulate a response based on input
        return "Hello, how can I help you?";
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.inputField.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredString(matrixStack, this.font, "NPC Interaction", this.width / 2, 20, 0xFFFFFF);

        // Render chat history
        int yOffset = 40;
        for (String line : chatHistory) {
            drawString(matrixStack, this.font, line, 10, yOffset, 0xFFFFFF);
            yOffset += 10;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}