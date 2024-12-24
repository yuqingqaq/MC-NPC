package system;

import model.NPCModel;
import model.TaskModel;

import java.util.*;

public class TaskSystem {
    private final Map<String, TaskModel> taskMap; // 任务存储
    private final Map<String, List<NPCModel>> npcByStage; // 按阶段存储 NPC

    public TaskSystem() {
        this.taskMap = new HashMap<>();
        this.npcByStage = new HashMap<>();
    }

    // 添加任务
    public void addTask(TaskModel task) {
        taskMap.put(task.getTaskId(), task);
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
}