package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import registry.ItemRegistry;

public class GoldCoinSpawner {

    public static void spawnGoldCoin(ServerLevel level, BlockPos pos, String stageId) {
        // 调整物品位置到方块中心
        double alignedX = Math.floor(pos.getX()) + 0.5;
        double alignedY = pos.getY();
        double alignedZ = Math.floor(pos.getZ()) + 0.5;

        // 创建金币物品
        ItemStack goldCoinStack = new ItemStack(ItemRegistry.GOLD_COIN.get());
        ItemEntity goldCoinEntity = new ItemEntity(level, alignedX, alignedY, alignedZ, goldCoinStack);

        // 添加到服务端的世界中
        level.addFreshEntity(goldCoinEntity);

        // 记录位置
        GoldCoinTracker.addCoinPosition(new BlockPos(alignedX, alignedY, alignedZ), stageId);

        System.out.println("Generated gold coin at " + pos + " for stage " + stageId);
    }

    // 可以添加带随机范围的变种
    public static void spawnGoldCoinWithRadius(ServerLevel level, BlockPos center, int radius, String stageId) {
        double x = center.getX() + (Math.random() * radius * 2 - radius);
        double z = center.getZ() + (Math.random() * radius * 2 - radius);
        spawnGoldCoin(level, new BlockPos(x, center.getY(), z), stageId);
    }

}