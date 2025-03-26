package item.knowledge.data;

import java.util.List;

// 排序题数据
public class OrderingData extends QuestionData {
    private final String orderingTopic;
    private final List<String> correctOrder;

    public OrderingData(String id, String category, String expertType,
                        String orderingTopic, List<String> correctOrder) {
        super(id, category, expertType);
        this.orderingTopic = orderingTopic;
        this.correctOrder = correctOrder;
    }

    @Override
    public String getQuestionType() {
        return "ordering";
    }

    public String getOrderingTopic() {
        return orderingTopic;
    }

    public List<String> getCorrectOrder() {
        return correctOrder;
    }
}