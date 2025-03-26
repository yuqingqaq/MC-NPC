package gui.academic.knowledge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.ContentPanel;
import controller.GameController;
import item.knowledge.QuestionClientHandler;
import item.knowledge.data.QuestionData;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class QuestionScreen extends Screen {
    private static final int FEEDBACK_HEIGHT = 80; // 反馈面板高度
    private static final int PANEL_TOP_MARGIN = 40; // 面板顶部边距 (调整减小)
    private static final int PANEL_BOTTOM_MARGIN = 50; // 面板底部边距
    private static final int SUBMIT_BUTTON_HEIGHT = 20; // 提交按钮高度
    private static final int NEXT_BUTTON_HEIGHT = 20; // 下一题按钮高度
    private static final ResourceLocation NPC_TEXTURE = new ResourceLocation("npcopenai:textures/entity/npc_expert.png");

    private final String title;
    private final NPCModel npcModel;

    private ContentPanel titlePanel;          // 标题面板
    private BaseQuestionPanel questionPanel;  // 当前题目面板 - 可以切换不同类型
    private FeedbackPanel feedbackPanel;      // 反馈面板
    private Button submitButton;              // 提交按钮
    private Button nextButton;                // 下一题按钮

    private boolean showFeedback = false;     // 是否显示反馈面板
    private boolean answerCorrect = false;    // 当前答案是否正确
    private int currentQuestionIndex = 1;     // 当前题目序号
    private int totalQuestions = 1;           // 总题目数量
    private QuestionData questionData;        // 当前题目数据

    public QuestionScreen(String title, NPCModel npcModel) {
        super(new TextComponent(title));
        this.title = title;
        this.npcModel = npcModel;
    }

    // 设置题目面板，根据不同题型可以设置不同的Panel
    public void setQuestionPanel(BaseQuestionPanel panel) {
        this.questionPanel = panel;
        // 需要重新初始化界面
        if (this.minecraft != null) {
            this.init(this.minecraft, this.width, this.height);
        }
    }

    // 设置当前题目数据
    public void setQuestionData(QuestionData questionData) {
        this.questionData = questionData;
    }

    // 设置进度信息
    public void setProgressInfo(int currentIndex, int totalQuestions) {
        this.currentQuestionIndex = currentIndex;
        this.totalQuestions = totalQuestions;
    }

    // 为Panel提供添加组件的方法
    public <T extends AbstractWidget> T addPanelWidget(T widget) {
        return this.addRenderableWidget(widget);
    }

    // 为Panel提供绘制背景的方法
    public void renderPanelBackground(PoseStack poseStack, int x, int y, int width, int height) {
        fill(poseStack, x, y, x + width, y + height, 0x80202020); // 使用单色背景
    }

    // 获取Minecraft实例的公共方法
    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    protected void init() {
        super.init();

        // 标题面板 - 显示在最上方，减小高度
        this.titlePanel = new ContentPanel(
                Minecraft.getInstance(),
                this.width - 40,
                30, // 减小高度
                20,
                5, // 调整上边距
                5,
                5
        );
        List<String> titleContent = new ArrayList<>();
        titleContent.add(title);
        this.titlePanel.setContent(titleContent);

        // 计算问题面板的高度
        int panelHeight = this.height - PANEL_TOP_MARGIN - PANEL_BOTTOM_MARGIN;

        // 初始化问题面板 - 如果已设置
        if (questionPanel != null) {
            questionPanel.init(this, 20, PANEL_TOP_MARGIN, this.width - 40, panelHeight);

            // 使用Panel提供的方法获取提交按钮位置
            int submitY = questionPanel.getSubmitButtonY();

            // 确保提交按钮不会太靠近底部或太远
            submitY = Math.min(Math.max(submitY, this.height - 100), this.height - 30);

            // 提交按钮 - 位置动态计算
            this.submitButton = this.addRenderableWidget(new Button(
                    this.width / 2 - 40,
                    submitY,
                    80,
                    SUBMIT_BUTTON_HEIGHT,
                    new TextComponent("Submit"),
                    button -> checkAnswer()
            ));

            // 下一题按钮 - 初始不可见
            this.nextButton = this.addRenderableWidget(new Button(
                    this.width / 2 - 40,
                    submitY + SUBMIT_BUTTON_HEIGHT + 5,
                    80,
                    NEXT_BUTTON_HEIGHT,
                    new TextComponent("Next Question"),
                    button -> goToNextQuestion()
            ));
            this.nextButton.visible = false;
        }

        // 初始化反馈面板 - 但默认不显示
        this.feedbackPanel = new FeedbackPanel(
                this.width - 40,
                FEEDBACK_HEIGHT,
                20,
                this.height - FEEDBACK_HEIGHT - 10
        );

        // 关闭按钮
        this.addRenderableWidget(new Button(
                this.width - 30,
                5,
                20,
                20,
                new TextComponent("X"),
                button -> onClose()
        ));
    }

    private void checkAnswer() {
        if (questionPanel == null) return;

        boolean isCorrect = questionPanel.isAnswerCorrect();
        answerCorrect = isCorrect;
        String feedback;

        if (isCorrect) {
            // 正确答案反馈
            feedback = questionPanel.getCorrectFeedback();

            // 执行正确后的逻辑 - 由各面板实现
            questionPanel.onCorrectAnswer();

            // 通知处理答题成功
            if (questionData != null) {
                QuestionClientHandler.handleQuestionSuccess(questionData);
            }

            // 显示反馈
            feedbackPanel.setFeedback(true, feedback);

            // 检查是否有下一题，如果有则显示下一题按钮
            if (questionData != null && !questionData.getNextQuestionId().isEmpty()) {
                nextButton.visible = true;
                submitButton.visible = false; // 隐藏提交按钮
            }
        } else {
            // 错误答案反馈
            feedback = questionPanel.getIncorrectFeedback();

            // 获取专家提示
            String expertHint = GameController.getInstance().interactWithExpert(
                    npcModel,
                    questionPanel.getHintPrompt()
            );

            // 显示反馈
            feedbackPanel.setFeedback(false, feedback + "\n\nHint: " + expertHint);
        }

        showFeedback = true;
    }

    // 前往下一题
    private void goToNextQuestion() {
        if (questionData != null && !questionData.getNextQuestionId().isEmpty()) {
            // 请求下一题
            QuestionClientHandler.requestNextQuestion(questionData.getNextQuestionId());

            // 关闭当前屏幕
            onClose();
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 绘制标题面板
        this.titlePanel.render(poseStack, mouseX, mouseY, partialTicks);

        // 绘制进度信息
        String progressText = "Question " + currentQuestionIndex + " of " + totalQuestions;
        drawString(poseStack, Minecraft.getInstance().font, progressText,
                width / 2 - Minecraft.getInstance().font.width(progressText) / 2,
                PANEL_TOP_MARGIN - 15, 0xFFFFFF);

        // 绘制问题面板
        if (questionPanel != null) {
            questionPanel.render(poseStack, mouseX, mouseY, partialTicks);
        }

        // 绘制NPC形象
        //renderNPC(poseStack);

        // 绘制基本界面元素
        super.render(poseStack, mouseX, mouseY, partialTicks);

        // 如果需要，绘制反馈面板（置于最上层）
        if (showFeedback) {
            feedbackPanel.render(poseStack, mouseX, mouseY, partialTicks);
        }
    }

    // 绘制NPC形象
    private void renderNPC(PoseStack poseStack) {
        int npcSize = 60;
        int x = width - npcSize - 10;
        int y = height - npcSize - 10;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, NPC_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        blit(poseStack, x, y, 0, 0, npcSize, npcSize, npcSize, npcSize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 如果反馈面板可见，并且点击在反馈面板上，处理反馈面板的点击
        if (showFeedback && feedbackPanel.isMouseOver(mouseX, mouseY)) {
            if (feedbackPanel.isCorrect() && feedbackPanel.isCloseButtonClicked(mouseX, mouseY)) {
                // 正确答案的反馈面板，点击关闭按钮时
                if (questionData != null && !questionData.getNextQuestionId().isEmpty()) {
                    // 如果有下一题，不关闭整个屏幕，而是关闭反馈面板
                    showFeedback = false;
                } else {
                    // 如果没有下一题，关闭整个屏幕
                    onClose();
                }
                return true;
            } else if (!feedbackPanel.isCorrect() && feedbackPanel.isCloseButtonClicked(mouseX, mouseY)) {
                // 错误答案的反馈面板，点击关闭按钮时只关闭反馈面板
                showFeedback = false;
                return true;
            }
            return false;
        }

        // 如果反馈面板可见，不处理其他点击
        if (showFeedback) return false;

        // 处理问题面板的点击
        if (questionPanel != null && questionPanel.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (questionPanel != null) {
            return questionPanel.mouseScrolled(mouseX, mouseY, delta);
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}