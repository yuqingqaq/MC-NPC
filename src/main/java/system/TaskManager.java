package system;

import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import controller.GameController;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

public class TaskManager {
    private static TaskManager instance;
    private List<AdaptiveTaskModel> currentTasks;
    private AdaptiveTaskModel currentTaskInOverview;
    private AdaptiveTaskModel currentActiveTask; // 当前正在执行的主任务

    // 存储每个主任务对应的排序后的子任务列表
    private Map<AdaptiveTaskModel, List<AdaptiveSubTaskModel>> sortedSubTasksMap;

    private TaskManager() {
        loadTasks();
        sortedSubTasksMap = new HashMap<>();
    }

    // 单例模式（获取 TaskManager 实例）
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    // 加载任务（从 GameController 获取任务列表）
    private void loadTasks() {
        currentTasks = GameController.getInstance().getAdaptiveTasks();
    }

    // 设置任务列表
    public void setTasks(List<AdaptiveTaskModel> tasks) {
        this.currentTasks = tasks;
    }

    // 获取任务列表
    public List<AdaptiveTaskModel> getTasks() {
        return currentTasks;
    }

    // 获取当前在Overview中显示的任务
    public AdaptiveTaskModel getCurrentTaskInOverview() {
        return currentTaskInOverview;
    }

    // 设置当前在Overview中显示的任务
    public void setCurrentTaskInOverview(AdaptiveTaskModel task) {
        this.currentTaskInOverview = task;
    }

    // 获取当前正在执行的任务
    public AdaptiveTaskModel getCurrentActiveTask() {
        return currentActiveTask;
    }

    // 设置当前正在执行的任务
    public void setCurrentActiveTask(AdaptiveTaskModel task) {
        this.currentActiveTask = task;
        System.out.println("Current active task: " + (task != null ? task.getTitle() : "None"));
    }

    // 为任务设置排序后的子任务列表
    public void setSortedSubTasks(AdaptiveTaskModel task, List<AdaptiveSubTaskModel> sortedSubTasks) {
        if (task != null && sortedSubTasks != null) {
            sortedSubTasksMap.put(task, new ArrayList<>(sortedSubTasks));
            System.out.println("Set sorted sub-tasks for task: " + task.getTitle() + ", count: " + sortedSubTasks.size());
        }
    }

    // 获取任务的排序后子任务列表
    public List<AdaptiveSubTaskModel> getSortedSubTasks(AdaptiveTaskModel task) {
        if (task == null) {
            return new ArrayList<>();
        }
        return sortedSubTasksMap.getOrDefault(task, new ArrayList<>(task.getSubTasks()));
    }

    // 开始一个子任务，同时传入排序后的子任务列表
    public void startSubTask(AdaptiveSubTaskModel subTask, List<AdaptiveSubTaskModel> sortedSubTasks) {
        if (subTask == null) {
            System.out.println("Cannot start null sub-task");
            return;
        }

        // 确保子任务状态正确
        if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.NOT_STARTED) {
            subTask.setStatus(AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS);

            // 启动时间管理器来跟踪任务时间
            TimeManager.getInstance().startTrackingTime(subTask);

            System.out.println("Sub-task started: " + subTask.getTitle());
        }

