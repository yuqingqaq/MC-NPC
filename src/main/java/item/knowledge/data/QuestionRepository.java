package item.knowledge.data;

import java.util.*;

public class QuestionRepository {
    private static final Map<String, QuestionData> questionDataMap = new HashMap<>();

    static {
        // 初始化智能体定义相关的多选题
        initAgentDefinitionQuestions();

        // 初始化大型语言模型相关的匹配题
        initLLMMatchingQuestions();

        // 初始化智能体工具相关的判断题
        initToolsTrueFalseQuestions();

        // 初始化智能体工作流相关的排序题
        initWorkflowOrderingQuestions();
    }

    private static void initAgentDefinitionQuestions() {
        // 智能体定义题目1
        MultipleChoiceData agentDefinition1 = new MultipleChoiceData(
                "mc_agent_definition_1",
                "智能体基础",
                "Question Expert",
                "智能体定义",
                "什么是智能体(Agent)?",
                Arrays.asList(
                        "仅处理静态文本的系统",
                        "能推理、规划并使用工具与环境交互的系统",
                        "仅回答问题的聊天机器人",
                        "提供信息但无法执行任务的百科全书"
                ),
                1,  // 第二个选项是正确答案
                "mc_agent_definition_2" // 下一题的ID
        );
        questionDataMap.put(agentDefinition1.getId(), agentDefinition1);

        // 智能体定义题目2
        MultipleChoiceData agentDefinition2 = new MultipleChoiceData(
                "mc_agent_definition_2",
                "智能体组成",
                "Question Expert",
                "智能体结构",
                "智能体的两个核心部分是什么?",
                Arrays.asList(
                        "输入设备和输出设备",
                        "大脑(AI模型)和身体(能力与工具)",
                        "数据库和用户界面",
                        "硬件和软件"
                ),
                1,  // 第二个选项是正确答案
                "mc_agent_definition_3" // 下一题的ID
        );
        questionDataMap.put(agentDefinition2.getId(), agentDefinition2);

        // 智能体定义题目3
        MultipleChoiceData agentDefinition3 = new MultipleChoiceData(
                "mc_agent_definition_3",
                "智能体特性",
                "Question Expert",
                "智能体能力",
                "以下哪个是智能体的关键特性?",
                Arrays.asList(
                        "只能处理文本信息",
                        "必须具有实体形态",
                        "自主性和与环境交互的能力",
                        "只能执行预设的脚本"
                ),
                2,  // 第三个选项是正确答案
                "mc_agent_definition_4" // 下一题的ID
        );
        questionDataMap.put(agentDefinition3.getId(), agentDefinition3);

        // 智能体定义题目4
        MultipleChoiceData agentDefinition4 = new MultipleChoiceData(
                "mc_agent_definition_4",
                "智能体应用",
                "Question Expert",
                "智能体实例",
                "以下哪个是AI智能体的实际例子?",
                Arrays.asList(
                        "常见问题解答页面",
                        "Siri或Alexa等虚拟助手",
                        "简单计算器",
                        "预设行为的游戏角色"
                ),
                1,  // 第二个选项是正确答案
                "" // 没有下一题
        );
        questionDataMap.put(agentDefinition4.getId(), agentDefinition4);
    }

