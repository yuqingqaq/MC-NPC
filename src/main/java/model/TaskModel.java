package model;

import interfaces.OnTaskCompleteListener;
import java.util.List;

public class TaskModel {
    private String taskId; // 修改为 String 类型
    private String description;
    private String completionCriteria;
    private boolean completed;
    private String objectName;
    private OnTaskCompleteListener onCompleteListener;
    private List<String> reward; // 修改为 List<String> 类型
    private String plotSignificance;

    public TaskModel(String taskId, String description, String completionCriteria, String objectName, List<String> reward, String plotSignificance, boolean completed) {
        this.taskId = taskId;
        this.description = description;
        this.completionCriteria = completionCriteria;
        this.completed = completed;
        this.objectName = objectName;
        this.reward = reward;
        this.plotSignificance = plotSignificance;
        this.onCompleteListener = null;
    }

    public void setOnCompleteListener(OnTaskCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && this.onCompleteListener != null) {
            for (String rewardItem : this.reward) {
                this.onCompleteListener.onTaskComplete(this.taskId, rewardItem);
            }
        }
    }

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public String getCompletionCriteria() {
        return completionCriteria;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getObjectName() {
        return objectName;
    }

    public List<String> getReward() {
        return reward;
    }

    public String getPlotSignificance() {
        return plotSignificance;
    }

    public OnTaskCompleteListener getOnCompleteListener() {
        return onCompleteListener;
    }
}