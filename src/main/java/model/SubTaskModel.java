package model;

public class SubTaskModel {
    private String subTaskId;
    private String title;
    private String estimatedTime;
    private boolean completed;
    private TaskStatus status;

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
    }

    // Getters 和 Setters
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
        this.status = status;
    }
} 