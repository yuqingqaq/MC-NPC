package item.goldcoin;

import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prompt.TaskPrompts;

public class GoldCoinTracker {
    // 存储每个阶段的金币位置
    private static final Map<TaskPrompts.TaskStage, List<BlockPos>> stageCoins = new HashMap<>();

    // 添加一个金币与阶段的关联
    public static void addCoinPosition(BlockPos pos, TaskPrompts.TaskStage stage) {
        stageCoins.computeIfAbsent(stage, k -> new ArrayList<>()).add(pos);
        System.out.println("Added gold coin to tracker at position: " + pos + " for stage: " + stage);
    }

    // 添加一个金币（不指定阶段，兼容旧代码）
    public static void addCoinPosition(BlockPos pos) {
        // 尝试确定当前的游戏阶段，如果无法确定则使用ADMIN作为默认值
        TaskPrompts.TaskStage currentStage = TaskPrompts.TaskStage.ADMIN;
        addCoinPosition(pos, currentStage);
    }

    // 获取特定阶段的所有金币位置
    public static List<BlockPos> getCoinsForStage(TaskPrompts.TaskStage stage) {
        return new ArrayList<>(stageCoins.getOrDefault(stage, new ArrayList<>()));
    }

    // 获取所有金币位置（兼容旧代码）
    public static List<BlockPos> getCoinPositions() {
        List<BlockPos> allCoins = new ArrayList<>();
        for (List<BlockPos> coins : stageCoins.values()) {
            allCoins.addAll(coins);
        }
        return allCoins;
    }

    // 移除指定位置的金币
    public static boolean removeCoinPosition(BlockPos pos) {
        for (Map.Entry<TaskPrompts.TaskStage, List<BlockPos>> entry : stageCoins.entrySet()) {
            List<BlockPos> stagePositions = entry.getValue();
            for (BlockPos coinPos : new ArrayList<>(stagePositions)) {
                if (coinPos.distSqr(pos) <= 2.25) { // 1.5^2 = 2.25
                    stagePositions.remove(coinPos);
                    System.out.println("Removed gold coin from tracker at position: " + coinPos + " for stage: " + entry.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    // 找到指定阶段的最近金币位置
    public static BlockPos findNearestCoinForStage(BlockPos playerPos, TaskPrompts.TaskStage stage) {
        List<BlockPos> stagePositions = stageCoins.getOrDefault(stage, new ArrayList<>());
        if (stagePositions.isEmpty()) {
            return null;
        }

        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (BlockPos pos : stagePositions) {
            double distance = playerPos.distSqr(pos);
            if (distance < nearestDistance) {
                nearest = pos;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    // 找到最近的金币位置（兼容旧代码）
    public static BlockPos findNearestCoin(BlockPos playerPos) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (List<BlockPos> positions : stageCoins.values()) {
            for (BlockPos pos : positions) {
                double distance = playerPos.distSqr(pos);
                if (distance < nearestDistance) {
                    nearest = pos;
                    nearestDistance = distance;
                }
            }
        }

        return nearest;
    }

    // 清空所有记录的金币位置
    public static void clearAllCoins() {
        stageCoins.clear();
        System.out.println("Cleared all gold coin positions from tracker");
    }

    // 清空特定阶段的金币位置
    public static void clearCoinsForStage(TaskPrompts.TaskStage stage) {
        stageCoins.remove(stage);
        System.out.println("Cleared gold coin positions for stage: " + stage);
    }
}