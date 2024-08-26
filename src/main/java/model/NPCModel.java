package model;

import metadata.NPCMessage;

import java.util.ArrayList;
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
}
