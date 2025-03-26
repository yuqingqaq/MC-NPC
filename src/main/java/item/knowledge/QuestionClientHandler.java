package item.knowledge;


import item.knowledge.data.*;
import gui.academic.knowledge.*;
import model.NPCModel;
import net.minecraft.client.Minecraft;

public class QuestionClientHandler {
    public static void openQuestionScreen(QuestionData questionData, NPCModel npcModel) {
        // 创建适合的QuestionScreen实例
        QuestionScreen screen = new QuestionScreen(
                "Agent Knowledge - " + questionData.getCategory(),
                npcModel
        );

        // 根据题目类型创建对应的Panel
        BaseQuestionPanel panel = createPanel(questionData);

        // 设置Panel到Screen并显示
        if (panel != null) {
            screen.setQuestionPanel(panel);
            Minecraft.getInstance().setScreen(screen);
        }
    }

    private static BaseQuestionPanel createPanel(QuestionData questionData) {
        if (questionData instanceof MultipleChoiceData) {
            return createMultipleChoicePanel((MultipleChoiceData) questionData);
        } else if (questionData instanceof MatchingData) {
            return createMatchingPanel((MatchingData) questionData);
        } else if (questionData instanceof OrderingData) {
            return createOrderingPanel((OrderingData) questionData);
        } else if (questionData instanceof TrueFalseData) {
            return createTrueFalsePanel((TrueFalseData) questionData);
        }
        return null;
    }

    private static MultipleChoicePanel createMultipleChoicePanel(MultipleChoiceData data) {
        return new MultipleChoicePanel(
                data.getConceptName(),
                data.getQuestion(),
                data.getOptions(),
                data.getCorrectOption()
        );
    }

    private static MatchingPanel createMatchingPanel(MatchingData data) {
        return new MatchingPanel(
                data.getConceptCategory(),
                data.getLeftItems(),
                data.getRightItems(),
                data.getCorrectMatches()
        );
    }

    private static OrderingPanel createOrderingPanel(OrderingData data) {
        return new OrderingPanel(
                data.getOrderingTopic(),
                data.getCorrectOrder()
        );
    }

    private static TrueFalsePanel createTrueFalsePanel(TrueFalseData data) {
        return new TrueFalsePanel(
                data.getStatement(),
                data.isTrue(),
                data.getExplanation()
        );
    }
}