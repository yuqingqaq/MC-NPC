package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import registry.ItemRegistry;
import system.TaskSystem;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinPickupHandler {

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level.isClientSide()) { // 只在服务端处理
                // 获取拾取的物品
                ItemStack pickedUpItem = event.getItem().getItem();

                // 检查是否为自定义金币
                if (pickedUpItem.getItem() == ItemRegistry.GOLD_COIN.get()) {
                    BlockPos coinPos = event.getItem().blockPosition();
                    System.out.println("Player picked up a gold coin at position: " + coinPos);

                    // 获取 TaskSystem
                    TaskSystem taskSystem = GameController.getInstance().getTaskSystem();

                    // 标记当前任务完成
                    taskSystem.markCurrentTaskCompleted();

                    // 获取下一个任务的位置信息
                    String nextLocation = taskSystem.getNextTaskLocation();
                    if (nextLocation != null) {
                        String[] locationParts = nextLocation.split(",");
                        if (locationParts.length == 3) {
                            try {
                                int x = Integer.parseInt(locationParts[0].trim());
                                int y = Integer.parseInt(locationParts[1].trim());
                                int z = Integer.parseInt(locationParts[2].trim());

                                // 生成下一个任务的金币
                                GoldCoinSpawner.spawnGoldCoins((ServerLevel) player.level, new BlockPos(x, y, z), 1, 1);
                                player.displayClientMessage(new TextComponent("Next gold coin spawned at: " + nextLocation), false);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid location format for next task: " + nextLocation);
                            }
                        }
                    } else {
                        player.displayClientMessage(new TextComponent("All tasks completed!"), false);
                    }
                }
            }
        }
    }
}