package controller;

import block.poster.AcademicPosterBlockEntity;

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
    private final PosterData agentBasicData;
    private final PosterData agentEvolutionData;
    private final PosterData agentLearningData;
    private final PosterData agentPrinciplesData;

    private PosterManager() {
        // 初始化各种海报
        agentBasicData = initAgentBasicPoster();
        agentEvolutionData = initAgentEvolutionPoster();
        agentLearningData = initAgentLearningPoster();
        agentPrinciplesData = initAgentPrinciplesPoster();

        // 输出调试信息
        LOGGER.info("已初始化所有学术海报");
        logPosterInfo("基础概念", agentBasicData);
        logPosterInfo("技术演化", agentEvolutionData);
        logPosterInfo("学习方法", agentLearningData);
        logPosterInfo("设计原则", agentPrinciplesData);
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

    // 初始化智能体基础概念海报
    private PosterData initAgentBasicPoster() {
        List<String> content = new ArrayList<>();
        content.add("智能体(Agent)是能够感知环境并采取行动以实现目标的实体。");
        content.add("智能体包含感知系统、决策系统和执行系统三个核心部分。");
        content.add("关键特性：自主性、反应性、前瞻性和社交能力。");
        content.add("智能体是人工智能研究的基础单元，是实现智能行为的实体。");
        content.add("简单智能体直接根据当前感知做出反应，而复杂智能体可维护内部状态。");

        return new PosterData(
                "智能体基础概念",
                content,
                "npcopenai:textures/poster/agent_basic.png",
                "人工智能专家",
                "agent_concept",
                "mc_agent_concept"
        );
    }

    // 初始化智能体发展演化海报
    private PosterData initAgentEvolutionPoster() {
        List<String> content = new ArrayList<>();
        content.add("智能体技术演化历程：");
        content.add("1970年代：基于规则的专家系统出现，使用IF-THEN规则处理特定领域问题");
        content.add("1980年代：符号AI智能体发展，利用符号表示和逻辑推理解决问题");
        content.add("1990年代：机器学习智能体兴起，能从数据中学习模式");
        content.add("2000年代：多智能体系统发展，多个智能体协作解决复杂问题");
        content.add("2010年代：深度学习智能体崛起，处理大量非结构化数据");
        content.add("2020年代：大型语言模型智能体出现，具备更强的理解和生成能力");

        return new PosterData(
                "智能体技术演化",
                content,
                "npcopenai:textures/poster/agent_evolution.png",
                "AI历史学家",
                "agent_timeline",
                "order_tech_evolution"
        );
    }

    // 初始化智能体学习方法海报
    private PosterData initAgentLearningPoster() {
        List<String> content = new ArrayList<>();
        content.add("智能体学习方法：");
        content.add("监督学习：从带标签的示例中学习，如图像分类和文本分类");
        content.add("无监督学习：在没有标签的情况下发现数据中的模式和结构");
        content.add("强化学习：通过尝试和错误，最大化累积奖励");
        content.add("迁移学习：将一个任务中学到的知识应用到另一个任务");
        content.add("这些学习方式使智能体能够适应环境变化并提高解决问题的能力。");

        return new PosterData(
                "智能体学习方法",
                content,
                "npcopenai:textures/poster/agent_learning.png",
                "机器学习专家",
                "agent_relationship",
                "match_learning_methods"
        );
    }

    // 初始化智能体原则海报
    private PosterData initAgentPrinciplesPoster() {
        List<String> content = new ArrayList<>();
        content.add("智能体设计与应用原则：");
        content.add("1. 不是所有智能体都必须具备学习能力 -- 基于固定规则的智能体也可高效工作");
        content.add("2. 智能体自主性应根据应用场景调整，完全自主可能并非总是最佳选择");
        content.add("3. 智能体设计应平衡反应性（快速响应）和深思熟虑（规划能力）");
        content.add("4. 智能体应优先考虑安全性和可控性，特别是在关键应用中");
        content.add("5. 智能体系统应具有可解释性，使人类能理解其决策过程");

        return new PosterData(
                "智能体设计原则",
                content,
                "npcopenai:textures/poster/agent_principles.png",
                "AI伦理专家",
                "agent_principle",
                "tf_learning_principle"
        );
    }

    // 获取智能体基础概念海报数据
    public PosterData getAgentBasicPosterData() {
        return agentBasicData;
    }

    // 获取智能体发展演化海报数据
    public PosterData getAgentEvolutionPosterData() {
        return agentEvolutionData;
    }

    // 获取智能体学习方法海报数据
    public PosterData getAgentLearningPosterData() {
        return agentLearningData;
    }

    // 获取智能体原则海报数据
    public PosterData getAgentPrinciplesPosterData() {
        return agentPrinciplesData;
    }

    // 将基础概念海报数据应用到方块实体
    public void applyAgentBasicPosterToEntity(AcademicPosterBlockEntity entity) {
        applyPosterDataToEntity(agentBasicData, entity, "基础概念");
    }

    // 将发展演化海报数据应用到方块实体
    public void applyAgentEvolutionPosterToEntity(AcademicPosterBlockEntity entity) {
        applyPosterDataToEntity(agentEvolutionData, entity, "技术演化");
    }

    // 将学习方法海报数据应用到方块实体
    public void applyAgentLearningPosterToEntity(AcademicPosterBlockEntity entity) {
        applyPosterDataToEntity(agentLearningData, entity, "学习方法");
    }

    // 将设计原则海报数据应用到方块实体
    public void applyAgentPrinciplesPosterToEntity(AcademicPosterBlockEntity entity) {
        applyPosterDataToEntity(agentPrinciplesData, entity, "设计原则");
    }

    // 辅助方法：应用海报数据到方块实体
    private void applyPosterDataToEntity(PosterData data, AcademicPosterBlockEntity entity, String posterType) {
        if (entity != null) {
            entity.setPosterData(
                    data.getTitle(),
                    data.getContent(),
                    data.getImagePath(),
                    data.getExpertType(),
                    data.getConceptKey(),
                    data.getQuestionId()
            );
            // 添加调试输出
            LOGGER.info("已将" + posterType + "海报 '" + data.getTitle() +
                    "' 应用到位于 " + entity.getBlockPos() + " 的方块实体");
        } else {
            LOGGER.severe("应用" + posterType + "海报数据失败: 方块实体为null");
        }
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