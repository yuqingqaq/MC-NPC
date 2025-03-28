package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import prompt.TaskPrompts;
import registry.ItemRegistry;

import java.util.Random;

public class GoldCoinSpawner {

    private static final Random RANDOM = new Random();

    // 添加使用TaskSystem生成金币的方法
    public static void spawnGoldCoinsForStage(ServerLevel level, TaskPrompts.TaskStage stage, int count) {
        // 从TaskSystem获取该阶段的金币位置
        String locationStr = GameController.getInstance().getTaskSystem().getStageCoinLocation(stage);
        if (locationStr != null) {
            String[] coords = locationStr.split(",");
            if (coords.length == 3) {
                try {
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());
                    int z = Integer.parseInt(coords[2].trim());

                    // 使用已有方法生成金币
                    spawnGoldCoins(level, new BlockPos(x, y, z), 1, count, stage);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing coordinates for stage " + stage + ": " + locationStr);
                }
            }
        }
    }

    public static void spawnGoldCoins(ServerLevel level, BlockPos center, int radius, int count, TaskPrompts.TaskStage stage) {
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            // 随机生成位置
            double x = center.getX() + (random.nextDouble() * radius * 2 - radius);
            double z = center.getZ() + (random.nextDouble() * radius * 2 - radius);
            double y = center.getY();

            // 调整物品位置到方块中心
            double alignedX = Math.floor(x) + 0.5;
            double alignedZ = Math.floor(z) + 0.5;

            BlockPos coinPos = new BlockPos(alignedX, y, alignedZ);

            // 创建金币物品
            ItemStack goldCoinStack = new ItemStack(ItemRegistry.GOLD_COIN.get());
            ItemEntity goldCoinEntity = new ItemEntity(level, alignedX, y, alignedZ, goldCoinStack);

            // 添加到服务端的世界中
            level.addFreshEntity(goldCoinEntity);

            // 记录位置和阶段
            GoldCoinTracker.addCoinPosition(coinPos, stage);

            System.out.printf("Generated gold coin at %s for stage %s%n", coinPos, stage);
        }
    }

    // 兼容旧代码的方法
    public static void spawnGoldCoins(ServerLevel level, BlockPos center, int radius, int count) {
        // 使用ADMIN作为默认阶段
        spawnGoldCoins(level, center, radius, count, TaskPrompts.TaskStage.ADMIN);
    }


}