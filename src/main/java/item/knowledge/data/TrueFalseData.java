package item.knowledge.data;

public class TrueFalseData extends QuestionData {
    private final String statement;
    private final boolean isTrue;
    private final String explanation;

    public TrueFalseData(String id, String category, String expertType, String statement,
                         boolean isTrue, String explanation) {
        super(id, category, expertType);
        this.statement = statement;
        this.isTrue = isTrue;
        this.explanation = explanation;
    }

    public TrueFalseData(String id, String category, String expertType, String statement,
                         boolean isTrue, String explanation, String nextQuestionId) {
        super(id, category, expertType, nextQuestionId);
        this.statement = statement;
        this.isTrue = isTrue;
        this.explanation = explanation;
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

    @Override
    public String getQuestionType() {
        return "TRUE_FALSE";
    }
}