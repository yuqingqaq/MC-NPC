package interfaces;

import model.AdaptiveSubTaskModel;

public interface TaskStatusListener {
    void onTaskStarted(AdaptiveSubTaskModel subTask);
    void onTaskCompleted(AdaptiveSubTaskModel subTask);
} 