package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoicePanel extends BaseQuestionPanel {
    private final String conceptName;          // 概念名称
    private final String question;             // 问题
    private final List<String> options;        // 选项
    private final int correctOption;           // 正确选项索引

    private List<Button> optionButtons = new ArrayList<>();
    private int selectedOption = -1;           // 当前选中的选项

    // 布局常量
    private static final int OPTION_HEIGHT = 25; // 选项间距

    public MultipleChoicePanel(String conceptName, String question,
                               List<String> options, int correctOption) {
        this.conceptName = conceptName;
        this.question = question;
        this.options = new ArrayList<>(options); // 创建副本以防止外部修改
        this.correctOption = correctOption;
    }

    @Override
    protected void initPanel() {
        // 清空按钮列表
        optionButtons.clear();

        // 添加选项按钮
        for (int i = 0; i < options.size(); i++) {
            final int optionIndex = i;
            Button optionButton = addButton(
                    x + width / 2 - 150,  // 居中
                    y + CONTENT_START_Y + (i * OPTION_HEIGHT),
                    300,  // 更宽的按钮
                    20,
                    new TextComponent(options.get(i)),
                    button -> selectOption(optionIndex)
            );
            optionButtons.add(optionButton);
        }
    }

    private void selectOption(int index) {
        // 检查索引是否有效
        if (index < 0 || index >= options.size()) {
            return; // 索引无效，不做任何处理
        }

        // 重置所有按钮样式
        for (int i = 0; i < optionButtons.size(); i++) {
            optionButtons.get(i).setMessage(new TextComponent(options.get(i)));
        }

        // 高亮选中的按钮
        optionButtons.get(index).setMessage(new TextComponent("✓ " + options.get(index)));
        selectedOption = index;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        renderBackground(poseStack);

        // 绘制标题和问题
        String title = "概念： " + conceptName;
        renderTitleAndInstructions(poseStack, title, question);
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
    public int getContentHeight() {
        // 计算内容高度
        return options.size() * OPTION_HEIGHT;
    }

    @Override
    public boolean isAnswerCorrect() {
        return selectedOption == correctOption;
    }

    @Override
    public String getCorrectFeedback() {
        return "正确！您已掌握此概念。" ;
    }
    
    @Override
    public String getIncorrectFeedback() {
        if(GameController.getInstance().isSRLQuestAvailable()){
            return "不太正确。" +
                    "正确答案是：" + options.get(correctOption);
        }
        return "不太正确。";
    }
    
    @Override
    public String getHintPrompt() {
        return "解释原因 \"" + options.get(correctOption) +
        "\" 是 " + conceptName + " 的正确定义";
    }

    @Override
    public void onCorrectAnswer() {
        // 添加概念到知识图谱
        GameController.getInstance().addConceptToKnowledgeGraph(conceptName);
    }
}