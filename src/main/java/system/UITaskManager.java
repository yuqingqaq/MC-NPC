package system;

import model.AdaptiveTaskModel;

public class UITaskManager {
    private static UITaskManager instance;
    private AdaptiveTaskModel currentTaskInOverview;

    private UITaskManager() {}

    public static UITaskManager getInstance() {
        if (instance == null) {
            instance = new UITaskManager();
        }
        return instance;
    }

    public AdaptiveTaskModel getCurrentTaskInOverview() {
        return currentTaskInOverview;
    }

    public void setCurrentTaskInOverview(AdaptiveTaskModel task) {
        this.currentTaskInOverview = task;
    }
} 