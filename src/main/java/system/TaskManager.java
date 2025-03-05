package system;

import model.AcademicTaskModel;
import model.SubTaskModel;
import java.util.List;

import controller.GameController;
import interfaces.TaskStatusListener;
import system.UITaskManager; // 确保导入 UITaskManager
import utils.TimeUtil; // 确保导入 TimeUtil

public class TaskManager {
    private static TaskManager instance;
    private List<AcademicTaskModel> currentTasks;
    private AcademicTaskModel currentTaskinOverview;

    private TaskManager() {
        loadTasks();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    private void loadTasks() {
        currentTasks = GameController.getInstance().getAcademicTasks();
    }

    public void setTasks(List<AcademicTaskModel> tasks) {
        this.currentTasks = tasks;
    }

    public List<AcademicTaskModel> getTasks() {
        return currentTasks;
    }

    public AcademicTaskModel getCurrentTask() {
        return currentTaskinOverview;
    }

    public void setCurrentTask(AcademicTaskModel task) {
        this.currentTaskinOverview = task;
    }

    public void startSubTask(SubTaskModel subTask) {
        subTask.setStatus(SubTaskModel.TaskStatus.IN_PROGRESS);
        subTask.setStartTime(System.currentTimeMillis()); // 记录开始时间
        if (subTask.getStatus() == SubTaskModel.TaskStatus.NOT_STARTED) {
            System.out.println("Starting subtask: " + subTask.getTitle() + ", remainingTime: " + subTask.getRemainingTime());
            subTask.setStatus(SubTaskModel.TaskStatus.IN_PROGRESS);
        }
        // 获取当前的学术任务
        AcademicTaskModel parentTask = UITaskManager.getInstance().getCurrentTaskInOverview();
        if (parentTask != null) {
            parentTask.setStatus(AcademicTaskModel.TaskStatus.IN_PROGRESS);
        }
    }

    public void updateSubTaskTime(SubTaskModel subTask) {
        System.out.println("Updating subtask time: " + subTask.getTitle());
        if (subTask.getStatus() == SubTaskModel.TaskStatus.IN_PROGRESS) {
            subTask.reduceRemainingTime(1);
        }
    }

    public void completeSubTask(SubTaskModel subTask) {
        subTask.setStatus(SubTaskModel.TaskStatus.COMPLETED);
        long currentTime = System.currentTimeMillis();
        long totalTime = TimeUtil.parseEstimatedTime(subTask.getEstimatedTime()); // 获取预计总时间
        long actualTime = totalTime - subTask.getRemainingTime(); // 实际花费时间 = 总时间 - 剩余时间

        // 获取当前的学术任务
        AcademicTaskModel parentTask = UITaskManager.getInstance().getCurrentTaskInOverview();
        if (parentTask != null) {
            parentTask.addElapsedTime(actualTime); // 更新学术任务的总已用时间
            parentTask.setStatus(parentTask.getSubTasks().stream().allMatch(s -> s.getStatus() == SubTaskModel.TaskStatus.COMPLETED) ? 
                                  AcademicTaskModel.TaskStatus.COMPLETED : 
                                  AcademicTaskModel.TaskStatus.IN_PROGRESS);
        }
    }

} 