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
        // Intelligent Agent概念题目1
        MultipleChoiceData agentConcept1 = new MultipleChoiceData(
                "mc_agent_concept_1",
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
                1,  // 0-based index, second option is correct
                "mc_agent_concept_2" // 下一题的ID
        );
        questionDataMap.put(agentConcept1.getId(), agentConcept1);

        // Intelligent Agent概念题目2
        MultipleChoiceData agentConcept2 = new MultipleChoiceData(
                "mc_agent_concept_2",
                "Agent Components",
                "Question Expert",
                "Agent Architecture",
                "What are the three core components of an intelligent agent?",
                Arrays.asList(
                        "Learning, Planning, Acting",
                        "Perception, Decision, Action",
                        "Memory, Computation, Output",
                        "Algorithm, Database, Interface"
                ),
                1,  // 第二个选项是正确答案
                "mc_agent_concept_3" // 下一题的ID
        );
        questionDataMap.put(agentConcept2.getId(), agentConcept2);

        // Intelligent Agent概念题目3
        MultipleChoiceData agentConcept3 = new MultipleChoiceData(
                "mc_agent_concept_3",
                "Agent Characteristics",
                "Question Expert",
                "Agent Autonomy",
                "What is autonomy in the context of intelligent agents?",
                Arrays.asList(
                        "The ability to operate without any human intervention",
                        "The ability to make decisions based on internal state rather than just current percepts",
                        "The ability to learn from past experiences",
                        "The ability to adapt to changing environments"
                ),
                1,  // 第二个选项是正确答案
                "mc_agent_concept_4" // 下一题的ID
        );
        questionDataMap.put(agentConcept3.getId(), agentConcept3);

        // Intelligent Agent概念题目4
        MultipleChoiceData agentConcept4 = new MultipleChoiceData(
                "mc_agent_concept_4",
                "Agent Functions",
                "Question Expert",
                "Agent Sensing",
                "What's the primary purpose of the perception system in an intelligent agent?",
                Arrays.asList(
                        "To communicate with other agents",
                        "To execute actions in the environment",
                        "To gather information about the environment",
                        "To process and store memories"
                ),
                2,  // 第三个选项是正确答案
                "" // 没有下一题
        );
        questionDataMap.put(agentConcept4.getId(), agentConcept4);
    }

    private static void initMatchingData() {
        // Agent Learning Methods匹配题1
        Map<Integer, Integer> learningMatches1 = new HashMap<>();
        learningMatches1.put(0, 0); // Supervised -> labeled examples
        learningMatches1.put(1, 1); // Unsupervised -> patterns without labels
        learningMatches1.put(2, 2); // Reinforcement -> trial and error

        MatchingData learningMethods1 = new MatchingData(
                "match_learning_methods_1",
                "Agent Learning",
                "Question Expert",
                "Basic Learning Methods",
                Arrays.asList(
                        "Supervised Learning",
                        "Unsupervised Learning",
                        "Reinforcement Learning"
                ),
                Arrays.asList(
                        "Learning from labeled examples",
                        "Finding patterns without labels",
                        "Learning through trial and error"
                ),
                learningMatches1,
                "match_learning_methods_2" // 下一题的ID
        );
        questionDataMap.put(learningMethods1.getId(), learningMethods1);

        // Agent Learning Methods匹配题2
        Map<Integer, Integer> learningMatches2 = new HashMap<>();
        learningMatches2.put(0, 0); // Transfer -> applying knowledge
        learningMatches2.put(1, 1); // Few-shot -> limited examples
        learningMatches2.put(2, 2); // Self-supervised -> own supervision

        MatchingData learningMethods2 = new MatchingData(
                "match_learning_methods_2",
                "Agent Learning",
                "Question Expert",
                "Advanced Learning Methods",
                Arrays.asList(
                        "Transfer Learning",
                        "Few-shot Learning",
                        "Self-supervised Learning"
                ),
                Arrays.asList(
                        "Applying knowledge from one task to another",
                        "Learning from a very limited number of examples",
                        "Learning by generating own supervision signal"
                ),
                learningMatches2,
                "match_learning_methods_3" // 下一题的ID
        );
        questionDataMap.put(learningMethods2.getId(), learningMethods2);

        // Agent Environment匹配题
        Map<Integer, Integer> environmentMatches = new HashMap<>();
        environmentMatches.put(0, 0); // Observable -> fully visible
        environmentMatches.put(1, 1); // Deterministic -> predictable
        environmentMatches.put(2, 2); // Static -> unchanging

        MatchingData environmentTypes = new MatchingData(
                "match_learning_methods_3",
                "Agent Environments",
                "Question Expert",
                "Environment Properties",
                Arrays.asList(
                        "Fully Observable",
                        "Deterministic",
                        "Static"
                ),
                Arrays.asList(
                        "Agent can perceive complete state of environment",
                        "Next state is completely determined by current state and action",
                        "Environment doesn't change while agent is thinking"
                ),
                environmentMatches,
                "" // 没有下一题
        );
        questionDataMap.put(environmentTypes.getId(), environmentTypes);
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
                ),
                "" // 没有下一题
        );
        questionDataMap.put(techEvolution.getId(), techEvolution);
    }

    private static void initTrueFalseData() {
        // Agent Learning Principle判断题1
        TrueFalseData learningPrinciple1 = new TrueFalseData(
                "tf_learning_principle_1",
                "Agent Principles",
                "Question Expert",
                "All intelligent agents must have the capability to learn from experience.",
                false,
                "While learning is an important capability for many advanced agents, some intelligent agents operate using fixed rule sets or expert systems without learning capabilities. These can still be considered intelligent agents if they can perceive their environment and take actions to achieve goals.",
                "tf_learning_principle_2" // 下一题的ID
        );
        questionDataMap.put(learningPrinciple1.getId(), learningPrinciple1);

        // Agent Safety Principle判断题2
        TrueFalseData safetyPrinciple = new TrueFalseData(
                "tf_learning_principle_2",
                "Agent Principles",
                "Question Expert",
                "The higher the autonomy of an agent, the better it will always perform in any environment.",
                false,
                "Higher autonomy is not always better. The optimal level of autonomy depends on the specific task, environment, and safety requirements. In critical systems or when human values need to be precisely represented, limited autonomy with human oversight may be preferable.",
                "" // 没有下一题
        );
        questionDataMap.put(safetyPrinciple.getId(), safetyPrinciple);
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