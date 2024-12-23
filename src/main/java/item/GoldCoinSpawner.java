package item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import registry.ItemRegistry;

import java.util.Random;

public class GoldCoinSpawner {

    private static final Random RANDOM = new Random();

    /**
     * 在客户端生成金币实体
     *
     * @param center 中心位置
     * @param radius 生成范围（半径）
     * @param count  生成金币的数量
     */
    public static void spawnGoldCoins(ClientLevel level, BlockPos center, int radius, int count) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return; // 确保世界实例存在

        for (int i = 0; i < count; i++) {
            // 随机生成位置
            double x = center.getX() + (RANDOM.nextDouble() * radius * 2 - radius);
            double y = center.getY();
            double z = center.getZ() + (RANDOM.nextDouble() * radius * 2 - radius);

            // 创建金币物品
            ItemStack goldCoinStack = new ItemStack(ItemRegistry.GOLD_COIN.get());
            ItemEntity goldCoinEntity = new ItemEntity(mc.level, x, y, z, goldCoinStack);
            goldCoinEntity.setNoGravity(true); // 示例：如果需要特效，可以设置无重力
            goldCoinEntity.setUnlimitedLifetime(); // 确保实体不会被自动清理

            // 添加到客户端的世界中
            mc.level.addFreshEntity(goldCoinEntity);
            System.out.println("Generated Coin at " + x + ", " + y + ", " + z);

        }
    }
}