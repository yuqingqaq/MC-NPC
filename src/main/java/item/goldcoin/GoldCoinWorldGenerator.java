package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import system.TaskSystem;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinWorldGenerator {

    private static boolean hasGenerated = false; // 防止重复生成

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel serverLevel && !hasGenerated) {
            hasGenerated = true; // 确保只生成一次

            // 获取 TaskSystem
            TaskSystem taskSystem = GameController.getInstance().getTaskSystem();

            // 获取第一个任务的位置
            String firstLocation = taskSystem.getNextTaskLocation();
            if (firstLocation != null) {
                String[] locationParts = firstLocation.split(",");
                if (locationParts.length == 3) {
                    try {
                        int x = Integer.parseInt(locationParts[0].trim());
                        int y = Integer.parseInt(locationParts[1].trim());
                        int z = Integer.parseInt(locationParts[2].trim());

                        // 生成第一个金币
                        GoldCoinSpawner.spawnGoldCoins(serverLevel, new BlockPos(x, y, z), 1, 1);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid location format for first task: " + firstLocation);
                    }
                }
            }
        }
    }
}