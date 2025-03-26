package item.knowledge.data;

// 题目数据基类
public abstract class QuestionData {
    private final String id;           // 题目唯一ID
    private final String category;     // 题目分类
    private final String expertType;   // 专家类型
    private String nextQuestionId;     // 下一个题目的ID

    public QuestionData(String id, String category, String expertType) {
        this(id, category, expertType, "");
    }

    public QuestionData(String id, String category, String expertType, String nextQuestionId) {
        this.id = id;
        this.category = category;
        this.expertType = expertType;
        this.nextQuestionId = nextQuestionId;
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

    public String getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(String nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }

    public abstract String getQuestionType();
}