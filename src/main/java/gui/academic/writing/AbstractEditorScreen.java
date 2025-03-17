package gui.academic.writing;

import com.mojang.blaze3d.vertex.PoseStack;
import component.HintScrollPanel;
import controller.GameController;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import component.TextEditorWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEditorScreen extends Screen {
    protected EditBox titleField;             // 标题输入框
    protected TextEditorWidget bodyField;     // 正文输入框
    protected EditBox questionField;          // 用户问题输入框
    protected Button getAdviceButton;         // 获取建议按钮
    protected HintScrollPanel advicePanel;    // 建议展示面板
    protected List<String> adviceHistory;     // GPT 返回的建议历史

    protected NPCModel currentNPC;            // 当前 NPC 对象

    public AbstractEditorScreen(NPCModel npc, String screenTitle) {
        super(new TextComponent(screenTitle));
        this.currentNPC = npc;
        this.adviceHistory = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        int centerY = this.height / 2;

        // 屏幕宽度三等分
        int leftPanelWidth = (this.width * 3) / 5;
        int rightPanelWidth = (this.width * 2) / 5;

        // 标题输入框
        this.titleField = new EditBox(this.font, 20, centerY - 100, leftPanelWidth - 40, 20, new TextComponent(getTitlePlaceholder()));
        this.addWidget(this.titleField);

        // 正文输入框
        this.bodyField = new TextEditorWidget(20, centerY - 70, leftPanelWidth - 40, 150);
        this.addRenderableWidget(this.bodyField);

        // 用户问题输入框
        this.questionField = new EditBox(this.font, 40, centerY + 90, leftPanelWidth - 30, 20, new TextComponent(getQuestionPlaceholder()));
        this.addWidget(this.questionField);

        // 获取建议按钮
        this.getAdviceButton = this.addRenderableWidget(new Button(leftPanelWidth + 20, centerY + 90, 100, 20, new TextComponent("Get Advice"), button -> {
            generateAdvice();
        }));

        // 建议展示面板
        Minecraft mc = Minecraft.getInstance();
        int advicePanelHeight = this.height - 60;
        int advicePanelTop = 20;
        int advicePanelLeft = this.width - rightPanelWidth - 5;
        int advicePanelBorder = 5;
        int scrollBarWidth = 5;

        this.advicePanel = new HintScrollPanel(mc, rightPanelWidth, advicePanelHeight, advicePanelTop, advicePanelLeft - 10, advicePanelBorder, scrollBarWidth, adviceHistory);

        // 添加关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 5, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }

    private void generateAdvice() {
        String title = this.titleField.getValue().trim();
        String body = this.bodyField.getText().trim();
        String question = this.questionField.getValue().trim();

        if (!title.isEmpty() || !body.isEmpty() || !question.isEmpty()) {
            String markdownContent = buildPlainTextContent(title, body, question);
            String chatHistoryMarkdown = "```\n" + currentNPC.getChatHistoryAsString() + "\n```";
            String advice = GameController.getInstance().interactWithExpert(currentNPC, chatHistoryMarkdown + markdownContent);

            adviceHistory.add(advice);
            adviceHistory.add("----------------------");
            adviceHistory.add("");

            this.advicePanel.refreshPanel();
        }
    }

    private String buildPlainTextContent(String title, String body, String question) {
        StringBuilder builder = new StringBuilder();
        if (!title.isEmpty()) {
            builder.append("Title: ").append(title).append("\n\n");
        }
        if (!body.isEmpty()) {
            builder.append("Body: \n").append(body).append("\n\n");
        }
        if (!question.isEmpty()) {
            builder.append("Question: ").append(question).append("\n");
        }
        return builder.toString();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        this.titleField.render(poseStack, mouseX, mouseY, partialTicks);
        this.questionField.render(poseStack, mouseX, mouseY, partialTicks);
        this.bodyField.render(poseStack, mouseX, mouseY, partialTicks);
        this.advicePanel.render(poseStack, mouseX, mouseY, partialTicks);

        drawCenteredString(poseStack, this.font, getScreenTitle(), this.width / 2 - 100, 5, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "GPT Advice", this.width - (this.width / 6) - 20, 5, 0xFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return this.advicePanel.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // 获取屏幕标题
    protected abstract String getScreenTitle();

    // 获取标题输入框的占位符
    protected abstract String getTitlePlaceholder();

    // 获取问题输入框的占位符
    protected abstract String getQuestionPlaceholder();
}