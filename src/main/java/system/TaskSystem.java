package system;

import model.NPCModel;
import model.TaskModel;

import java.util.*;

public class TaskSystem {
    private final Map<String, TaskModel> taskMap; // 任务存储
    private final Map<String, List<NPCModel>> npcByStage; // 按阶段存储 NPC
    private int currentTaskIndex = 0; // 当前任务索引
    private final List<TaskModel> taskSequence; // 按顺序的任务列表

    public TaskSystem() {
        this.taskMap = new HashMap<>();
        this.npcByStage = new HashMap<>();
        this.taskSequence = new ArrayList<>();
    }

    // 添加任务
    public void addTask(TaskModel task) {
        taskMap.put(task.getTaskId(), task);
        taskSequence.add(task); // 按顺序存储任务
    }

    // 添加 NPC 到指定阶段
    public void addNPCToStage(String stage, NPCModel npc) {
        npcByStage.computeIfAbsent(stage.toUpperCase(), k -> new ArrayList<>()).add(npc);
    }

    // 获取某阶段的 NPC 列表
    public List<NPCModel> getNPCsByStage(String stage) {
        return npcByStage.getOrDefault(stage.toUpperCase(), Collections.emptyList());
    }

    // 获取任务通过 ID
    public TaskModel getTaskById(String taskId) {
        return taskMap.get(taskId);
    }

    // 更新任务完成状态
    public void updateTaskStatus(String taskId, boolean completed) {
        TaskModel task = taskMap.get(taskId);
        if (task != null) {
            task.setCompleted(completed);
        }
    }

    // 检查某阶段的所有任务是否完成
    public boolean areAllTasksCompletedInStage(String stage) {
        List<NPCModel> npcs = getNPCsByStage(stage);
        return npcs.stream().allMatch(NPCModel::areAllTasksCompleted);
    }

    // 根据位置完成任务
    public void completeTaskByLocation(String location) {
        for (NPCModel npc : npcByStage.values().stream().flatMap(Collection::stream).toList()) {
            if (npc.getLocation().equalsIgnoreCase(location)) {
                List<TaskModel> tasks = npc.getTasks();
                for (TaskModel task : tasks) {
                    if (!task.isCompleted()) {
                        task.setCompleted(true);
                        System.out.println("Task " + task.getTaskId() + " completed for location: " + location);
                    }
                }
            }
        }
    }

    // 获取当前任务
    public TaskModel getCurrentTask() {
        if (currentTaskIndex < taskSequence.size()) {
            return taskSequence.get(currentTaskIndex);
        }
        return null; // 没有更多任务
    }

    // 标记当前任务完成并移动到下一个任务
    public void markCurrentTaskCompleted() {
        if (currentTaskIndex < taskSequence.size()) {
            TaskModel currentTask = taskSequence.get(currentTaskIndex);
            currentTask.setCompleted(true);
            currentTaskIndex++;
        }
    }

    // 获取下一个任务的 NPC 位置信息
    public String getNextTaskLocation() {
        if (currentTaskIndex < taskSequence.size()) {
            return taskSequence.get(currentTaskIndex).getCoinLocation();
        }
        return null; // 没有更多任务
    }
}