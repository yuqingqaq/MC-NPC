package item.knowledge;

import controller.KnowledgeGraphManager;
import item.knowledge.data.*;
import gui.academic.knowledge.*;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import npcopenai.NPCOpenAI;
import registry.ItemRegistry;

import java.util.HashMap;
import java.util.Map;

public class QuestionClientHandler {

    // 缓存每种问题类型的总数量
    private static Map<String, Integer> questionTypeCounts = new HashMap<>();

    static {
        // 初始化各类题目的数量
        questionTypeCounts.put("MULTIPLE_CHOICE", 4); // 4个概念题
        questionTypeCounts.put("MATCHING", 3);        // 3个关系题
        questionTypeCounts.put("TRUE_FALSE", 2);      // 2个原则题
        questionTypeCounts.put("ORDERING", 1);        // 1个时间线题
    }

    public static void openQuestionScreen(QuestionData questionData, NPCModel npcModel) {
        // 创建适合的QuestionScreen实例
        QuestionScreen screen = new QuestionScreen(
                questionData.getCategory(),
                npcModel
        );

        // 计算当前题目的索引和总数
        int currentIndex = getCurrentQuestionIndex(questionData.getId());
        int totalQuestions = getTotalQuestionsForType(questionData.getQuestionType());

        // 设置进度信息
        screen.setProgressInfo(currentIndex, totalQuestions);

        // 根据题目类型创建对应的Panel
        BaseQuestionPanel panel = createPanel(questionData);

        // 设置Panel到Screen并显示
        if (panel != null) {
            screen.setQuestionPanel(panel);
            screen.setQuestionData(questionData);
            Minecraft.getInstance().setScreen(screen);
        }
    }

    // 获取当前题目的索引
    private static int getCurrentQuestionIndex(String questionId) {
        // 默认为第1题
        int currentIndex = 1;

        // 解析ID中的数字
        if (questionId.contains("_")) {
            String[] parts = questionId.split("_");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                if (lastPart.matches("\\d+")) {
                    try {
                        currentIndex = Integer.parseInt(lastPart);
                    } catch (NumberFormatException e) {
                        // 忽略解析错误
                    }
                }
            }
        }

        return currentIndex;
    }

    // 获取指定类型的总题目数
    private static int getTotalQuestionsForType(String questionType) {
        return questionTypeCounts.getOrDefault(questionType, 1);
    }

    private static BaseQuestionPanel createPanel(QuestionData questionData) {
        if (questionData instanceof MultipleChoiceData) {
            return createMultipleChoicePanel((MultipleChoiceData) questionData);
        } else if (questionData instanceof MatchingData) {
            return createMatchingPanel((MatchingData) questionData);
        } else if (questionData instanceof OrderingData) {
            return createOrderingPanel((OrderingData) questionData);
        } else if (questionData instanceof TrueFalseData) {
            return createTrueFalsePanel((TrueFalseData) questionData);
        }
        return null;
    }

    private static MultipleChoicePanel createMultipleChoicePanel(MultipleChoiceData data) {
        return new MultipleChoicePanel(
                data.getConceptName(),
                data.getQuestion(),
                data.getOptions(),
                data.getCorrectOption()
        );
    }

    private static MatchingPanel createMatchingPanel(MatchingData data) {
        return new MatchingPanel(
                data.getConceptCategory(),
                data.getLeftItems(),
                data.getRightItems(),
                data.getCorrectMatches()
        );
    }

    private static OrderingPanel createOrderingPanel(OrderingData data) {
        return new OrderingPanel(
                data.getOrderingTopic(),
                data.getCorrectOrder()
        );
    }

    private static TrueFalsePanel createTrueFalsePanel(TrueFalseData data) {
        return new TrueFalsePanel(
                data.getStatement(),
                data.isTrue(),
                data.getExplanation()
        );
    }

    // 处理答题成功
    public static void handleQuestionSuccess(QuestionData questionData) {
        // 根据题目类型更新知识图谱
        String questionType = questionData.getQuestionType();
        String nextQuestionId = questionData.getNextQuestionId();

        switch (questionType) {
            case "MULTIPLE_CHOICE":
                if (questionData instanceof MultipleChoiceData) {
                    KnowledgeGraphManager.getInstance().addConcept(
                            ((MultipleChoiceData) questionData).getConceptName());
                }
                break;
            case "MATCHING":
                if (questionData instanceof MatchingData) {
                    KnowledgeGraphManager.getInstance().addRelationship(
                            ((MatchingData) questionData).getConceptCategory());
                }
                break;
            case "TRUE_FALSE":
                KnowledgeGraphManager.getInstance().addPrinciple(questionData.getCategory());
                break;
            case "ORDERING":
                KnowledgeGraphManager.getInstance().addTimeline(
                        ((OrderingData) questionData).getOrderingTopic());
                break;
        }

        // 如果有下一题，请求服务器生成下一题物品
        if (nextQuestionId != null && !nextQuestionId.isEmpty()) {
            requestNextQuestion(nextQuestionId);
        }
    }
    // 请求下一题
    public static void requestNextQuestion(String nextQuestionId) {
        // 这里实际上需要通过网络包向服务器发送请求
        NPCOpenAI.getLogger().info("Next question requested: " + nextQuestionId);

        // 获取下一题数据
        QuestionData nextQuestionData = QuestionRepository.getQuestionById(nextQuestionId);
        if (nextQuestionData == null) {
            NPCOpenAI.getLogger().error("Next question data not found: " + nextQuestionId);
            return;
        }

        // 创建NPC模型
        NPCModel npcModel = new NPCModel(nextQuestionData.getExpertType());

        // 打开下一题的界面
        // 使用延迟执行，确保当前界面正确关闭
        Minecraft.getInstance().tell(() -> {
            openQuestionScreen(nextQuestionData, npcModel);
        });

        // 实际网络实现示例：
        // NetworkHandler.sendToServer(new RequestNextQuestionPacket(nextQuestionId));
    }
}