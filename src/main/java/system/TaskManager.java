package system;

import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;

import java.util.List;

import controller.GameController;

public class TaskManager {
    private static TaskManager instance;
    private List<AdaptiveTaskModel> currentTasks;
    private AdaptiveTaskModel currentTaskInOverview;

    private TaskManager() {
        loadTasks();
    }

    // 单例模式（获取 TaskManager 实例）
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    // 加载任务（从 GameController 获取任务列表）
    private void loadTasks() {
        currentTasks = GameController.getInstance().getAdaptiveTasks();
    }

    // 设置任务列表
    public void setTasks(List<AdaptiveTaskModel> tasks) {
        this.currentTasks = tasks;
    }

    // 获取任务列表
    public List<AdaptiveTaskModel> getTasks() {
        return currentTasks;
    }

    // 获取当前任务（用于任务概览）
    public AdaptiveTaskModel getCurrentTask() {
        return currentTaskInOverview;
    }

    // 设置当前任务
    public void setCurrentTask(AdaptiveTaskModel task) {
        this.currentTaskInOverview = task;
    }

    // 开始一个子任务
    public void startSubTask(AdaptiveSubTaskModel subTask) {
        if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.NOT_STARTED) {
            subTask.setStatus(AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS);

            // 启动时间管理器来跟踪任务时间
            TimeManager.getInstance().startTrackingTime(subTask);

            System.out.println("Sub-task started: " + subTask.getTitle());
        }

        // 更新父任务的状态
        AdaptiveTaskModel parentTask = getParentTask(subTask);
        if (parentTask != null) {
            parentTask.setStatus(AdaptiveTaskModel.TaskStatus.IN_PROGRESS);
        }
    }

    // 完成子任务
    public void completeSubTask(AdaptiveSubTaskModel subTask) {
        subTask.setStatus(AdaptiveSubTaskModel.TaskStatus.COMPLETED);

        // 停止时间追踪
        TimeManager.getInstance().stopTrackingTime(subTask);

        System.out.println("Sub-task completed: " + subTask.getTitle());

        // 更新父任务的状态
        AdaptiveTaskModel parentTask = getParentTask(subTask);
        if (parentTask != null) {
            // 检查所有子任务是否完成
            boolean allSubTasksCompleted = parentTask.getSubTasks().stream()
                    .allMatch(s -> s.getStatus() == AdaptiveSubTaskModel.TaskStatus.COMPLETED);

            parentTask.setStatus(allSubTasksCompleted ? AdaptiveTaskModel.TaskStatus.COMPLETED :
                    AdaptiveTaskModel.TaskStatus.IN_PROGRESS);
        }
    }

    // 获取父任务（用于更新父任务状态）
    private AdaptiveTaskModel getParentTask(AdaptiveSubTaskModel subTask) {
        for (AdaptiveTaskModel task : currentTasks) {
            if (task.getSubTasks().contains(subTask)) {
                return task;
            }
        }
        return null;
    }
}