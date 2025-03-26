package item.knowledge.data;


import java.util.*;

public class QuestionRepository {
    private static final Map<String, QuestionData> questionDataMap = new HashMap<>();

    static {
        // 初始化多选题数据
        initMultipleChoiceData();

        // 初始化匹配题数据
        initMatchingData();

        // 初始化排序题数据
        initOrderingData();

        // 初始化判断题数据
        initTrueFalseData();
    }

    private static void initMultipleChoiceData() {
        // Intelligent Agent概念题目
        MultipleChoiceData agentConcept = new MultipleChoiceData(
                "mc_agent_concept",
                "Agent Fundamentals",
                "Question Expert",
                "Intelligent Agent",
                "Which of the following best defines an 'Intelligent Agent'?",
                Arrays.asList(
                        "A computer program that performs tasks automatically",
                        "An entity that perceives its environment and takes actions to achieve goals",
                        "A robot with humanlike capabilities",
                        "Any software that can communicate with users"
                ),
                1  // 0-based index, second option is correct
        );
        questionDataMap.put(agentConcept.getId(), agentConcept);

        // 可以继续添加更多多选题...
        MultipleChoiceData agentTypes = new MultipleChoiceData(
                "mc_agent_types",
                "Agent Classification",
                "Agent Architecture Expert",
                "Reactive Agents",
                "What is the key characteristic of reactive agents?",
                Arrays.asList(
                        "They maintain complex internal models of the world",
                        "They use deep learning to make decisions",
                        "They respond directly to current environment perceptions",
                        "They communicate with other agents to make decisions"
                ),
                2  // 第三个选项是正确答案
        );
        questionDataMap.put(agentTypes.getId(), agentTypes);
    }

    private static void initMatchingData() {
        // Agent Learning Methods匹配题
        Map<Integer, Integer> learningMatches = new HashMap<>();
        learningMatches.put(0, 0); // Supervised -> labeled examples
        learningMatches.put(1, 1); // Unsupervised -> patterns without labels
        learningMatches.put(2, 2); // Reinforcement -> trial and error
        learningMatches.put(3, 3); // Transfer -> applying knowledge

        MatchingData learningMethods = new MatchingData(
                "match_learning_methods",
                "Agent Learning",
                "Question Expert",
                "Agent Learning Methods",
                Arrays.asList(
                        "Supervised Learning",
                        "Unsupervised Learning",
                        "Reinforcement Learning",
                        "Transfer Learning"
                ),
                Arrays.asList(
                        "Learning from labeled examples",
                        "Finding patterns without labels",
                        "Learning through trial and error",
                        "Applying knowledge from one task to another"
                ),
                learningMatches
        );
        questionDataMap.put(learningMethods.getId(), learningMethods);

        // 可以继续添加更多匹配题...
    }

    private static void initOrderingData() {
        // Agent Technology Evolution排序题
        OrderingData techEvolution = new OrderingData(
                "order_tech_evolution",
                "Agent History",
                "Question Expert",
                "Agent Technology Evolution",
                Arrays.asList(
                        "Rule-based Expert Systems (1970s)",
                        "Symbolic AI Agents (1980s)",
                        "Machine Learning Agents (1990s)",
                        "Multi-Agent Systems (2000s)",
                        "Deep Learning Agents (2010s)",
                        "Large Language Model Agents (2020s)"
                )
        );
        questionDataMap.put(techEvolution.getId(), techEvolution);

        // 可以继续添加更多排序题...
    }

    private static void initTrueFalseData() {
        // Agent Learning Principle判断题
        TrueFalseData learningPrinciple = new TrueFalseData(
                "tf_learning_principle",
                "Agent Principles",
                "Question Expert",
                "All intelligent agents must have the capability to learn from experience.",
                false,
                "While learning is an important capability for many advanced agents, some intelligent agents operate using fixed rule sets or expert systems without learning capabilities. These can still be considered intelligent agents if they can perceive their environment and take actions to achieve goals."
        );
        questionDataMap.put(learningPrinciple.getId(), learningPrinciple);

        // 可以继续添加更多判断题...
    }

    // 根据ID获取题目数据
    public static QuestionData getQuestionById(String id) {
        return questionDataMap.get(id);
    }

    // 按类型获取所有题目ID列表
    public static List<String> getQuestionIdsByType(String type) {
        List<String> ids = new ArrayList<>();
        for (QuestionData data : questionDataMap.values()) {
            if (data.getQuestionType().equals(type)) {
                ids.add(data.getId());
            }
        }
        return ids;
    }

    // 按分类获取所有题目ID列表
    public static List<String> getQuestionIdsByCategory(String category) {
        List<String> ids = new ArrayList<>();
        for (QuestionData data : questionDataMap.values()) {
            if (data.getCategory().equals(category)) {
                ids.add(data.getId());
            }
        }
        return ids;
    }
}