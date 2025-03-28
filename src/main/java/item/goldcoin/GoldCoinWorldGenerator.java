package item.goldcoin;

import controller.GameController;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import system.StageManager.StageInfo;
import system.StageManager;

import java.util.List;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinWorldGenerator {

    public static boolean hasGenerated = false; // 防止重复生成

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel serverLevel && !hasGenerated) {
            hasGenerated = true; // 确保只生成一次

            System.out.println("GoldCoinWorldGenerator: Initializing gold coins...");

            // 获取阶段管理器
            StageManager stageManager = GameController.getInstance().getStageManager();

            // 获取当前阶段
            StageInfo currentStage = stageManager.getCurrentStage();

            if (currentStage == null) {
                System.out.println("GoldCoinWorldGenerator: Current stage is null");
                return;
            }

            System.out.println("GoldCoinWorldGenerator: Current stage is " + currentStage.getId());

            // 如果当前是INTRO阶段，查找第一个有金币的阶段
            if ("INTRO".equals(currentStage.getId())) {
                System.out.println("GoldCoinWorldGenerator: Current stage is INTRO, looking for first stage with coin...");
                StageInfo firstStageWithCoin = findFirstStageWithCoin(stageManager);

                if (firstStageWithCoin != null) {
                    System.out.println("GoldCoinWorldGenerator: Found stage with coin: " + firstStageWithCoin.getId());

                    // 生成该阶段的金币
                    generateCoinForStage(serverLevel, firstStageWithCoin);
                } else {
                    System.out.println("GoldCoinWorldGenerator: No stage with coin found!");
                }
                return;
            }

            // 如果当前是END阶段，不生成金币
            if ("END".equals(currentStage.getId())) {
                System.out.println("GoldCoinWorldGenerator: Current stage is END, no coins needed");
                return;
            }

            // 检查当前阶段是否有金币位置
            if (currentStage.getCoinLocation() != null && !currentStage.getCoinLocation().isEmpty()) {
                // 当前阶段有金币位置，直接生成
                generateCoinForStage(serverLevel, currentStage);
            } else {
                System.out.println("GoldCoinWorldGenerator: Current stage has no coin location");
            }
        }
    }

    /**
     * 查找第一个有金币位置的阶段（排除INTRO和END）
     */
    private static StageInfo findFirstStageWithCoin(StageManager stageManager) {
        List<StageInfo> stages = stageManager.getAllStages();
        for (StageInfo stage : stages) {
            // 跳过INTRO和END阶段
            if ("INTRO".equals(stage.getId()) || "END".equals(stage.getId())) {
                continue;
            }

            if (stage.getCoinLocation() != null && !stage.getCoinLocation().isEmpty()) {
                return stage;
            }
        }
        return null;
    }

    /**
     * 为指定阶段生成金币
     */
    private static void generateCoinForStage(ServerLevel serverLevel, StageInfo stage) {
        String locationStr = stage.getCoinLocation();
        System.out.println("GoldCoinWorldGenerator: Generating coin for stage " + stage.getId() + " at " + locationStr);

        String[] locationParts = locationStr.split(",");

        if (locationParts.length == 3) {
            try {
                int x = Integer.parseInt(locationParts[0].trim());
                int y = Integer.parseInt(locationParts[1].trim());
                int z = Integer.parseInt(locationParts[2].trim());

                // 使用新的方法生成金币
                GoldCoinSpawner.spawnGoldCoin(
                        serverLevel,
                        new BlockPos(x, y, z),
                        stage.getId()
                );

                System.out.println("GoldCoinWorldGenerator: Gold coin spawned at " + x + ", " + y + ", " + z + " (Stage: " + stage.getId() + ")");
            } catch (NumberFormatException e) {
                System.out.println("GoldCoinWorldGenerator: Invalid coordinate format: " + locationStr);
            }
        } else {
            System.out.println("GoldCoinWorldGenerator: Invalid location format: " + locationStr);
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