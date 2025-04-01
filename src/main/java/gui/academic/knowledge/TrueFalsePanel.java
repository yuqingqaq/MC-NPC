package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

public class TrueFalsePanel extends BaseQuestionPanel {
    private final String statement;            // 陈述句
    private final boolean isTrue;              // 陈述句是否正确
    private final String explanation;          // 解释

    private Button trueButton;                 // "正确"按钮
    private Button falseButton;                // "错误"按钮
    private Boolean userAnswer = null;         // 用户的回答

    // 布局常量
    private static final int STATEMENT_MARGIN = 20;
    private static final int BUTTONS_Y_OFFSET = 30 + 10;

    public TrueFalsePanel(String statement, boolean isTrue, String explanation) {
        this.statement = statement;
        this.isTrue = isTrue;
        this.explanation = explanation;
    }

    @Override
    protected void initPanel() {
        // 添加"正确"按钮
        this.trueButton = addButton(
                x + width / 3 - 40,
                y + CONTENT_START_Y + getStatementHeight() + BUTTONS_Y_OFFSET,
                80,
                20,
                new TextComponent("正确"),
                button -> selectAnswer(true)
        );

        // 添加"错误"按钮
        this.falseButton = addButton(
                x + 2 * width / 3 - 40,
                y + CONTENT_START_Y + getStatementHeight() + BUTTONS_Y_OFFSET,
                80,
                20,
                new TextComponent("错误"),
                button -> selectAnswer(false)
        );
    }

    private void selectAnswer(boolean answer) {
        // 重置按钮样式
        trueButton.setMessage(new TextComponent("正确"));
        falseButton.setMessage(new TextComponent("错误"));

        // 高亮选中的按钮
        if (answer) {
            trueButton.setMessage(new TextComponent("✓ 正确"));
        } else {
            falseButton.setMessage(new TextComponent("✓ 错误"));
        }

        userAnswer = answer;
    }

    // 计算陈述句高度
    private int getStatementHeight() {
        String[] lines = statement.split("\n");
        return lines.length * 15 + STATEMENT_MARGIN;
    }

    @Override
    public int getContentHeight() {
        return getStatementHeight() + BUTTONS_Y_OFFSET + 30; // 按钮高度 + 额外空间
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        renderBackground(poseStack);

        // 绘制标题
        renderTitleAndInstructions(poseStack, "判断下面这句话的正误:", "");

        // 绘制陈述句 - 居中显示
        String[] lines = statement.split("\n");
        for (int i = 0; i < lines.length; i++) {
            drawString(poseStack, lines[i],
                    x + 30,
                    y + CONTENT_START_Y + (i * 15),
                    0xFFFFFF);
        }

        // // 绘制提示
        // drawString(poseStack, "Is this statement TRUE or FALSE?",
        //         x + (width / 2) - 100,
        //         y + CONTENT_START_Y + getStatementHeight() + 10,
        //         0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false; // 按钮点击已由Screen处理
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false; // 不需要滚动
    }

    @Override
    public boolean isAnswerCorrect() {
        return userAnswer != null && userAnswer == isTrue;
    }

    @Override
    public String getCorrectFeedback() {
        return "正确！你的理解很准确。";
    }

    @Override
    public String getIncorrectFeedback() {
        return "这不太正确。" + "该语句实际上是" + (isTrue ? "正确" : "错误") + "解释：" + explanation;
    }

    @Override
    public String getHintPrompt() {
        return "简单解释一下为什么语句 " + statement + "是 " +(isTrue ? "正确" : "错误");
    }

    @Override
    public void onCorrectAnswer() {
        // 记录玩家验证了这个原则
        GameController.getInstance().validateAgentPrinciple(statement);
    }
}