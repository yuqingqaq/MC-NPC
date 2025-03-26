package item.knowledge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MatchingData extends QuestionData {
    private final String conceptCategory;
    private final List<String> leftItems;
    private final List<String> rightItems;
    private final Map<Integer, Integer> correctMatches;

    public MatchingData(String id, String category, String expertType, String conceptCategory,
                        List<String> leftItems, List<String> rightItems,
                        Map<Integer, Integer> correctMatches) {
        super(id, category, expertType);
        this.conceptCategory = conceptCategory;
        this.leftItems = new ArrayList<>(leftItems);
        this.rightItems = new ArrayList<>(rightItems);
        this.correctMatches = new HashMap<>(correctMatches);
    }

    public MatchingData(String id, String category, String expertType, String conceptCategory,
                        List<String> leftItems, List<String> rightItems,
                        Map<Integer, Integer> correctMatches, String nextQuestionId) {
        super(id, category, expertType, nextQuestionId);
        this.conceptCategory = conceptCategory;
        this.leftItems = new ArrayList<>(leftItems);
        this.rightItems = new ArrayList<>(rightItems);
        this.correctMatches = new HashMap<>(correctMatches);
    }

    public String getConceptCategory() {
        return conceptCategory;
    }

    public List<String> getLeftItems() {
        return new ArrayList<>(leftItems);
    }

    public List<String> getRightItems() {
        return new ArrayList<>(rightItems);
    }

    public Map<Integer, Integer> getCorrectMatches() {
        return new HashMap<>(correctMatches);
    }

    @Override
    public String getQuestionType() {
        return "MATCHING";
    }
}