        // 更新父任务的状态
        AdaptiveTaskModel parentTask = getParentTask(subTask);
        if (parentTask != null) {
            // 设置排序后的子任务列表
            if (sortedSubTasks != null && !sortedSubTasks.isEmpty()) {
                setSortedSubTasks(parentTask, sortedSubTasks);
            }

            parentTask.setStatus(AdaptiveTaskModel.TaskStatus.IN_PROGRESS);

            // 设置父任务为当前活动任务
            setCurrentActiveTask(parentTask);
        }
    }

    // 兼容旧版本的startSubTask方法
    public void startSubTask(AdaptiveSubTaskModel subTask) {
        startSubTask(subTask, null);
    }

    // 完成子任务并自动开始下一个
    public void completeSubTask(AdaptiveSubTaskModel subTask) {
        if (subTask == null) {
            return;
        }

        subTask.setStatus(AdaptiveSubTaskModel.TaskStatus.COMPLETED);

        // 停止时间追踪
        TimeManager.getInstance().stopTrackingTime(subTask);

        System.out.println("Sub-task completed: " + subTask.getTitle());

        // 更新父任务的状态并检查是否需要启动下一个子任务
        AdaptiveTaskModel parentTask = getParentTask(subTask);
        if (parentTask != null) {
            // 获取排序后的子任务列表
            List<AdaptiveSubTaskModel> sortedSubTasks = getSortedSubTasks(parentTask);

            // 检查所有子任务是否完成
            boolean allSubTasksCompleted = true;
            AdaptiveSubTaskModel nextSubTask = null;

            // 找到当前完成任务在排序列表中的位置
            int currentIndex = -1;
            for (int i = 0; i < sortedSubTasks.size(); i++) {
                if (sortedSubTasks.get(i).equals(subTask)) {
                    currentIndex = i;
                    break;
                }
            }

            // 如果找到了当前任务的位置，寻找下一个未完成的任务
            if (currentIndex != -1 && currentIndex < sortedSubTasks.size() - 1) {
                for (int i = currentIndex + 1; i < sortedSubTasks.size(); i++) {
                    AdaptiveSubTaskModel candidate = sortedSubTasks.get(i);
                    if (candidate.getStatus() != AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                        nextSubTask = candidate;
                        break;
                    }
                }
            }

            // 检查是否有未完成的子任务
            for (AdaptiveSubTaskModel subTaskItem : parentTask.getSubTasks()) {
                if (subTaskItem.getStatus() != AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                    allSubTasksCompleted = false;
                    break;
                }
            }

            // 更新父任务状态
            parentTask.setStatus(allSubTasksCompleted ?
                    AdaptiveTaskModel.TaskStatus.COMPLETED :
                    AdaptiveTaskModel.TaskStatus.IN_PROGRESS);

            // 如果所有子任务都已完成，取消当前活动任务状态
            if (allSubTasksCompleted) {
                if (parentTask.equals(currentActiveTask)) {
                    setCurrentActiveTask(null);
                }
                System.out.println("All sub-tasks completed! Task completed: " + parentTask.getTitle());
            } else if (nextSubTask != null) {
                // 自动开始下一个子任务
                startSubTask(nextSubTask);
                System.out.println("Automatically starting next sub-task: " + nextSubTask.getTitle());
            }
        }
    }

    // 获取父任务（用于更新父任务状态）
    private AdaptiveTaskModel getParentTask(AdaptiveSubTaskModel subTask) {
        for (AdaptiveTaskModel task : currentTasks) {
            if (task.getSubTasks().contains(subTask)) {
                return task;
            }
        }
        return null;
    }

    // 新增方法：查找特定标题的子任务
    public AdaptiveSubTaskModel findSubTaskByTitle(String title) {
        if (title == null || title.isEmpty()) {
            return null;
        }

        for (AdaptiveTaskModel task : currentTasks) {
            for (AdaptiveSubTaskModel subTask : task.getSubTasks()) {
                if (title.equals(subTask.getTitle())) {
                    return subTask;
                }
            }
        }
        return null;
    }

    // 新增方法：根据标题完成子任务
    public boolean completeSubTaskByTitle(String title, boolean showNotification) {
        AdaptiveSubTaskModel subTask = findSubTaskByTitle(title);

        if (subTask != null) {
            // 如果子任务未完成，则标记为完成
            if (subTask.getStatus() != AdaptiveSubTaskModel.TaskStatus.COMPLETED) {
                completeSubTask(subTask);

                // 如果需要，显示通知
                if (showNotification && Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.displayClientMessage(
                            new TextComponent("Task completed: " + title),
                            false
                    );
                }

                return true;
            } else {
                // 任务已经完成
                System.out.println("Sub-task already completed: " + title);
                return false;
            }
        } else {
            // 未找到任务
            System.out.println("Could not find sub-task with title: " + title);
            return false;
        }
    }
}