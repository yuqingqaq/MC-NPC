package item.knowledge.data;

import java.util.ArrayList;
import java.util.List;

public class OrderingData extends QuestionData {
    private final String orderingTopic;
    private final List<String> correctOrder;

    public OrderingData(String id, String category, String expertType, String orderingTopic,
                        List<String> correctOrder) {
        super(id, category, expertType);
        this.orderingTopic = orderingTopic;
        this.correctOrder = new ArrayList<>(correctOrder);
    }

    public OrderingData(String id, String category, String expertType, String orderingTopic,
                        List<String> correctOrder, String nextQuestionId) {
        super(id, category, expertType, nextQuestionId);
        this.orderingTopic = orderingTopic;
        this.correctOrder = new ArrayList<>(correctOrder);
    }

    public String getOrderingTopic() {
        return orderingTopic;
    }

    public List<String> getCorrectOrder() {
        return new ArrayList<>(correctOrder);
    }

    @Override
    public String getQuestionType() {
        return "ORDERING";
    }
}