package system;

import model.AcademicTaskModel;
import java.util.List;

import controller.GameController;

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
} 