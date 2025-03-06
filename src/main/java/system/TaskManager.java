package system;

import model.AcademicTaskModel;
import model.SubTaskModel;

import java.util.List;

import controller.GameController;
import interfaces.TaskStatusListener;

public class TaskManager {
    private static TaskManager instance;
    private List<AcademicTaskModel> currentTasks;
    private AcademicTaskModel currentTaskInOverview;

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
        currentTasks = GameController.getInstance().getAcademicTasks();
    }

    // 设置任务列表
    public void setTasks(List<AcademicTaskModel> tasks) {
        this.currentTasks = tasks;
    }

    // 获取任务列表
    public List<AcademicTaskModel> getTasks() {
        return currentTasks;
    }

    // 获取当前任务（用于任务概览）
    public AcademicTaskModel getCurrentTask() {
        return currentTaskInOverview;
    }

    // 设置当前任务
    public void setCurrentTask(AcademicTaskModel task) {
        this.currentTaskInOverview = task;
    }

    // 开始一个子任务
    public void startSubTask(SubTaskModel subTask) {
        if (subTask.getStatus() == SubTaskModel.TaskStatus.NOT_STARTED) {
            subTask.setStatus(SubTaskModel.TaskStatus.IN_PROGRESS);

            // 启动时间管理器来跟踪任务时间
            TimeManager.getInstance().startTrackingTime(subTask);

            System.out.println("Sub-task started: " + subTask.getTitle());
        }

        // 更新父任务的状态
        AcademicTaskModel parentTask = getParentTask(subTask);
        if (parentTask != null) {
            parentTask.setStatus(AcademicTaskModel.TaskStatus.IN_PROGRESS);
        }
    }

    // 完成子任务
    public void completeSubTask(SubTaskModel subTask) {
        subTask.setStatus(SubTaskModel.TaskStatus.COMPLETED);

        // 停止时间追踪
        TimeManager.getInstance().stopTrackingTime(subTask);

        System.out.println("Sub-task completed: " + subTask.getTitle());

        // 更新父任务的状态
        AcademicTaskModel parentTask = getParentTask(subTask);
        if (parentTask != null) {
            // 检查所有子任务是否完成
            boolean allSubTasksCompleted = parentTask.getSubTasks().stream()
                    .allMatch(s -> s.getStatus() == SubTaskModel.TaskStatus.COMPLETED);

            parentTask.setStatus(allSubTasksCompleted ? AcademicTaskModel.TaskStatus.COMPLETED :
                    AcademicTaskModel.TaskStatus.IN_PROGRESS);
        }
    }

    // 获取父任务（用于更新父任务状态）
    private AcademicTaskModel getParentTask(SubTaskModel subTask) {
        for (AcademicTaskModel task : currentTasks) {
            if (task.getSubTasks().contains(subTask)) {
                return task;
            }
        }
        return null;
    }
}