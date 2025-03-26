package item.knowledge.data;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceData extends QuestionData {
    private final String conceptName;
    private final String question;
    private final List<String> options;
    private final int correctOption;

    public MultipleChoiceData(String id, String category, String expertType, String conceptName,
                              String question, List<String> options, int correctOption) {
        super(id, category, expertType);
        this.conceptName = conceptName;
        this.question = question;
        this.options = new ArrayList<>(options);
        this.correctOption = correctOption;
    }

    public MultipleChoiceData(String id, String category, String expertType, String conceptName,
                              String question, List<String> options, int correctOption, String nextQuestionId) {
        super(id, category, expertType, nextQuestionId);
        this.conceptName = conceptName;
        this.question = question;
        this.options = new ArrayList<>(options);
        this.correctOption = correctOption;
    }

    public String getConceptName() {
        return conceptName;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    public int getCorrectOption() {
        return correctOption;
    }

    @Override
    public String getQuestionType() {
        return "MULTIPLE_CHOICE";
    }
}