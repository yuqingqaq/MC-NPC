package item.knowledge.data;


import java.util.List;
import java.util.Map;

// 匹配题数据
public class MatchingData extends QuestionData {
    private final String conceptCategory;
    private final List<String> leftItems;
    private final List<String> rightItems;
    private final Map<Integer, Integer> correctMatches;

    public MatchingData(String id, String category, String expertType,
                        String conceptCategory, List<String> leftItems,
                        List<String> rightItems, Map<Integer, Integer> correctMatches) {
        super(id, category, expertType);
        this.conceptCategory = conceptCategory;
        this.leftItems = leftItems;
        this.rightItems = rightItems;
        this.correctMatches = correctMatches;
    }

    @Override
    public String getQuestionType() {
        return "matching";
    }

    public String getConceptCategory() {
        return conceptCategory;
    }

    public List<String> getLeftItems() {
        return leftItems;
    }

    public List<String> getRightItems() {
        return rightItems;
    }

    public Map<Integer, Integer> getCorrectMatches() {
        return correctMatches;
    }
}