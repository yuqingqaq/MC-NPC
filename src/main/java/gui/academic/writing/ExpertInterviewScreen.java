package gui.academic.writing;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ChatScrollPanel;
import component.TextEditorWidget;
import controller.GameController;
import metadata.NPCMessage;
import model.AdaptiveSubTaskModel;
import model.AdaptiveTaskModel;
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
import system.TaskManager;
import system.UIScreenManager;

import java.util.ArrayList;
import java.util.List;

public class ExpertInterviewScreen extends Screen {
    private static final String TITLE = "专家访谈";

    // NPC交互部分
    private EditBox inputField;
    private Button sendButton;
    private Button saveButton;
    private NPCModel currentNPC;
    private List<NPCMessage> chatHistory;
    private ChatScrollPanel chatPanel;
    private TutorialToast toast;
    private boolean isToastShown = false;
    private Button recordButton;
    private AudioPlayer audioPlayer = new AudioPlayer();
    private SpeechHandler speechHandler = new SpeechHandler();

    // 总结部分
    private TextEditorWidget summaryEditor;  // 用于编写总结的文本编辑器
    private final String subTaskTitle;       // 当前子任务标题
    private AdaptiveSubTaskModel currentSubTask; // 当前子任务的引用

    public ExpertInterviewScreen(String subTaskTitle, NPCModel npc) {
        super(new TextComponent(TITLE));
        this.subTaskTitle = subTaskTitle;
        this.currentNPC = npc;
        this.chatHistory = npc.getChatHistory();

        // 在构造函数中查找当前子任务
        findCurrentSubTask();
    }