    private static void initLLMMatchingQuestions() {
        // LLM概念匹配题1
        Map<Integer, Integer> llmMatches1 = new HashMap<>();
        llmMatches1.put(0, 0); // 大语言模型 -> 理解并生成人类语言的AI
        llmMatches1.put(1, 1); // 标记(Token) -> LLM处理的基本单位
        llmMatches1.put(2, 2); // 自回归性 -> 一次生成的输出成为下次输入

        MatchingData llmConcepts1 = new MatchingData(
                "match_llm_concepts_1",
                "语言模型基础",
                "Question Expert",
                "LLM基本概念",
                Arrays.asList(
                        "大型语言模型(LLM)",
                        "标记(Token)",
                        "自回归性"
                ),
                Arrays.asList(
                        "理解并生成人类语言的深度学习模型",
                        "LLM处理信息的基本单位，类似于词语",
                        "一次通过的输出成为下一次的输入"
                ),
                llmMatches1,
                "match_llm_concepts_2" // 下一题的ID
        );
        questionDataMap.put(llmConcepts1.getId(), llmConcepts1);

        // LLM消息类型匹配题
        Map<Integer, Integer> llmMatches2 = new HashMap<>();
        llmMatches2.put(0, 0); // 系统消息 -> 定义模型行为的指令
        llmMatches2.put(1, 1); // 用户消息 -> 人类输入的内容
        llmMatches2.put(2, 2); // 助手消息 -> 模型生成的回复

        MatchingData llmConcepts2 = new MatchingData(
                "match_llm_concepts_2",
                "LLM消息类型",
                "Question Expert",
                "LLM对话结构",
                Arrays.asList(
                        "系统消息",
                        "用户消息",
                        "助手消息"
                ),
                Arrays.asList(
                        "定义模型应如何表现的持久性指令",
                        "人类输入的查询或指令",
                        "模型生成的回复内容"
                ),
                llmMatches2,
                "match_llm_concepts_3" // 下一题的ID
        );
        questionDataMap.put(llmConcepts2.getId(), llmConcepts2);

        // 特殊标记匹配题
        Map<Integer, Integer> tokenMatches = new HashMap<>();
        tokenMatches.put(0, 0); // 特殊标记 -> 标记序列边界
        tokenMatches.put(1, 1); // EOS标记 -> 表示序列结束
        tokenMatches.put(2, 2); // 聊天模板 -> 保存对话历史

        MatchingData specialTokens = new MatchingData(
                "match_llm_concepts_3",
                "LLM特殊组件",
                "Question Expert",
                "LLM特殊元素",
                Arrays.asList(
                        "特殊标记(Special Tokens)",
                        "序列结束标记(EOS Token)",
                        "聊天模板(Chat Template)"
                ),
                Arrays.asList(
                        "用于标记消息边界和角色的特殊符号",
                        "指示模型生成过程结束的标记",
                        "保存对话历史并维持上下文的结构"
                ),
                tokenMatches,
                "" // 没有下一题
        );
        questionDataMap.put(specialTokens.getId(), specialTokens);
    }

    private static void initToolsTrueFalseQuestions() {
        // 工具定义判断题1
        TrueFalseData toolsConcept1 = new TrueFalseData(
                "tf_tools_concept_1",
                "智能体工具",
                "Question Expert",
                "工具可以让LLM直接访问外部数据，无需中间处理。",
                false,
                "这是错误的。LLM本身只能生成文本，它通过生成工具调用的文本，让智能体解析并执行工具调用，再将结果返回给LLM。LLM并不能直接访问外部数据，而是通过智能体作为中介。",
                "tf_tools_concept_2" // 下一题的ID
        );
        questionDataMap.put(toolsConcept1.getId(), toolsConcept1);

        // 工具设计判断题2
        TrueFalseData toolsConcept2 = new TrueFalseData(
                "tf_tools_concept_2",
                "智能体工具",
                "Question Expert",
                "优秀的工具设计应该是结构化的，包含明确的功能描述和输入格式。",
                true,
                "这是正确的。优秀的工具设计应该采用结构化表达方式，清晰描述工具功能和预期的输入格式，这样LLM才能正确理解如何调用工具。虽然没有强制的格式要求，但精确、连贯的描述对于工具的有效使用至关重要。",
                "tf_tools_concept_3" // 下一题的ID
        );
        questionDataMap.put(toolsConcept2.getId(), toolsConcept2);

        // 工具作用判断题3
        TrueFalseData toolsConcept3 = new TrueFalseData(
                "tf_tools_concept_3",
                "智能体工具",
                "Question Expert",
                "工具的主要作用是替代LLM，而不是增强LLM的能力。",
                false,
                "这是错误的。工具的主要作用是增强和补充LLM的能力，而不是替代它。例如，计算器工具补充LLM在数学计算方面的弱点，搜索工具提供最新信息以突破LLM训练数据的时间限制。工具和LLM相互配合，发挥各自优势。",
                "" // 没有下一题
        );
        questionDataMap.put(toolsConcept3.getId(), toolsConcept3);
    }

    private static void initWorkflowOrderingQuestions() {
        // 工作流程排序题
        OrderingData workflowSteps = new OrderingData(
                "order_workflow_steps",
                "智能体工作流",
                "Question Expert",
                "智能体思考-行动-观察循环的正确顺序",
                Arrays.asList(
                        "接收用户指令",
                        "思考分析任务",
                        "规划执行步骤",
                        "调用相关工具",
                        "观察行动结果",
                        "根据反馈调整",
                        "生成最终回应"
                ),
                "" // 没有下一题
        );
        questionDataMap.put(workflowSteps.getId(), workflowSteps);
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