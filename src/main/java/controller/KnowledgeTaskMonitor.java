package controller;

import model.AdaptiveSubTaskModel;
import system.TaskManager;

/**
 * 监控知识图谱进度并自动更新相关任务状态
 */
public class KnowledgeTaskMonitor {
    private static KnowledgeTaskMonitor instance;
    private boolean isMonitoring = false;

    // 子任务ID常量
    private static final String KNOWLEDGE_GRAPH_SUBTASK_ID = "1"; // 知识图谱子任务的ID
    private static final String AGENT_RESEARCH_TASK_TITLE = "Agent研究"; // 主任务标题

    private KnowledgeTaskMonitor() {
        // 私有构造函数
    }

    public static synchronized KnowledgeTaskMonitor getInstance() {
        if (instance == null) {
            instance = new KnowledgeTaskMonitor();
        }
        return instance;
    }

    /**
     * 开始监控知识图谱进度
     */
    public void startMonitoring() {
        if (isMonitoring) return;

        isMonitoring = true;

        // 创建一个定期检查的线程
        Thread monitorThread = new Thread(() -> {
            while (isMonitoring) {
                checkKnowledgeGraphProgress();

                try {
                    Thread.sleep(5000); // 每5秒检查一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        monitorThread.setDaemon(true); // 设为守护线程，不阻止程序退出
        monitorThread.start();
    }

    /**
     * 停止监控
     */
    public void stopMonitoring() {
        isMonitoring = false;
    }

    /**
     * 检查知识图谱进度并更新任务状态
     */
    public void checkKnowledgeGraphProgress() {
        KnowledgeGraphManager knowledgeManager = KnowledgeGraphManager.getInstance();

        // 检查所有知识区域是否都已完成100%
        int completedAreas = knowledgeManager.getCompletedKnowledgeAreas();
        int totalAreas = knowledgeManager.getTotalKnowledgeAreas();

        if (completedAreas >= totalAreas) {
            // 所有区域都达到100%，查找并完成相应的子任务
            completeKnowledgeGraphSubtask();
        }
    }

    /**
     * 完成知识图谱相关的子任务
     */
    private void completeKnowledgeGraphSubtask() {
        TaskManager taskManager = TaskManager.getInstance();

        // 查找Agent研究任务的知识图谱子任务
        AdaptiveSubTaskModel knowledgeSubtask = taskManager.findSubTaskByTitle("完成知识图谱学习");

        if (knowledgeSubtask != null &&
                knowledgeSubtask.getStatus() != AdaptiveSubTaskModel.TaskStatus.COMPLETED) {

            // 自动完成子任务
            taskManager.completeSubTaskByTitle("完成知识图谱学习", true);

            System.out.println("知识图谱学习任务已自动完成！");
        }
    }

    /**
     * 手动检查一次（可从UI按钮调用）
     */
    public void manualCheck() {
        checkKnowledgeGraphProgress();
    }
}