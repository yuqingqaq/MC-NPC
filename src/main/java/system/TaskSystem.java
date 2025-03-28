package system;

import model.NPCModel;
import model.TaskModel;
import prompt.TaskPrompts;

import java.util.*;

public class TaskSystem {
    private final Map<String, TaskModel> taskMap; // 任务存储
    private final Map<String, List<NPCModel>> npcByStage; // 按阶段存储 NPC
    private int currentTaskIndex = 0; // 当前任务索引
    private final List<TaskModel> taskSequence; // 按顺序的任务列表

    // 存储 NPC 位置名称与 TaskStage 的映射关系
    private final Map<String, TaskPrompts.TaskStage> locationToStageMap;
    // 存储每个阶段的金币位置
    private final Map<TaskPrompts.TaskStage, String> stageCoinLocations;

    public TaskSystem() {
        this.taskMap = new HashMap<>();
        this.npcByStage = new HashMap<>();
        this.taskSequence = new ArrayList<>();
        this.locationToStageMap = new HashMap<>();
        this.stageCoinLocations = new HashMap<>();

        // 初始化位置到阶段的映射
        initLocationToStageMap();
    }

    // 初始化位置名称到阶段的映射
    private void initLocationToStageMap() {
        locationToStageMap.put("ADMIN", TaskPrompts.TaskStage.ADMIN);
        locationToStageMap.put("TA", TaskPrompts.TaskStage.TA);
        locationToStageMap.put("TB", TaskPrompts.TaskStage.TB);
        locationToStageMap.put("TC", TaskPrompts.TaskStage.TC);
        locationToStageMap.put("TD", TaskPrompts.TaskStage.TD);
        locationToStageMap.put("DAOYUAN", TaskPrompts.TaskStage.DAOYUAN);
        locationToStageMap.put("CONFERENCE", TaskPrompts.TaskStage.CONFERENCE);
        locationToStageMap.put("LIBRARY", TaskPrompts.TaskStage.LIBRARY);
        locationToStageMap.put("GYM", TaskPrompts.TaskStage.GYM);
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

    // 在TaskSystem类中添加
    public Map<String, List<NPCModel>> getNpcByStage() {
        return new HashMap<>(npcByStage);
    }
    // 在TaskSystem类中添加
    public String getCurrentStage() {
        if (currentTaskIndex < taskSequence.size()) {
            TaskModel currentTask = taskSequence.get(currentTaskIndex);
            // 查找包含当前任务的NPC
            for (Map.Entry<String, List<NPCModel>> entry : npcByStage.entrySet()) {
                for (NPCModel npc : entry.getValue()) {
                    if (npc.getTasks().contains(currentTask)) {
                        return entry.getKey(); // 返回NPC所在的位置名称
                    }
                }
            }
        }
        return "UNKNOWN"; // 如果找不到，返回默认值
    }

    // 获取下一个任务的 NPC 位置信息
    public String getNextTaskLocation() {
        if (currentTaskIndex < taskSequence.size()) {
            return taskSequence.get(currentTaskIndex).getCoinLocation();
        }
        return null; // 没有更多任务
    }

    // 获取当前任务所属的阶段
    public TaskPrompts.TaskStage getCurrentTaskStage() {
        if (currentTaskIndex < taskSequence.size()) {
            TaskModel currentTask = taskSequence.get(currentTaskIndex);
            // 根据当前任务关联的NPC位置查找对应的阶段
            for (Map.Entry<String, List<NPCModel>> entry : npcByStage.entrySet()) {
                String locationName = entry.getKey();
                for (NPCModel npc : entry.getValue()) {
                    if (npc.getTasks().contains(currentTask)) {
                        return getStageFromLocation(locationName);
                    }
                }
            }
        }

        // 如果无法确定阶段，返回默认值
        return TaskPrompts.TaskStage.INTRO;
    }

    // 根据位置名称获取对应的阶段
    public TaskPrompts.TaskStage getStageFromLocation(String locationName) {
        return locationToStageMap.getOrDefault(locationName.toUpperCase(), TaskPrompts.TaskStage.INTRO);
    }

    // 设置阶段金币位置
    public void setStageCoinLocation(TaskPrompts.TaskStage stage, String location) {
        stageCoinLocations.put(stage, location);
    }

    // 根据位置名称设置阶段金币位置
    public void setCoinLocationForLocation(String locationName, String coordinates) {
        TaskPrompts.TaskStage stage = getStageFromLocation(locationName);
        if (stage != null) {
            stageCoinLocations.put(stage, coordinates);
        }
    }

    // 获取阶段金币位置
    public String getStageCoinLocation(TaskPrompts.TaskStage stage) {
        return stageCoinLocations.get(stage);
    }

    // 获取所有阶段金币位置
    public Map<TaskPrompts.TaskStage, String> getAllStageCoinLocations() {
        return new HashMap<>(stageCoinLocations);
    }
}