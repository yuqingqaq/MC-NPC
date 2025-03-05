package model;

import interfaces.TaskStatusListener;
import utils.TimeUtil;

public class SubTaskModel {
    private String subTaskId;
    private String title;
    private String estimatedTime;
    private boolean completed;
    private TaskStatus status;
    private long startTime; // 任务开始时间
    private long remainingTime; // 剩余时间（秒）
    private TaskStatusListener listener; // 任务状态监听器

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
        System.out.println("SubTaskModel initialized: " + title + ", estimatedTime: " + estimatedTime + ", remainingTime: " + remainingTime);

    }

    // Getters 和 Setters
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void reduceRemainingTime(long seconds) {
        if (status == TaskStatus.IN_PROGRESS) {
            System.out.println("Before reduction: " + remainingTime);
            remainingTime = Math.max(0, remainingTime - seconds);
            System.out.println("After reduction: " + remainingTime);
            if (remainingTime == 0) {
                setStatus(TaskStatus.COMPLETED);
                System.out.println("Task completed: " + title);
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
        if (this.status != status) { // 状态真正发生变化时才设置
            System.out.println("Task '" + title + "' status changed from " + this.status + " to " + status);
            this.status = status;
            if (status == TaskStatus.IN_PROGRESS && listener != null) {
                listener.onTaskStarted(this);
            } else if (status == TaskStatus.COMPLETED && listener != null) {
                listener.onTaskCompleted(this);
            }
        }
    }

    public void setListener(TaskStatusListener listener) {
        this.listener = listener;
    }
} 