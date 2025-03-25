package item.knowledge;

import item.knowledge.data.QuestionRepository;

// 题目物品工厂
public class QuestionItemFactory {
    // 创建多选题物品
    public static QuestionItem createMultipleChoiceItem(String questionId) {
        return new QuestionItem(questionId);
    }

    // 创建匹配题物品
    public static QuestionItem createMatchingItem(String questionId) {
        return new QuestionItem(questionId);
    }

    // 创建排序题物品
    public static QuestionItem createOrderingItem(String questionId) {
        return new QuestionItem(questionId);
    }

    // 创建判断题物品
    public static QuestionItem createTrueFalseItem(String questionId) {
        return new QuestionItem(questionId);
    }
}