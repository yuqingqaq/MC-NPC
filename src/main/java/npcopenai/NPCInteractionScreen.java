package npcopenai;
import component.ChatScrollPanel;
import component.HintScrollPanel;
import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import metadata.NPCMessage;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NPCInteractionScreen extends Screen {
    private EditBox inputField;
    private Button sendButton;
    private Button hintButton;
    private NPCModel currentNPC;
    private List<NPCMessage> chatHistory;
    private List<String> hintHistory;
    private HintScrollPanel hintPanel;
    private ChatScrollPanel chatPanel;

    public NPCInteractionScreen(NPCModel npc) {
        super(new TextComponent("NPC Interaction: " + npc.getNPCName()));
        this.currentNPC = npc;
        this.chatHistory = npc.getChatHistory();
        this.hintHistory = new ArrayList<>(Arrays.asList());
    }
    @Override
    protected void init() {

        super.init();

        int centerY = this.height / 2;
        int centerX = this.width  / 2;

        this.inputField = new EditBox(this.font, centerX - 190, centerY + 65, 200, 20, new TextComponent("Enter Message"));
        this.addWidget(this.inputField);

        this.hintButton = this.addRenderableWidget(new Button(centerX + 75, centerY + 65, 80, 20, new TextComponent("Hint"), button -> {
            getAdvice();
        }));

        this.sendButton = this.addRenderableWidget(new Button(centerX - 125, centerY + 95, 80, 20, new TextComponent("Send"), button -> {
            sendChatMessage();
        }));

        Minecraft mc = Minecraft.getInstance();

        // 计算 ScrollPanel 的顶部位置
        int panelTop = centerY - 87; // ScrollPanel 的顶部位置

        // ScrollPanel 的其他参数
        int hintPanelWidth = 150;  // 面板宽度
        int hintPanelHeight = 140; // 面板高度
        int hintPanelLeft = 250;    // 面板左侧位置
        int hintPanelBorder = 5;   // 面板边框大小
        int scrollBarWidth = 5; // 滚动条宽度

        // 在 NPCInteractionScreen 的 init 方法中
        this.hintPanel = new HintScrollPanel(mc, hintPanelWidth, hintPanelHeight, panelTop, hintPanelLeft, hintPanelBorder, scrollBarWidth, hintHistory);

        // ScrollPanel 的其他参数
        int chatPanelWidth = 250;  // 面板宽度
        int chatPanelHeight = 140; // 面板高度
        int chatPanelLeft = 15;    // 面板左侧位置
        int chatPanelBorder = 5;   // 面板边框大小

        // 在 NPCInteractionScreen 的 init 方法中
        this.chatPanel = new ChatScrollPanel(mc, chatPanelWidth, chatPanelHeight, panelTop, chatPanelLeft, chatPanelBorder, scrollBarWidth, chatHistory);

        // 添加关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));

        this.chatPanel.refreshPanel();
        this.hintPanel.refreshPanel();
    }
    @Override
    public void onClose() {
        this.minecraft.setScreen(null);  // 关闭当前屏幕
    }

    private void sendChatMessage() {
        String message = inputField.getValue().trim();
        if (!message.isEmpty()) {
            String response = GameController.getInstance().interactWithNPC(currentNPC, message);
            inputField.setValue(""); // Clear input field after sending

            // 更新聊天历史
            chatHistory.add(new NPCMessage("user", message));
            chatHistory.add(new NPCMessage(currentNPC.getNPCName(), response));

            // 刷新聊天面板和提示面板
            this.chatPanel.refreshPanel();
        }
    }
    private void getAdvice() {
        if (!chatHistory.isEmpty()) {
            String advice= GameController.getInstance().interactWithExpert(currentNPC,"当前对话无法进行下去，Give concise and professional advice from a third-party perspective to keep the conversation going");
            // 更新聊天历史
            hintHistory.add(advice);
            hintHistory.add("");
            this.hintPanel.refreshPanel();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        double screenWidth = this.width; // 假设 'this.width' 是屏幕宽度

        if (mouseX < screenWidth * 2 / 3.0) {
            // 鼠标在屏幕左侧2/3区域内
            return chatPanel.mouseScrolled(mouseX, mouseY, scroll);
        } else {
            // 鼠标在屏幕右侧1/3区域内
            return hintPanel.mouseScrolled(mouseX, mouseY, scroll);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.inputField.render(poseStack, mouseX, mouseY, partialTicks);
        this.hintPanel.render(poseStack, mouseX, mouseY, partialTicks); // Render ScrollPanel
        this.chatPanel.render(poseStack, mouseX, mouseY, partialTicks); // Render ScrollPanel
        drawCenteredString(poseStack, this.font, "NPC Interaction", this.width / 2 - 150, 20, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "Expert", this.width / 2 + 65, 20, 0xFFFFFF);

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