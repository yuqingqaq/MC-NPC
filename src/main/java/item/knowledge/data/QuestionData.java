package item.knowledge.data;

// 题目数据基类
public abstract class QuestionData {
    private final String id;           // 题目唯一ID
    private final String category;     // 题目分类
    private final String expertType;   // 专家类型

    public QuestionData(String id, String category, String expertType) {
        this.id = id;
        this.category = category;
        this.expertType = expertType;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getExpertType() {
        return expertType;
    }

    public abstract String getQuestionType();
}