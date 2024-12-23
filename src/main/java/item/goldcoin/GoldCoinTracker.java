package item.goldcoin;

import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class GoldCoinTracker {
    private static final List<BlockPos> goldCoinPositions = new ArrayList<>();

    // 添加一个金币的位置
    public static void addCoinPosition(BlockPos pos) {
        goldCoinPositions.add(pos);
    }

    // 获取所有金币的位置
    public static List<BlockPos> getCoinPositions() {
        return new ArrayList<>(goldCoinPositions); // 返回副本以避免直接修改
    }

    public static boolean removeCoinPosition(BlockPos pos) {
        BlockPos closestPos = null;
        double closestDistance = Double.MAX_VALUE;

        // 遍历所有追踪的金币位置
        for (BlockPos trackedPos : goldCoinPositions) {
            // 检查是否在玩家吸引范围内
            double distance = Math.sqrt(
                    Math.pow(trackedPos.getX() - pos.getX(), 2) +
                            Math.pow(trackedPos.getY() - pos.getY(), 2) +
                            Math.pow(trackedPos.getZ() - pos.getZ(), 2)
            );

            if (distance <= 1.5 && distance < closestDistance) { // 吸引范围 1.5 格
                closestPos = trackedPos;
                closestDistance = distance;
            }
        }

        if (closestPos != null) {
            goldCoinPositions.remove(closestPos);
            return true;
        }

        return false;
    }

    // 找到最近的金币位置
    public static BlockPos findNearestCoin(BlockPos playerPos) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (BlockPos pos : goldCoinPositions) {
            double distance = playerPos.distSqr(pos);
            if (distance < nearestDistance) {
                nearest = pos;
                nearestDistance = distance;
            }
        }

        return nearest;
    }
}