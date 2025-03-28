package gui.screen;

import component.ChatScrollPanel;
import component.HintScrollPanel;
import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import metadata.NPCMessage;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import speech.AudioPlayer;
import speech.SpeechHandler;
import prompt.GuideNPCPromptRouting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import speech.TextToSpeechService;
import system.UIScreenManager;

public class NPCNarrationScreen extends Screen {
    private EditBox inputField;
    private Button sendButton;
    private Button hintButton;
    private Button clearButton;
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

    public NPCNarrationScreen(NPCModel npc) {
        super(new TextComponent("NPC Narration: " + npc.getNPCName()));
        this.currentNPC = npc;
        this.chatHistory = npc.getChatHistory();
        this.hintHistory = new ArrayList<>(Arrays.asList());

                
        if (npc.getNPCName().equals("学术写作导师")) {
            // 如果聊天历史为空，添加一条NPC的欢迎消息
                if (this.chatHistory.isEmpty()) {
                    String welcomeMessage = "欢迎来到大模型学习课，我是你的大模型学习导师。在这里，你将要学习有关大模型 Agent 相关的知识。\n良好的学习过程往往从确定一个明确的目标开始，请告诉我，你有什么具体想法或目标吗？";
                    this.chatHistory.add(new NPCMessage(npc.getNPCName(), welcomeMessage));
                    
                    // // 使用语音合成将欢迎词转换为语音
                    // try {
                    //     String ttsPath = TextToSpeechService.RefTTS(welcomeMessage,npc.getNPCName());
                    //     System.out.println(ttsPath);
                    //     audioPlayer.playAudio(ttsPath);
                    // } catch (Exception e) {
                    //     System.out.println("欢迎语音生成错误: " + e.getMessage());
                    // }
                }
            }
    }

    @Override
    protected void init() {

        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

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

        clearButton = this.addRenderableWidget(new Button(centerX + 145, centerY + 95, 50, 20, new TextComponent("Clear"), button -> {
            clearChatHistory();
        }));
        // this.sendButton = this.addRenderableWidget(new Button(centerX + 145, centerY + 65, 50, 20, new TextComponent("Send"), button -> {
        //      sendChatMessage();
        // }));

        Minecraft mc = Minecraft.getInstance();

        // 计算 ScrollPanel 的顶部位置
        int panelTop = centerY - 87; // ScrollPanel 的顶部位置

        // ScrollPanel 的其他参数
        int hintPanelWidth = 150;  // 面板宽度
        int hintPanelHeight = 140; // 面板高度
        int hintPanelLeft = 250;    // 面板左侧位置
        int hintPanelBorder = 5;   // 面板边框大小
        int scrollBarWidth = 5; // 滚动条宽度
//
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
//        TextComponent title = new TextComponent("Don't know how to reply？");
//        TextComponent messageContent = new TextComponent("Ask expert for advice！");
//
//        // 创建 TutorialToast 实例
//        this.toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, title, messageContent, true);

    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
        audioPlayer.stopAudio();
        if (this.toast != null) {
            this.toast.hide();
            isToastShown = false;
        }
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);

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

    private void clearChatHistory() {
            this.chatHistory.clear();

        // 刷新聊天面板和提示面板
            this.chatPanel.refreshPanel();

    }

    private void sendChatMessage() {
        String message = inputField.getValue().trim();
        String npcName = currentNPC.getNPCName();
        String selectedPrompt = "";
        if (!message.isEmpty()) {
            try{
                selectedPrompt = GuideNPCPromptRouting.route(message, currentNPC);
            } catch (Exception e) {
                System.out.println("Error routing: " + e.getMessage());
            }
            String response = GameController.getInstance().interactWithNPC(currentNPC, selectedPrompt+"\n\n用户输入："+message);
            inputField.setValue(""); // Clear input field after sending

            // 更新聊天历史
            chatHistory.add(new NPCMessage("player", message));
            chatHistory.add(new NPCMessage(currentNPC.getNPCName(), response));
            System.out.println("Response is:" + response);

            // 更新 NPC 的对话历史
            currentNPC.addDialogueToHistory(new NPCMessage("player", message));
            currentNPC.addDialogueToHistory(new NPCMessage(currentNPC.getNPCName(), response));

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

       if (mouseX < screenWidth * 3 / 3.0) {
           // 鼠标在屏幕左侧2/3区域内
            return chatPanel.mouseScrolled(mouseX, mouseY, scroll);
        }
        else {
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
        drawCenteredString(poseStack, this.font, "新生第一课", this.width / 2 - 170, 20, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "Expert", this.width / 2 + 65, 20, 0xFFFFFF);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}