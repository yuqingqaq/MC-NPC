package gui.academic.writting;

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

public class PaperEditorScreen extends Screen {
    private EditBox paperTitleField;         // 论文标题输入框
    private TextEditorWidget paperBodyField; // 使用 TextEditorWidget
    private EditBox userQuestionField;       // 问题输入框
    private Button getAdviceButton;          // 获取建议按钮
    private HintScrollPanel advicePanel;     // 建议展示面板
    private List<String> adviceHistory;      // GPT 返回的建议历史

    private NPCModel currentNPC; // 当前 NPC 对象

    public PaperEditorScreen(NPCModel npc) {
        super(new TextComponent("Paper Editor with " + npc.getNPCName()));
        this.currentNPC = npc; // 将传入的 NPC 对象赋值给 currentNPC
        this.adviceHistory = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        int centerY = this.height / 2;

        // 屏幕宽度三等分
        int leftPanelWidth = (this.width * 3) / 5; // 左侧占 3/5
        int rightPanelWidth = (this.width * 2) / 5; // 右侧占 2/5

        // 论文标题输入框
        this.paperTitleField = new EditBox(this.font, 20, centerY - 100, leftPanelWidth - 40, 20, new TextComponent("Enter Paper Title"));
        this.addWidget(this.paperTitleField);

        // 正文输入框
        this.paperBodyField = new TextEditorWidget(20, centerY - 70, leftPanelWidth - 40, 150);
        this.addRenderableWidget(this.paperBodyField);

        // 用户问题输入框
        this.userQuestionField = new EditBox(this.font, 40, centerY + 90, leftPanelWidth - 30, 20, new TextComponent("Enter Your Question"));
        this.addWidget(this.userQuestionField);

        // 获取建议按钮
        this.getAdviceButton = this.addRenderableWidget(new Button(leftPanelWidth + 20, centerY + 90, 100, 20, new TextComponent("Get Advice"), button -> {
            generateAdvice();
        }));

        // 建议展示面板
        Minecraft mc = Minecraft.getInstance();

        // ScrollPanel 的相关参数
        int advicePanelHeight = this.height - 60; // 面板高度
        int advicePanelTop = 20;                  // 面板顶部位置
        int advicePanelLeft = this.width - rightPanelWidth - 5; // 面板左侧位置
        int advicePanelBorder = 5;               // 面板边框大小
        int scrollBarWidth = 5;                  // 滚动条宽度

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
        String title = this.paperTitleField.getValue().trim();      // 获取论文标题
        String body = this.paperBodyField.getText().trim();         // 获取论文正文
        String userQuestion = this.userQuestionField.getValue().trim(); // 获取用户提问

        // 如果标题、正文或提问内容任意一个不为空，则生成建议
        if (!title.isEmpty() || !body.isEmpty() || !userQuestion.isEmpty()) {
            // 将标题、正文和提问内容转化
            String markdownContent = buildPlainTextContent(title, body, userQuestion);

            // 使用当前 NPC 的聊天记录作为上下文
            String chatHistoryMarkdown = "```\n" + currentNPC.getChatHistoryAsString() + "\n```";

            // 调用 GPT 接口获取建议
            String advice = GameController.getInstance().interactWithExpert(currentNPC, chatHistoryMarkdown + markdownContent);

            // 添加分隔线到建议历史
            adviceHistory.add(advice);
            adviceHistory.add("----------------------"); // 分隔符
            adviceHistory.add(""); // 添加空行用于分隔建议

            // 刷新建议面板
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

        // 渲染标题输入框
        this.paperTitleField.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染用户问题输入框
        this.userQuestionField.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染正文输入框
        this.paperBodyField.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染建议面板
        this.advicePanel.render(poseStack, mouseX, mouseY, partialTicks);

        // 绘制标题文字
        drawCenteredString(poseStack, this.font, "Paper Editor with " + currentNPC.getNPCName(), this.width / 2 - 100, 5, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "GPT Advice", this.width - (this.width / 6) - 20, 5, 0xFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        // 鼠标滚动事件，控制建议面板滚动
        return this.advicePanel.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}