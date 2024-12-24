package gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ChatScrollPanel;
import component.HintScrollPanel;
import controller.GameController;
import metadata.NPCMessage;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import speech.AudioPlayer;
import speech.SpeechHandler;
import speech.TextToSpeechService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NPCInteractionScreenwithExpert extends Screen {
    private EditBox inputField;
    private Button sendButton;
    private Button hintButton;
    private NPCModel currentNPC;
    private List<NPCMessage> chatHistory;
    private List<String> hintHistory;
    private HintScrollPanel hintPanel;
    private ChatScrollPanel chatPanel;
    private TutorialToast toast;
    private boolean isToastShown = false;

    private Button recordButton;
    private AudioPlayer audioPlayer = new AudioPlayer();
    private SpeechHandler speechHandler = new SpeechHandler();

    public NPCInteractionScreenwithExpert(NPCModel npc) {
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
        // 添加录音按钮
        recordButton = this.addRenderableWidget(new Button(centerX - 190, centerY + 90, 110, 20, new TextComponent("Start Recording"), button -> {
            toggleRecording();
        }));
        this.hintButton = this.addRenderableWidget(new Button(centerX + 75, centerY + 65, 80, 20, new TextComponent("Hint"), button -> {
            getAdvice();
        }));

        this.sendButton = this.addRenderableWidget(new Button(centerX - 70, centerY + 90, 80, 20, new TextComponent("Send"), button -> {
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

        //this.toast = new PersistentToast(TutorialToast.Icons.RECIPE_BOOK,"Title","Please ask expert for advices",true);
        //Minecraft.getInstance().getToasts().addToast(toast);
        TextComponent title = new TextComponent("Don't know how to reply？");
        TextComponent messageContent = new TextComponent("Ask expert for advice！");

        // 创建 TutorialToast 实例
        this.toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, title, messageContent, true);

    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
        if (this.toast != null) {
            this.toast.hide();
            isToastShown = false;
        }
    }

    private void toggleRecording() {
        if (!speechHandler.isRecording()) {
            try {
                speechHandler.startRecording();
                recordButton.setMessage(new TextComponent("Stop Recording"));
            } catch (Exception e) {
                System.out.println("Error starting recording: " + e.getMessage());
            }
        } else {
            try {
                String audioDataText = speechHandler.stopRecording();
                recordButton.setMessage(new TextComponent("Start Recording"));
                inputField.setValue(new String(audioDataText));
                sendChatMessage();
            } catch (Exception e) {
                System.out.println("Error stopping recording: " + e.getMessage());
            }
        }
    }

    private void sendChatMessage() {
        String message = inputField.getValue().trim();
        String npcName = currentNPC.getNPCName();
        if (!message.isEmpty()) {
            String response = GameController.getInstance().interactWithNPC(currentNPC, message);
            inputField.setValue(""); // Clear input field after sending

            // 更新聊天历史
            chatHistory.add(new NPCMessage("player", message));
            chatHistory.add(new NPCMessage(currentNPC.getNPCName(), response));

            // 刷新聊天面板和提示面板
            this.chatPanel.refreshPanel();

            // 使用语音合成将NPC的回答转换为语音
            try {
                String ttsPath = TextToSpeechService.RefTTS(response,npcName);
                System.out.println(ttsPath);
                audioPlayer.playAudio(ttsPath);
            } catch (Exception e) {
                System.out.println("Text-to-speech error: " + e.getMessage());
            }

            if(!isToastShown){
                Minecraft.getInstance().getToasts().addToast(toast);
                isToastShown = true;
            }
        }
    }

    private void getAdvice() {
        if (!chatHistory.isEmpty()) {
            String advice= GameController.getInstance().interactWithExpert(currentNPC,"从客观第三方的专家视角给出建议");
            // 更新聊天历史
            hintHistory.add(advice);
            hintHistory.add("");
            this.hintPanel.refreshPanel();

            if(isToastShown) toast.hide();
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
        drawCenteredString(poseStack, this.font, "对话", this.width / 2 - 150, 20, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "Expert", this.width / 2 + 65, 20, 0xFFFFFF);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}