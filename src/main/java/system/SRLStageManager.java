package system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SRLStageManager {
    private static SRLStageManager instance;

    // 存储所有阶段信息
    private final List<SRLStageInfo> stages;

    // 按ID快速查找阶段
    private final Map<String, SRLStageInfo> stageMap;

    // 当前激活的阶段
    private SRLStageInfo currentStage;

    // 已访问的阶段
    private final Map<String, Boolean> visitedStages;

    // 已完成的阶段
    private final Map<String, Boolean> completedStages;

    private SRLStageManager() {
        this.stages = new ArrayList<>();
        this.stageMap = new HashMap<>();
        this.visitedStages = new HashMap<>();
        this.completedStages = new HashMap<>();
    }

    public static SRLStageManager getInstance() {
        if (instance == null) {
            instance = new SRLStageManager();
        }
        return instance;
    }

    // 设置阶段数据（由GameController调用）
    public void setStages(List<SRLStageInfo> stageInfos) {
        this.stages.clear();
        this.stageMap.clear();

        // 按顺序排序
        stageInfos.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));

        // 存储并索引
        this.stages.addAll(stageInfos);
        for (SRLStageInfo stage : stageInfos) {
            this.stageMap.put(stage.getId(), stage);
        }

        // 设置初始阶段
        if (!stages.isEmpty()) {
            this.currentStage = stages.get(0);
        }

        System.out.println("SRLStageManager initialized with " + stages.size() + " stages");
    }

    public SRLStageInfo getStageById(String id) {
        return stageMap.get(id);
    }

    public SRLStageInfo getCurrentStage() {
        return currentStage;
    }

    public void advanceToNextStage() {
        int currentIndex = stages.indexOf(currentStage);
        if (currentIndex < stages.size() - 1) {
            // 先标记当前阶段为完成
            markStageCompleted(currentStage.getId());
            // 然后前进到下一阶段
            currentStage = stages.get(currentIndex + 1);
            System.out.println("Advanced to SRL stage: " + currentStage.getId());
        }
    }

    public void markStageVisited(String stageId) {
        visitedStages.put(stageId, true);
        System.out.println("Marked SRL stage as visited: " + stageId);
    }

    public boolean isStageVisited(String stageId) {
        return Boolean.TRUE.equals(visitedStages.get(stageId));
    }

    public void markStageCompleted(String stageId) {
        completedStages.put(stageId, true);
        System.out.println("Marked SRL stage as completed: " + stageId);
    }

    public boolean isStageCompleted(String stageId) {
        return Boolean.TRUE.equals(completedStages.get(stageId));
    }

    // 获取所有阶段列表
    public List<SRLStageInfo> getAllStages() {
        return new ArrayList<>(stages);
    }

    // 获取下一个阶段
    public SRLStageInfo getNextStage() {
        int currentIndex = stages.indexOf(currentStage);
        if (currentIndex < stages.size() - 1) {
            return stages.get(currentIndex + 1);
        }
        return null;
    }

    // 获取上一个阶段
    public SRLStageInfo getPreviousStage() {
        int currentIndex = stages.indexOf(currentStage);
        if (currentIndex > 0) {
            return stages.get(currentIndex - 1);
        }
        return null;
    }

    // 获取已完成的阶段数量
    public int getCompletedStagesCount() {
        return completedStages.size();
    }

    // 获取总阶段数量
    public int getTotalStagesCount() {
        return stages.size();
    }

    // 手动设置当前阶段 (用于测试或调试)
    public void setCurrentStage(String stageId) {
        SRLStageInfo stage = stageMap.get(stageId);
        if (stage != null) {
            this.currentStage = stage;
            System.out.println("Manually set current SRL stage to: " + stageId);
        }
    }

    // 重置所有进度 (用于测试)
    public void resetAllProgress() {
        visitedStages.clear();
        completedStages.clear();
        if (!stages.isEmpty()) {
            currentStage = stages.get(0);
        }
        System.out.println("All SRL stage progress has been reset");
    }

    public static class SRLStageInfo {
        private String id;
        private String title;
        private String content;
        private String outcome;
        private String relatedTaskId;
        private int order;

        // 无参构造函数
        public SRLStageInfo() {
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getOutcome() {
            return outcome;
        }

        public String getRelatedTaskId() {
            return relatedTaskId;
        }

        public int getOrder() {
            return order;
        }

        // 设置器方法
        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setOutcome(String outcome) {
            this.outcome = outcome;
        }

        public void setRelatedTaskId(String relatedTaskId) {
            this.relatedTaskId = relatedTaskId;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}