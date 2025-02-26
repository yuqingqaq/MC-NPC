package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ChatScrollPanel;
import metadata.NPCMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;


public class DialoguePanel extends AbstractWidget implements GuiEventListener, Widget {
    private final Minecraft minecraft;
    private final int x, y, width, height;
    private EditBox inputField;
    private Button sendButton;
    private ChatScrollPanel chatPanel;
    private List<NPCMessage> chatHistory;

    public DialoguePanel(Minecraft minecraft, int x, int y, int width, int height, List<NPCMessage> chatHistory) {
        super(x, y, width, height, new TextComponent("Dialogue Panel"));
        this.minecraft = minecraft;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.chatHistory = chatHistory;
        init();
    }

    // 初始化对话面板的组件
    private void init() {
        // 输入框
        int inputFieldWidth = this.width - 90; // 输入框宽度
        int inputFieldHeight = 20; // 输入框高度
        int inputFieldX = this.x - 10; // 输入框 X 坐标
        int inputFieldY = this.y + this.height - inputFieldHeight - 10; // 输入框 Y 坐标

        this.inputField = new EditBox(minecraft.font, inputFieldX, inputFieldY, inputFieldWidth, inputFieldHeight, new TextComponent("Enter Message"));

        // 发送按钮
        int sendButtonWidth = 80; // 发送按钮宽度
        int sendButtonX = inputFieldX + inputFieldWidth + 10; // 发送按钮 X 坐标
        int sendButtonY = inputFieldY; // 发送按钮 Y 坐标

        this.sendButton = new Button(sendButtonX, sendButtonY, sendButtonWidth, inputFieldHeight, new TextComponent("Send"), button -> sendChatMessage());

        // 聊天滚动面板
        int chatPanelWidth = this.width;
        int chatPanelHeight = this.height - inputFieldHeight - 20 - 10;
        int chatPanelX = this.x - 20;
        int chatPanelY = this.y + 10;
        int chatPanelBorder = 5;   // 面板边框大小
        int scrollBarWidth = 5; // 滚动条宽度
        this.chatPanel = new ChatScrollPanel(minecraft, chatPanelWidth, chatPanelHeight, chatPanelY, chatPanelX, chatPanelBorder, scrollBarWidth, chatHistory);
    }

    // 发送聊天消息
    private void sendChatMessage() {
        String message = this.inputField.getValue().trim();
        if (!message.isEmpty()) {
            // 添加玩家消息到聊天历史
            chatHistory.add(new NPCMessage("player", message));

            // 模拟 NPC 回复
            String response = "你好，你需要找图书管理员吗？或许你可以去图书馆！"; // 这里可以替换为实际的逻辑
            chatHistory.add(new NPCMessage("npc", response));
            
            response = "任务：到达图书馆 预计用时10min"; // 这里可以替换为实际的逻辑
            chatHistory.add(new NPCMessage("npc", response));
            // 清空输入框
            this.inputField.setValue("");

            // 刷新聊天面板
            this.chatPanel.refreshPanel();
        }
    }

    // 渲染对话面板
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染背景
        //fill(poseStack, x, y, x + width, y + height, 0x33000000);

        // 渲染聊天滚动面板
        this.chatPanel.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染输入框和发送按钮
        this.inputField.render(poseStack, mouseX, mouseY, partialTicks);
        this.sendButton.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 将鼠标点击事件传递给输入框和按钮
        if (inputField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (sendButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 将键盘事件传递到输入框（支持输入消息）
        if (inputField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        // 在按下 Enter 键时发送消息
        if (keyCode == 257) { // Enter 键的键码
            sendChatMessage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // 将字符输入传递给输入框
        return inputField.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }

    // 每帧更新
    public void tick() {
        this.inputField.tick();
    }

    // 处理鼠标释放事件（可选）
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    // 处理鼠标滚轮事件
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.chatPanel.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
        throw new UnsupportedOperationException("Unimplemented method 'updateNarration'");
    }
}