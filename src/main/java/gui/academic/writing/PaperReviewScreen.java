package gui.academic.writing;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ContentPanel;
import component.HintScrollPanel;
import component.TextEditorWidget;
import controller.GameController;
import model.AdaptiveSubTaskModel;
import model.AdaptiveTaskModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import model.NPCModel;
import system.TaskManager;
import system.UIScreenManager;

import java.util.ArrayList;
import java.util.List;

public class PaperReviewScreen extends Screen {
    private static final String TITLE = "Paper Review";

    private ContentPanel paperContentPanel; // 用于显示论文内容的滚动面板
    private TextEditorWidget textEditor;        // 可编辑区域（正文）
    private EditBox questionInput;              // 用户输入问题的文本框
    private Button saveButton;                  // 保存按钮
    private Button getAdviceButton;             // 获取建议按钮
    private HintScrollPanel hintPanel;          // 显示建议的右侧面板
    private List<String> hintHistory;           // 保存建议历史记录

    private final String subTaskTitle;          // 当前任务标题
    private final List<String> paperContent;    // 论文内容
    private final NPCModel npcModel;            // 当前的 NPC 模型
    private AdaptiveSubTaskModel currentSubTask; // 当前子任务的引用

    public PaperReviewScreen(String subTaskTitle, List<String> paperContent, NPCModel npcModel) {
        super(new TextComponent(TITLE));
        this.subTaskTitle = subTaskTitle;
        this.paperContent = paperContent != null ? paperContent : new ArrayList<>();
        this.npcModel = npcModel;
        this.hintHistory = new ArrayList<>();

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

        // 屏幕宽度划分
        int leftPanelWidth = (this.width * 3) / 5;
        int rightPanelWidth = (this.width * 2) / 5;

        // 左侧上方：显示论文内容的滚动面板
        this.paperContentPanel= new ContentPanel(
                Minecraft.getInstance(),
                250,   // 面板宽度
                85,   // 面板高度
                20,    // 面板顶部位置
                20,    // 面板左侧位置
                5,     // 面板边框大小
                5
        );
        // 设置论文内容
        this.paperContentPanel.setContent(this.paperContent);

        // 左侧中间：正文编辑器
        this.textEditor = new TextEditorWidget(
                20,
                centerY - 10,
                leftPanelWidth - 40,
                70
        );
        this.addRenderableWidget(this.textEditor);

        // 加载已有的outcome
        loadExistingOutcome();

        // 左侧下方：问题输入框
        this.questionInput = new EditBox(
                this.font,
                20,
                centerY + 90,
                leftPanelWidth - 40,
                20,
                new TextComponent("Enter your question here...")
        );
        this.addWidget(this.questionInput);

        // 左侧下方：保存按钮
        this.saveButton = this.addRenderableWidget(new Button(
                leftPanelWidth - 100,
                centerY + 65,
                80,
                20,
                new TextComponent("Save"),
                button -> saveOutcome()
        ));

        // 右侧上方：获取建议按钮
        this.getAdviceButton = this.addRenderableWidget(new Button(
                this.width - rightPanelWidth + 10,
                centerY + 90,
                100,
                20,
                new TextComponent("Get Advice"),
                button -> generateAdvice()
        ));

        // 右侧：显示建议历史的面板
        int hintPanelHeight = this.height - 60;
        this.hintPanel = new HintScrollPanel(
                Minecraft.getInstance(),
                rightPanelWidth - 20,
                hintPanelHeight,
                20,
                this.width - rightPanelWidth - 10,
                4,
                5,
                hintHistory
        );

        // 添加关闭按钮
        this.addRenderableWidget(new Button(
                this.width - 30,
                5,
                20,
                20,
                new TextComponent("X"),
                button -> onClose()
        ));
    }

    // 加载已有的outcome到编辑器
    private void loadExistingOutcome() {
        if (currentSubTask != null) {
            String outcome = currentSubTask.getOutcome();
            if (outcome != null && !outcome.isEmpty()) {
                textEditor.setText(outcome);

                // 添加一条信息到提示历史，指示已加载之前的工作
                hintHistory.add("Previous work loaded for task: " + currentSubTask.getTitle());
                hintHistory.add("Status: " + currentSubTask.getStatus().toString());
                hintHistory.add("----------------------");
                hintHistory.add("");
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);

    }

    private void saveOutcome() {
        String editorContent = textEditor.getText().trim();
        if (!editorContent.isEmpty()) {
            TaskManager taskManager = TaskManager.getInstance();
            if (currentSubTask != null) {
                currentSubTask.setOutcome(editorContent);
                taskManager.completeSubTask(currentSubTask);

                Minecraft.getInstance().player.displayClientMessage(
                        new TextComponent("Outcome saved for sub-task: " + currentSubTask.getTitle()), true
                );
            } else {
                // 如果在构造函数中没有找到当前子任务，再次尝试查找

                for (AdaptiveTaskModel task : taskManager.getTasks()) {
                    for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                        if (subTask.getTitle().equals(subTaskTitle)) {
                            subTask.setOutcome(editorContent);
                            this.currentSubTask = subTask; // 保存引用以备将来使用
                            taskManager.completeSubTask(subTask);
                            Minecraft.getInstance().player.displayClientMessage(
                                    new TextComponent("Outcome saved for sub-task: " + subTask.getTitle()), true
                            );
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
                    new TextComponent("Editor content is empty!"), true
            );
        }
    }

    private void generateAdvice() {
        String question = questionInput.getValue().trim(); // 获取用户输入的问题
        String summary = textEditor.getText().trim(); // 获取用户输入的摘要

        // 检查所有输入是否为空
        if (question.isEmpty() && summary.isEmpty() && (paperContent == null || paperContent.isEmpty())) {
            Minecraft.getInstance().player.displayClientMessage(new TextComponent("Please enter a question, summary, or paper content!"), true);
            return;
        }

        // 将 paperContent 的每一行拼接成一个多行字符串
        String paperContentText = "";
        if (paperContent != null && !paperContent.isEmpty()) {
            paperContentText = "Paper Content:\n" + String.join("\n", paperContent);
        }

        // 拼接所有输入内容
        String combinedInput =
                (question.isEmpty() ? "" : "Question: " + question + "\n") +
                        (summary.isEmpty() ? "" : "Summary: " + summary + "\n") +
                        (paperContentText.isEmpty() ? "" : paperContentText);

        // 与专家 NPC 交互生成建议
        String advice = GameController.getInstance().interactWithExpert(npcModel, combinedInput);

        // 将建议和分隔符添加到历史记录中
        hintHistory.add("Advice: " + advice);
        hintHistory.add("----------------------");

        // 刷新提示面板
        this.hintPanel.refreshPanel();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        drawCenteredString(poseStack, this.font, TITLE, this.width / 2, 10, 0xFFFFFF);

        this.paperContentPanel.render(poseStack, mouseX, mouseY, partialTicks);
        this.textEditor.render(poseStack, mouseX, mouseY, partialTicks);
        this.questionInput.render(poseStack, mouseX, mouseY, partialTicks);
        this.hintPanel.render(poseStack, mouseX, mouseY, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        double screenWidth = this.width; // 假设 'this.width' 是屏幕宽度

        if (mouseX < screenWidth * 2 / 3.0) {
            // 鼠标在屏幕左侧2/3区域内
            return paperContentPanel.mouseScrolled(mouseX, mouseY, scroll);
        } else {
            // 鼠标在屏幕右侧1/3区域内
            return hintPanel.mouseScrolled(mouseX, mouseY, scroll);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}