    // 查找当前子任务
    private void findCurrentSubTask() {
        TaskManager taskManager = TaskManager.getInstance();

        for (AdaptiveTaskModel task : taskManager.getTasks()) {
            for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                if (subTask.getTitle().equals(subTaskTitle)) {
                    this.currentSubTask = subTask;
                    return;
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        int centerY = this.height / 2;
        int centerX = this.width / 2;

        // 屏幕宽度划分
        int leftPanelWidth = (this.width * 3) / 5;
        int rightPanelWidth = (this.width * 2) / 5;

        // ======= 左侧：NPC交互部分 =======

        // 输入框
        this.inputField = new EditBox(this.font,
                20,
                centerY + 65,
                leftPanelWidth - 80,
                20,
                new TextComponent("发送消息"));
        this.addWidget(this.inputField);

        // 录音按钮
        recordButton = this.addRenderableWidget(new Button(
                20,
                centerY + 90,
                110,
                20,
                new TextComponent("开始录音"),
                button -> toggleRecording()));

        // 发送按钮
        this.sendButton = this.addRenderableWidget(new Button(
                leftPanelWidth - 130,
                centerY + 90,
                80,
                20,
                new TextComponent("发送"),
                button -> sendChatMessage()));

        // 聊天记录面板
        Minecraft mc = Minecraft.getInstance();
        int panelTop = 30;
        int chatPanelWidth = leftPanelWidth - 40;
        int chatPanelHeight = centerY + 25;
        int chatPanelLeft = 20;
        int chatPanelBorder = 5;
        int scrollBarWidth = 5;

        this.chatPanel = new ChatScrollPanel(
                mc,
                chatPanelWidth,
                chatPanelHeight,
                panelTop,
                chatPanelLeft,
                chatPanelBorder,
                scrollBarWidth,
                chatHistory);

        // ======= 右侧：总结编辑部分 =======

        // 总结编辑器
        this.summaryEditor = new TextEditorWidget(
                leftPanelWidth + 20,
                30,
                rightPanelWidth - 40,
                centerY + 25
        );
        this.addRenderableWidget(this.summaryEditor);

        // 加载已有的outcome
        loadExistingOutcome();

        // 保存按钮
        this.saveButton = this.addRenderableWidget(new Button(
                this.width - rightPanelWidth + 20,
                centerY + 90,
                rightPanelWidth - 40,
                20,
                new TextComponent("保存总结"),
                button -> saveOutcome()
        ));

        // 添加关闭按钮
        this.addRenderableWidget(new Button(
                this.width - 30,
                5,
                20,
                20,
                new TextComponent("X"),
                button -> onClose()));

        this.chatPanel.refreshPanel();

        // 提示Toast
        TextComponent title = new TextComponent("访谈完成了吗?");
        TextComponent messageContent = new TextComponent("来写个总结吧！");
        this.toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, title, messageContent, true);
    }

    // 加载已有的outcome到编辑器
    private void loadExistingOutcome() {
        if (currentSubTask != null) {
            String outcome = currentSubTask.getOutcome();
            if (outcome != null && !outcome.isEmpty()) {
                summaryEditor.setText(outcome);

                // 显示加载已有总结的消息
                Minecraft.getInstance().player.displayClientMessage(
                        new TextComponent("Previous summary loaded for task: " + currentSubTask.getTitle()), true
                );
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
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
                recordButton.setMessage(new TextComponent("录音停止"));
            } catch (Exception e) {
                System.out.println("Error starting recording: " + e.getMessage());
            }
        } else {
            try {
                String audioDataText = speechHandler.stopRecording();
                recordButton.setMessage(new TextComponent("=开始录音"));
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
            chatHistory.add(new NPCMessage(npcName, response));

            // 刷新聊天面板
            this.chatPanel.refreshPanel();

            // 使用语音合成将NPC的回答转换为语音
            try {
                String ttsPath = TextToSpeechService.RefTTS(response, npcName);
                System.out.println(ttsPath);
                audioPlayer.playAudio(ttsPath);
            } catch (Exception e) {
                System.out.println("Text-to-speech error: " + e.getMessage());
            }

            if (!isToastShown) {
                Minecraft.getInstance().getToasts().addToast(toast);
                isToastShown = true;
            }
        }
    }

    private void saveOutcome() {
        String editorContent = summaryEditor.getText().trim();

        if (!editorContent.isEmpty()) {
            TaskManager taskManager = TaskManager.getInstance();

            if (currentSubTask != null) {
                // 保存内容
                currentSubTask.setOutcome(editorContent);

                // 将子任务设置为已完成
                taskManager.completeSubTask(currentSubTask);

                Minecraft.getInstance().player.displayClientMessage(
                        new TextComponent("Summary saved and task marked as completed: " + currentSubTask.getTitle()), true
                );

                // 可以选择关闭界面或留在界面上
                // this.onClose();
            } else {
                // 如果在构造函数中没有找到当前子任务，再次尝试查找
                for (AdaptiveTaskModel task : taskManager.getTasks()) {
                    for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                        if (subTask.getTitle().equals(subTaskTitle)) {
                            // 保存内容
                            subTask.setOutcome(editorContent);

                            // 保存引用以备将来使用
                            this.currentSubTask = subTask;

                            // 将子任务设置为已完成
                            taskManager.completeSubTask(subTask);

                            Minecraft.getInstance().player.displayClientMessage(
                                    new TextComponent("Summary saved and task marked as completed: " + subTask.getTitle()), true
                            );

                            // 可以选择关闭界面或留在界面上
                            // this.onClose();
                            return;
                        }
                    }
                }

                Minecraft.getInstance().player.displayClientMessage(
                        new TextComponent("No sub-task found with the matching title."), true
                );
            }
        } else {
            Minecraft.getInstance().player.displayClientMessage(
                    new TextComponent("Summary is empty! Please write a summary before saving."), true
            );
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        double screenWidth = this.width;

        if (mouseX < screenWidth * 3 / 5.0) {
            // 鼠标在屏幕左侧区域内（聊天区域）
            return chatPanel.mouseScrolled(mouseX, mouseY, scroll);
        }

        return false;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 绘制标题和分区标题
        drawCenteredString(poseStack, this.font, TITLE, this.width / 2, 10, 0xFFFFFF);
        drawString(poseStack, this.font, "与" + currentNPC.getNPCName() + "对话", 20, 20, 0xFFFFFF);
        drawString(poseStack, this.font, "访谈总结", this.width * 3 / 5 + 20, 20, 0xFFFFFF);

        // 渲染各个面板和组件
        this.chatPanel.render(poseStack, mouseX, mouseY, partialTicks);
        this.summaryEditor.render(poseStack, mouseX, mouseY, partialTicks);
        this.inputField.render(poseStack, mouseX, mouseY, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}