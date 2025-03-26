package item.knowledge.data;

// 判断题数据
public class TrueFalseData extends QuestionData {
    private final String statement;
    private final boolean isTrue;
    private final String explanation;

    public TrueFalseData(String id, String category, String expertType,
                         String statement, boolean isTrue, String explanation) {
        super(id, category, expertType);
        this.statement = statement;
        this.isTrue = isTrue;
        this.explanation = explanation;
    }

    @Override
    public String getQuestionType() {
        return "true_false";
    }

    public String getStatement() {
        return statement;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public String getExplanation() {
        return explanation;
    }
}