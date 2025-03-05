package system;

import model.AcademicTaskModel;
import java.util.List;

import controller.GameController;

public class TaskManager {
    private static TaskManager instance;
    private List<AcademicTaskModel> tasks;
    private AcademicTaskModel currentTask;

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
        tasks = GameController.getInstance().getAcademicTasks();
    }

    public void setTasks(List<AcademicTaskModel> tasks) {
        this.tasks = tasks;
    }

    public List<AcademicTaskModel> getTasks() {
        return tasks;
    }

    public AcademicTaskModel getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(AcademicTaskModel task) {
        this.currentTask = task;
    }
} 