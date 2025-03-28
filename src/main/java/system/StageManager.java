package system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageManager {
    private static StageManager instance;

    // 存储所有阶段信息
    private final List<StageInfo> stages;

    // 按ID快速查找阶段
    private final Map<String, StageInfo> stageMap;

    // 当前激活的阶段
    private StageInfo currentStage;

    // 已访问的阶段
    private final Map<String, Boolean> visitedStages;

    // 已完成的阶段
    private final Map<String, Boolean> completedStages;

    private StageManager() {
        this.stages = new ArrayList<>();
        this.stageMap = new HashMap<>();
        this.visitedStages = new HashMap<>();
        this.completedStages = new HashMap<>();
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    // 设置阶段数据（由GameController调用）
    public void setStages(List<StageInfo> stageInfos) {
        this.stages.clear();
        this.stageMap.clear();

        // 按顺序排序
        stageInfos.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));

        // 存储并索引
        this.stages.addAll(stageInfos);
        for (StageInfo stage : stageInfos) {
            this.stageMap.put(stage.getId(), stage);
        }

        // 设置初始阶段
        if (!stages.isEmpty()) {
            this.currentStage = stages.get(0);
        }

        System.out.println("StageManager initialized with " + stages.size() + " stages");
    }

    public StageInfo getStageById(String id) {
        return stageMap.get(id);
    }

    public StageInfo getCurrentStage() {
        return currentStage;
    }

    public void advanceToNextStage() {
        int currentIndex = stages.indexOf(currentStage);
        if (currentIndex < stages.size() - 1) {
            // 先标记当前阶段为完成
            markStageCompleted(currentStage.getId());
            // 然后前进到下一阶段
            currentStage = stages.get(currentIndex + 1);
            System.out.println("Advanced to stage: " + currentStage.getId());
        }
    }

    public StageInfo getStageByNpcLocation(String location) {
        for (StageInfo stage : stages) {
            if (location != null && location.equalsIgnoreCase(stage.getNpcLocation())) {
                return stage;
            }
        }
        return null;
    }

    public void markStageVisited(String stageId) {
        visitedStages.put(stageId, true);
        System.out.println("Marked stage as visited: " + stageId);
    }

    public boolean isStageVisited(String stageId) {
        return Boolean.TRUE.equals(visitedStages.get(stageId));
    }

    public void markStageCompleted(String stageId) {
        completedStages.put(stageId, true);
        System.out.println("Marked stage as completed: " + stageId);
    }

    public boolean isStageCompleted(String stageId) {
        return Boolean.TRUE.equals(completedStages.get(stageId));
    }

    // 获取所有阶段列表
    public List<StageInfo> getAllStages() {
        return new ArrayList<>(stages);
    }

    // 获取下一个阶段
    public StageInfo getNextStage() {
        int currentIndex = stages.indexOf(currentStage);
        if (currentIndex < stages.size() - 1) {
            return stages.get(currentIndex + 1);
        }
        return null;
    }

    // 获取上一个阶段
    public StageInfo getPreviousStage() {
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
        StageInfo stage = stageMap.get(stageId);
        if (stage != null) {
            this.currentStage = stage;
            System.out.println("Manually set current stage to: " + stageId);
        }
    }

    // 重置所有进度 (用于测试)
    public void resetAllProgress() {
        visitedStages.clear();
        completedStages.clear();
        if (!stages.isEmpty()) {
            currentStage = stages.get(0);
        }
        System.out.println("All stage progress has been reset");
    }

    public static class StageInfo {
        private String id;
        private String title;
        private String content;
        private String outcome;
        private String npcLocation;
        private String coinLocation;
        private int order;

        // 无参构造函数
        public StageInfo() {
        }
        // getters and setters

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

        public String getNpcLocation() {
            return npcLocation;
        }

        public String getCoinLocation() {
            return coinLocation;
        }

        public int getOrder() {
            return order;
        }
    }
}