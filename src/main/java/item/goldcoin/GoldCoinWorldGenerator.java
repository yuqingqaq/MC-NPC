package item.goldcoin;

import controller.GameController;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import system.TaskSystem;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinWorldGenerator {

    public static boolean hasGenerated = false; // 防止重复生成

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel serverLevel && !hasGenerated) {
            hasGenerated = true; // 确保只生成一次

            //executeClearGoldCoinsCommand(serverLevel);

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
    /**
     * 调用 /cleargoldcoins 命令清除所有金币
     *
     * @param serverLevel 当前服务器世界
     */
    private static void executeClearGoldCoinsCommand(ServerLevel serverLevel) {
        MinecraftServer server = serverLevel.getServer(); // 获取服务器实例

        // 创建命令源（这里使用控制台作为源）
        CommandSourceStack commandSourceStack = server.createCommandSourceStack()
                .withLevel(serverLevel) // 设置命令执行的目标世界
                .withPermission(4); // 设置权限等级（4 = 管理员）

        // 调用命令
        server.getCommands().performCommand(commandSourceStack, "cleargoldcoins");
    }
}