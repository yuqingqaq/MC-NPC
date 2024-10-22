package model;

import metadata.NPCMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NPCModel {
    private String name;
    private String description;
    private String role;
    private List<String> dialogues;
    private List<TaskModel> tasks;
    private List<NPCMessage> dialogueHistory;
    private List<NPCMessage> chatHistory; // Only dialogues shown in the GUI
    private List<ItemModel> items;

    public NPCModel(String name, String description, String role, List<String> dialogues, List<TaskModel> tasks) {
        this.name = name;
        this.description = description;
        this.role = role;
        this.dialogues = new ArrayList<>(dialogues);
        this.tasks = new ArrayList<>(tasks);
        this.dialogueHistory = new ArrayList<>();
        this.chatHistory = new ArrayList<>();
        this.items = new ArrayList<>();
    }
    public String getNPCName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getRole(){
        return role;
    }
    public List<TaskModel> getTasks(){
        return tasks;
    }

    // 判断此 NPC 的所有任务是否已完成
    public boolean areAllTasksCompleted() {
        return tasks.stream().allMatch(TaskModel::isCompleted);
    }

    public void addTask(TaskModel task) {
        tasks.add(task);
    }

    // 修改对话历史的添加方法，接受 NPCMessage 对象
    public void addDialogueToHistory(NPCMessage dialogue) {
        dialogueHistory.add(dialogue);
    }
    // Other methods remain the same with slight modification for chatHistory
    public void addDialogueTochatHistory(NPCMessage dialogue) {
        dialogueHistory.add(dialogue);
        if (!"system".equals(dialogue.getSender())) {
            chatHistory.add(dialogue);
        }
    }
    // 返回 NPCMessage 列表
    public List<NPCMessage> getDialogueHistory() {
        return dialogueHistory;
    }
    public List<NPCMessage> getChatHistory() { return chatHistory; }

    // New method to check completion conditions based on dialogue
    public boolean checkDialogueCompletionCondition() {
        int dialogueCount = 0;
        boolean hasThankKeywords = false;

        for (NPCMessage message : dialogueHistory) {
            if ("assistant".equals(message.getSender())) {
                if (message.getContent().contains("谢谢") || message.getContent().contains("Thank")) {
                    hasThankKeywords = true;
                }
                dialogueCount++;
            }
        }

        return dialogueCount > 3 || hasThankKeywords;
    }

    // Method to complete all tasks
    public void completeAllTasks() {
        for (TaskModel task : tasks) {
            task.setCompleted(true);
        }
    }
    public String getChatHistoryAsString() {
        StringBuilder historyBuilder = new StringBuilder();
        for (NPCMessage message : this.chatHistory) {
            if (historyBuilder.length() > 0) {
                historyBuilder.append("\n");  // 为每条消息添加换行符，保持清晰的格式
            }
            historyBuilder.append(message.getSender() + ": " + message.getContent());
        }
        return historyBuilder.toString();
    }

    public void updateTaskStatus(String taskId, boolean completed) {
        for (TaskModel task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                task.setCompleted(completed);
                break;
            }
        }
    }

    public void addItem(ItemModel item) {
        System.out.println("Give " + item.getName() + " to " + name);
        boolean needed = checkTasks(item);
        if (!needed) {
            System.out.println(name + " does not need " + item.getName() + ".");
        } else {
            items.add(item);
            System.out.println(name + " accepts " + item.getName() + ".");
        }
    }

    private boolean checkTasks(ItemModel item) {
        boolean neededByAnyTask = false;
        for (TaskModel task : tasks) {
            if (task.getObjectName() != null && item.getName().equals(task.getObjectName())) {
                task.setCompleted(true);
                neededByAnyTask = true;
            }
        }
        return neededByAnyTask;
    }

    public List<String> getDialogues() {  // 返回类型是 List<String>
        return dialogues;
    }
}
