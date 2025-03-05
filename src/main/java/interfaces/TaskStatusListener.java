package interfaces;

import model.SubTaskModel;

public interface TaskStatusListener {
    void onTaskStarted(SubTaskModel subTask);
    void onTaskCompleted(SubTaskModel subTask);
} 