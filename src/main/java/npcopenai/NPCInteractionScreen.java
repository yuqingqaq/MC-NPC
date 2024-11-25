package npcopenai;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import speech.AudioPlayer;
import speech.Example;
import speech.SpeechToTextService;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.lwjgl.BufferUtils;
import speech.TextToSpeechService;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class NPCInteractionScreen extends Screen {
    private EditBox inputField;
    private Button sendButton;
    private Button hintButton;
    private NPCModel currentNPC;
    private List<NPCMessage> chatHistory;
    private List<String> hintHistory;
    private HintScrollPanel hintPanel;
    private ChatScrollPanel chatPanel;
    private TutorialToast toast;

    private Button recordButton;
    private AudioPlayer audioPlayer = new AudioPlayer();
    private boolean isRecording = false;
    private ByteArrayOutputStream out;

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
        // 添加录音按钮
        recordButton = this.addRenderableWidget(new Button(centerX - 100, centerY + 100, 200, 20, new TextComponent("Start Recording"), button -> {
            if (!isRecording) {
                startRecording();
                recordButton.setMessage(new TextComponent("Stop Recording"));
            } else {
                stopRecording();
                recordButton.setMessage(new TextComponent("Start Recording"));
            }
        }));
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
        toast.hide();
    }

    // 开始录音
    private void startRecording() {
        isRecording = true;
        System.out.println("Recording started");

        try {
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            System.out.println("Microphone line opened and started");

            Thread thread = new Thread(() -> {
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                try {
                    while (isRecording) {
                        int count = line.read(buffer, 0, buffer.length);
                        if (count > 0) {
                            out.write(buffer, 0, count);
                        }
                    }
                    out.close();
                    System.out.println("Audio data captured and stream closed");

                } catch (Exception e) {
                    System.err.println("Recording error: " + e.getMessage());
                }
            });
            thread.start();
        } catch (Exception e) {
            System.err.println("Microphone not accessible: " + e.getMessage());
        }
    }

    // 停止录音并处理录制的音频
//    private void stopRecording() {
//        isRecording = false;
//        try {
//            byte[] audioData = out.toByteArray();
//            System.out.println("Audio data size: " + audioData.length + " bytes");
//
//            System.out.println("Before calling recognizeAudio");
//            try {
//                String text = SpeechToTextService.recognizeAudio(audioData);
//                System.out.println("Recognized text: " + text);
//                inputField.setValue(text); // 将识别的文本设置到输入框中
//            } catch (Exception e) {
//                System.err.println("Error calling recognizeAudio: " + e.getMessage());
//                e.printStackTrace();
//            }
//            System.out.println("After calling recognizeAudio");
//
//        } catch (Exception e) {
//            System.err.println("Speech recognition error: " + e.getMessage());
//        }
//    }
    private void stopRecording() {
        isRecording = false;
        try {
            byte[] audioData = out.toByteArray();
            System.out.println("Audio data size: " + audioData.length + " bytes");

            // Create a temporary WAV file
            File wavFile = createWavFile(audioData);

            if (wavFile != null) {
                System.out.println("Audio recorded and stored in " + wavFile.getAbsolutePath());

                try {
                    // Pass the WAV file to speech recognition
                    String text = SpeechToTextService.ASR(wavFile);
                    System.out.println("Recognized text: " + text);
                    inputField.setValue(text);

                    // Optional: Delete the temporary file after processing
                    wavFile.delete();
                } catch (Exception e) {
                    System.err.println("Error calling recognizeAudio: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Speech recognition error: " + e.getMessage());
        }
    }

    private File createWavFile(byte[] audioData) {
        try {
            // Define the audio format used in startRecording()
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);

            // Create a temporary file
            File tempFile = File.createTempFile("recording", ".wav");
            tempFile.deleteOnExit(); // Ensure file is deleted when JVM exits

            // Create audio input stream from byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

            // Write to WAV file
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, tempFile);

            return tempFile;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error creating WAV file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private void sendChatMessage() {
        String message = inputField.getValue().trim();
        if (!message.isEmpty()) {
            String response = GameController.getInstance().interactWithNPC(currentNPC, message);
            inputField.setValue(""); // Clear input field after sending

            // 使用语音合成将NPC的回答转换为语音
            try {
                String ttsPath = TextToSpeechService.OpenAITTS(response);
                audioPlayer.playAudio(ttsPath);
            } catch (Exception e) {
                System.err.println("Text-to-speech error: " + e.getMessage());
            }

            // 更新聊天历史
            chatHistory.add(new NPCMessage("player", message));
            chatHistory.add(new NPCMessage(currentNPC.getNPCName(), response));

            // 刷新聊天面板和提示面板
            this.chatPanel.refreshPanel();

            Minecraft.getInstance().getToasts().addToast(toast);
            // 初始化进度
            //toast.updateProgress(0.0F);
        }
    }

    private void getAdvice() {
        if (!chatHistory.isEmpty()) {
            String advice= GameController.getInstance().interactWithExpert(currentNPC,"从客观第三方的专家视角给出建议");
            // 更新聊天历史
            hintHistory.add(advice);
            hintHistory.add("");
            this.hintPanel.refreshPanel();
            //CustomToast.show(Minecraft.getInstance(), "Hint", advice);
            //toast.updateProgress(1.0F);
            toast.hide();
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