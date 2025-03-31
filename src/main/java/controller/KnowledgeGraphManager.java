package controller;

import java.util.*;

/**
 * 管理玩家的知识图谱，包括概念、关系和时间线
 */
public class KnowledgeGraphManager {
    // 单例模式
    private static KnowledgeGraphManager instance;

    // 玩家已获取的概念
    private Set<String> acquiredConcepts = new HashSet<>();

    // 玩家已掌握的概念关系类别
    private Set<String> masteredRelationships = new HashSet<>();

    // 玩家已验证的原则
    private Set<String> validatedPrinciples = new HashSet<>();

    // 玩家已掌握的时间线
    private Set<String> masteredTimelines = new HashSet<>();

    // 知识图谱完成度（用于任务进度）
    private Map<String, Integer> completionStatus = new HashMap<>();

    private KnowledgeGraphManager() {
        // 初始化任务相关的知识图谱目标
        completionStatus.put("智能体定义", 0); // 0%完成度
        completionStatus.put("大型语言模型", 0);
        completionStatus.put("智能体工具", 0);
        completionStatus.put("智能体工作流", 0);
    }

    public static synchronized KnowledgeGraphManager getInstance() {
        if (instance == null) {
            instance = new KnowledgeGraphManager();
        }
        return instance;
    }

    /**
     * 添加一个已获取的概念
     */
    public void addConcept(String conceptName) {
        acquiredConcepts.add(conceptName);
        updateCompletionStatus("智能体定义");
    }

    /**
     * 添加一个已掌握的关系类别
     */
    public void addRelationship(String relationshipCategory) {
        masteredRelationships.add(relationshipCategory);
        updateCompletionStatus("大型语言模型");
    }

    /**
     * 添加一个已验证的原则
     */
    public void addPrinciple(String principle) {
        validatedPrinciples.add(principle);
        updateCompletionStatus("智能体工具");
    }

    /**
     * 添加一个已掌握的时间线
     */
    public void addTimeline(String timeline) {
        masteredTimelines.add(timeline);
        updateCompletionStatus("智能体工作流");
    }

    /**
     * 更新特定知识领域的完成度
     */
    private void updateCompletionStatus(String knowledgeArea) {
        int currentTotal = 0;
        int targetTotal = 0;

        switch(knowledgeArea) {
            case "智能体定义":
                currentTotal = acquiredConcepts.size();
                targetTotal = 4; // 目标是掌握4个概念
                break;
            case "大型语言模型":
                currentTotal = masteredRelationships.size();
                targetTotal = 3; // 目标是掌握3个LLM特性
                break;
            case "智能体工具":
                currentTotal = validatedPrinciples.size();
                targetTotal = 2; // 目标是验证2个工具原则
                break;
            case "智能体工作流":
                currentTotal = masteredTimelines.size();
                targetTotal = 1; // 目标是掌握1个工作流程
                break;
        }

        // 计算完成百分比
        int completionPercentage = (targetTotal > 0) ? (currentTotal * 100 / targetTotal) : 0;
        completionPercentage = Math.min(100, completionPercentage); // 确保不超过100%

        completionStatus.put(knowledgeArea, completionPercentage);
        KnowledgeTaskMonitor.getInstance().checkKnowledgeGraphProgress();
    }

    /**
     * 获取已获取的概念列表
     */
    public List<String> getAcquiredConcepts() {
        return new ArrayList<>(acquiredConcepts);
    }

    /**
     * 获取已掌握的关系类别列表
     */
    public List<String> getMasteredRelationships() {
        return new ArrayList<>(masteredRelationships);
    }

    /**
     * 获取已验证的原则列表
     */
    public List<String> getValidatedPrinciples() {
        return new ArrayList<>(validatedPrinciples);
    }

    /**
     * 获取已掌握的时间线列表
     */
    public List<String> getMasteredTimelines() {
        return new ArrayList<>(masteredTimelines);
    }

    /**
     * 获取特定知识领域的完成度
     */
    public int getCompletionPercentage(String knowledgeArea) {
        return completionStatus.getOrDefault(knowledgeArea, 0);
    }

    /**
     * 获取整体知识图谱完成度
     */
    public int getOverallCompletionPercentage() {
        int sum = 0;
        for (int percentage : completionStatus.values()) {
            sum += percentage;
        }
        return sum / completionStatus.size();
    }

    /**
     * 检查知识准备是否足够开始文献任务
     */
    public boolean isReadyForLiteratureTask() {
        return getOverallCompletionPercentage() >= 50; // 至少需要50%的知识准备
    }

    /**
     * 检查是否掌握了特定概念
     */
    public boolean hasConcept(String conceptName) {
        return acquiredConcepts.contains(conceptName);
    }

    /**
     * 检查是否掌握了特定关系类别
     */
    public boolean hasRelationship(String relationshipCategory) {
        return masteredRelationships.contains(relationshipCategory);
    }

    /**
     * 检查是否验证了特定原则
     */
    public boolean hasPrinciple(String principle) {
        return validatedPrinciples.contains(principle);
    }

    /**
     * 检查是否掌握了特定时间线
     */
    public boolean hasTimeline(String timeline) {
        return masteredTimelines.contains(timeline);
    }

    /**
     * 获取知识区域总数
     */
    public int getTotalKnowledgeAreas() {
        return completionStatus.size();
    }

    /**
     * 获取已完成100%的知识区域数量
     */
    public int getCompletedKnowledgeAreas() {
        int count = 0;
        for (int percentage : completionStatus.values()) {
            if (percentage >= 100) {
                count++;
            }
        }
        return count;
    }

    /**
     * 重置所有知识（用于测试或新游戏）
     */
    public void resetAllKnowledge() {
        acquiredConcepts.clear();
        masteredRelationships.clear();
        validatedPrinciples.clear();
        masteredTimelines.clear();

        for (String key : completionStatus.keySet()) {
            completionStatus.put(key, 0);
        }
    }

    /**
     * 添加一个新的知识区域（用于扩展）
     */
    public void addKnowledgeArea(String areaName) {
        if (!completionStatus.containsKey(areaName)) {
            completionStatus.put(areaName, 0);
        }
    }
}