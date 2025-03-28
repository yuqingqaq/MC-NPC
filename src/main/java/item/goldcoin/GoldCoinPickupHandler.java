package item.goldcoin;

import controller.GameController;
import model.NPCModel;
import model.TaskModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import prompt.TaskPrompts;
import registry.ItemRegistry;
import system.TaskManager;
import system.TaskSystem;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinPickupHandler {

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level.isClientSide()) {
            return; // 只在服务端处理玩家拾取事件
        }

        // 获取拾取的物品
        ItemStack pickedUpItem = event.getItem().getItem();

        // 检查是否为自定义金币
        if (pickedUpItem.getItem() != ItemRegistry.GOLD_COIN.get()) {
            return; // 不是金币，忽略
        }

        // 获取金币位置
        BlockPos coinPos = event.getItem().blockPosition();
        System.out.println("Player picked up a gold coin at position: " + coinPos);

        // 从追踪器中移除金币位置
        GoldCoinTracker.removeCoinPosition(coinPos);

        // 获取任务系统并标记当前任务完成
        TaskSystem taskSystem = GameController.getInstance().getTaskSystem();

        // 在标记完成之前获取当前任务的信息
        TaskModel currentTask = taskSystem.getCurrentTask();
        String nextLocation = null;

        if (currentTask != null) {
            // 尝试找到当前任务所属的NPC和位置
            String currentLocation = findLocationForTask(currentTask, taskSystem);
            System.out.println("Current task at location: " + currentLocation);

            // 标记当前任务完成
            taskSystem.markCurrentTaskCompleted();

            // 获取下一个任务的位置信息
            nextLocation = taskSystem.getNextTaskLocation();
        } else {
            System.out.println("No current task found");
            // 仍然标记任务完成以推进任务系统
            taskSystem.markCurrentTaskCompleted();
        }

        if (nextLocation != null) {
            try {
                // 解析下一个任务位置
                String[] locationParts = nextLocation.split(",");
                if (locationParts.length == 3) {
                    int x = Integer.parseInt(locationParts[0].trim());
                    int y = Integer.parseInt(locationParts[1].trim());
                    int z = Integer.parseInt(locationParts[2].trim());

                    // 生成下一个任务的金币
                    GoldCoinSpawner.spawnGoldCoins(
                            (ServerLevel) player.level,
                            new BlockPos(x, y, z),
                            1, // 半径
                            1   // 数量
                    );

                    player.displayClientMessage(
                            new TextComponent("Next gold coin spawned at: " + nextLocation),
                            false
                    );
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid location format for next task: " + nextLocation);
                player.displayClientMessage(
                        new TextComponent("Error: Invalid location format for next task"),
                        false
                );
            }
        } else {
            // 所有任务完成
            player.displayClientMessage(new TextComponent("All tasks completed!"), false);

            TaskManager.getInstance().completeSubTaskByTitle("收集校徽碎片", true);
            player.displayClientMessage(
                    new TextComponent("Subtask '收集校徽碎片' completed!"),
                    false
            );

        }
    }

    // 辅助方法：查找任务所属的位置
    private static String findLocationForTask(TaskModel task, TaskSystem taskSystem) {
        // 遍历所有NPC及其位置
        for (Map.Entry<String, List<NPCModel>> entry : taskSystem.getNpcByStage().entrySet()) {
            String location = entry.getKey();
            List<NPCModel> npcs = entry.getValue();

            // 检查每个NPC的任务列表
            for (NPCModel npc : npcs) {
                if (npc.getTasks().contains(task)) {
                    return location;
                }
            }
        }
        return null;
    }

}