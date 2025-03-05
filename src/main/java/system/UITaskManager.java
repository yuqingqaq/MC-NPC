package system;

import model.AcademicTaskModel;

public class UITaskManager {
    private static UITaskManager instance;
    private AcademicTaskModel currentTaskInOverview;

    private UITaskManager() {}

    public static UITaskManager getInstance() {
        if (instance == null) {
            instance = new UITaskManager();
        }
        return instance;
    }

    public AcademicTaskModel getCurrentTaskInOverview() {
        return currentTaskInOverview;
    }

    public void setCurrentTaskInOverview(AcademicTaskModel task) {
        this.currentTaskInOverview = task;
    }
} 