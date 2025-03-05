package model;

import interfaces.OnTaskCompleteListener;

import java.util.List;
import java.util.Map;

public class AcademicTaskModel {
    private String taskId; // 任务 ID
    private String title; // 任务标题
    private String category; // 类别归属
    private String description; // 任务描述
    private String target; // 目标
    private List<String> resources; // 所需资源
    private String location; // 推荐地点
    private String estimatedTime; // 预计时间
    private List<String> strategies; // 策略列表
    private Map<String, String> summary; // 任务总结，键值对格式
    private boolean completed; // 是否完成
    private OnTaskCompleteListener onCompleteListener; // 任务完成监听器
    private TaskStatus status; // 任务状态
    private String notes; // 笔记字段
    private List<SubTaskModel> subTasks; // 子任务列表
    private long startTime; // 任务开始时间
    private long elapsedTime; // 已用时间

    public enum TaskStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    // 无参构造函数（Jackson 需要这个）
    public AcademicTaskModel() {}

    // 全参构造函数
    public AcademicTaskModel(String taskId, String title, String category, String description, String target,
                             List<String> resources, String location, String estimatedTime, List<String> strategies,
                             Map<String, String> summary, boolean completed) {
        this.taskId = taskId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.target = target;
        this.resources = resources;
        this.location = location;
        this.estimatedTime = estimatedTime;
        this.strategies = strategies;
        this.summary = summary;
        this.completed = completed;
        this.status = completed ? TaskStatus.COMPLETED : TaskStatus.NOT_STARTED;
        this.elapsedTime = 0;
    }

    // Getters 和 Setters
    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public List<String> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<String> strategies) {
        this.strategies = strategies;
    }

    public Map<String, String> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, String> summary) {
        this.summary = summary;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.status = completed ? TaskStatus.COMPLETED : this.status;
        // 如果任务完成，且监听器不为空，触发完成逻辑
        if (completed && this.onCompleteListener != null) {
            this.onCompleteListener.onTaskComplete(this.taskId, "Academic task completed!");
        }
    }

    public OnTaskCompleteListener getOnCompleteListener() {
        return onCompleteListener;
    }

    public void setOnCompleteListener(OnTaskCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SubTaskModel> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTaskModel> subTasks) {
        this.subTasks = subTasks;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void addElapsedTime(long time) {
        this.elapsedTime += time;
    }

    // toString 方法（调试时使用）
    @Override
    public String toString() {
        return "AcademicTaskModel{" +
                "taskId='" + taskId + '\'' +
                ", description='" + description + '\'' +
                ", target='" + target + '\'' +
                ", resources=" + resources +
                ", location='" + location + '\'' +
                ", estimatedTime='" + estimatedTime + '\'' +
                ", strategies=" + strategies +
                ", summary=" + summary +
                ", completed=" + completed +
                ", status=" + status +
                '}';
    }
}