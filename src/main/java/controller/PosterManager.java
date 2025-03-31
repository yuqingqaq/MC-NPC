package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 管理游戏中的学术海报
 */
public class PosterManager {
    private static PosterManager instance;
    private static final Logger LOGGER = Logger.getLogger("PosterManager");

    // 存储各种海报数据
    private final PosterData agentDefinitionData;
    private final PosterData llmsData;
    private final PosterData toolsData;
    private final PosterData workflowData;

    private PosterManager() {
        // 初始化各种海报
        agentDefinitionData = initAgentDefinitionPoster();
        llmsData = initLLMsPoster();
        toolsData = initToolsPoster();
        workflowData = initWorkflowPoster();

        // 输出调试信息
        LOGGER.info("已初始化所有学术海报");
        logPosterInfo("智能体定义", agentDefinitionData);
        logPosterInfo("大型语言模型", llmsData);
        logPosterInfo("智能体工具", toolsData);
        logPosterInfo("智能体工作流", workflowData);
    }

    private void logPosterInfo(String type, PosterData data) {
        LOGGER.info("海报类型: " + type +
                ", 标题: " + data.getTitle() +
                ", 内容行数: " + data.getContent().size() +
                ", 图像路径: " + data.getImagePath());
    }

    public static synchronized PosterManager getInstance() {
        if (instance == null) {
            instance = new PosterManager();
        }
        return instance;
    }

    // 初始化智能体定义海报
    private PosterData initAgentDefinitionPoster() {
        List<String> content = new ArrayList<>();
        content.add("智能体(Agent)是能够感知环境并采取行动以实现目标的实体。");
        content.add("智能体包含两个核心部分：大脑(AI模型)负责思考和规划，身体(能力和工具)负责执行动作。");
        content.add("关键特性：自主性、反应性、前瞻性和社交能力。");
        content.add("智能体的核心是与环境交互的能动性，常用于个人助手、客服和游戏NPC等场景。");
        content.add("智能体解决问题的方式是通过理解、推理与环境交互的综合能力。");

        return new PosterData(
                "智能体定义",
                content,
                "npcopenai:textures/poster/definition.png",
                "人工智能专家",
                "agent_definition",
                "mc_agent_definition"
        );
    }

    // 初始化LLMs海报
    private PosterData initLLMsPoster() {
        List<String> content = new ArrayList<>();
        content.add("大型语言模型(LLM)是智能体的核心大脑，擅长理解和生成人类语言。");
        content.add("LLM工作原理：基于前文预测下一个标记(token)，自回归生成文本。");
        content.add("消息类型：系统消息定义行为规则，用户和助手消息构成对话。");
        content.add("特殊标记(Special Tokens)用于界定消息边界和序列结束。");
        content.add("LLM通过保存对话历史维持上下文，实现连贯的多轮交互。");
        content.add("虽然LLM只能生成文本，但通过工具调用可以实现更多功能。");

        return new PosterData(
                "大型语言模型",
                content,
                "npcopenai:textures/poster/llms.png",
                "语言模型专家",
                "llms",
                "match_llm_concepts"
        );
    }

    // 初始化Tools海报
    private PosterData initToolsPoster() {
        List<String> content = new ArrayList<>();
        content.add("工具(Tools)是赋予LLM执行能力的函数，弥补模型的局限性。");
        content.add("工具定义包括：功能描述、预期输入格式和输出结果。");
        content.add("工具调用流程：LLM生成调用文本 → 智能体解析并执行 → 结果返回给LLM。");
        content.add("合格工具应补充LLM能力，如计算器弥补数学弱点，搜索提供最新信息。");
        content.add("工具描述通常采用结构化表达方式，确保模型正确理解和调用。");
        content.add("工具对于突破LLM静态训练的局限至关重要，使其能处理实时任务。");

        return new PosterData(
                "智能体工具",
                content,
                "npcopenai:textures/poster/tools.png",
                "工具开发专家",
                "tools",
                "tf_tools_concept"
        );
    }

    // 初始化Workflow海报
    private PosterData initWorkflowPoster() {
        List<String> content = new ArrayList<>();
        content.add("智能体工作流：思考-行动-观察(Thought-Action-Observation)循环。");
        content.add("思考(Thought)：智能体推理并规划下一步行动，通常采用ReAct方法分解问题。");
        content.add("行动(Action)：调用合适工具或执行操作，有多种类型如JSON、代码或函数调用。");
        content.add("观察(Observation)：获取行动结果，整合反馈并调整策略，为下一轮循环提供信息。");
        content.add("整个循环持续进行，直到智能体实现目标或达到停止条件。");
        content.add("ReAct是指‘推理’与‘行动’结合，引导智能体逐步思考而非直接输出最终方案。");

        return new PosterData(
                "智能体工作流",
                content,
                "npcopenai:textures/poster/workflow.png",
                "智能体工程师",
                "workflow",
                "order_workflow_steps"
        );
    }

    // 获取智能体定义海报数据
    public PosterData getAgentDefinitionPosterData() {
        return agentDefinitionData;
    }

    // 获取LLMs海报数据
    public PosterData getLLMsPosterData() {
        return llmsData;
    }

    // 获取Tools海报数据
    public PosterData getToolsPosterData() {
        return toolsData;
    }

    // 获取Workflow海报数据
    public PosterData getWorkflowPosterData() {
        return workflowData;
    }

    // 海报数据类
    public static class PosterData {
        private final String title;
        private final List<String> content;
        private final String imagePath;
        private final String expertType;
        private final String conceptKey;
        private final String questionId;

        public PosterData(String title, List<String> content,
                          String imagePath, String expertType,
                          String conceptKey, String questionId) {
            this.title = title;
            this.content = new ArrayList<>(content); // 创建内容的副本以避免外部修改
            this.imagePath = imagePath;
            this.expertType = expertType;
            this.conceptKey = conceptKey;
            this.questionId = questionId;
        }

        // Getters
        public String getTitle() { return title; }
        public List<String> getContent() { return new ArrayList<>(content); } // 返回副本以防修改
        public String getImagePath() { return imagePath; }
        public String getExpertType() { return expertType; }
        public String getConceptKey() { return conceptKey; }
        public String getQuestionId() { return questionId; }
    }
}