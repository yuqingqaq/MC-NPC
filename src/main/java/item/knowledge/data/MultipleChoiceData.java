package item.knowledge.data;

import java.util.List;

// 多选题数据
public class MultipleChoiceData extends QuestionData {
    private final String conceptName;
    private final String question;
    private final List<String> options;
    private final int correctOption;

    public MultipleChoiceData(String id, String category, String expertType,
                              String conceptName, String question,
                              List<String> options, int correctOption) {
        super(id, category, expertType);
        this.conceptName = conceptName;
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    @Override
    public String getQuestionType() {
        return "multiple_choice";
    }

    public String getConceptName() {
        return conceptName;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }
}