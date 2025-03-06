package model;

import interfaces.TaskStatusListener;
import utils.TimeUtil;

public class SubTaskModel {
    private String subTaskId;
    private String title;
    private String estimatedTime;
    private boolean completed;
    private TaskStatus status;
    private long remainingTime; // 剩余时间（秒）
    private TaskStatusListener listener;

    public enum TaskStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    // 无参构造函数
    public SubTaskModel() {}

    // 带参构造函数
    public SubTaskModel(String subTaskId, String title, String estimatedTime, boolean completed) {
        this.subTaskId = subTaskId;
        this.title = title;
        this.estimatedTime = estimatedTime;
        this.completed = completed;
        this.status = completed ? TaskStatus.COMPLETED : TaskStatus.NOT_STARTED;
        this.remainingTime = TimeUtil.parseEstimatedTime(estimatedTime); // 初始化剩余时间
    }

    // Getters 和 Setters
    public long getRemainingTime() {
        return remainingTime;
    }

    public void reduceRemainingTime(long seconds) {
        if (status == TaskStatus.IN_PROGRESS) {
            remainingTime = Math.max(0, remainingTime - seconds);
            if (remainingTime == 0) {
                setStatus(TaskStatus.COMPLETED);
            }
        }
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
        this.remainingTime = TimeUtil.parseEstimatedTime(estimatedTime); // 更新剩余时间
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.status = completed ? TaskStatus.COMPLETED : this.status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (this.status != status) { // 状态变化时触发
            this.status = status;
            if (status == TaskStatus.COMPLETED) {
                System.out.println("Task '" + title + "' completed.");
                if (listener != null) {
                    listener.onTaskCompleted(this);
                }
            }
        }
    }

    public void setListener(TaskStatusListener listener) {
        this.listener = listener;
    }
}