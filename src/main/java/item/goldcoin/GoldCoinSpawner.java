package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import registry.ItemRegistry;

import java.util.Random;

public class GoldCoinSpawner {

    private static final Random RANDOM = new Random();

    /**
     * 在服务端生成金币实体
     *
     * @param level  服务端世界实例
     * @param center 中心位置
     * @param radius 生成范围（半径）
     * @param count  生成金币的数量
     */
    public static void spawnGoldCoins(ServerLevel level, BlockPos center, int radius, int count) {
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

            // 记录位置
            GoldCoinTracker.addCoinPosition(coinPos);

            System.out.printf("Generated gold coin at %s%n", coinPos);
        }
    }
}