package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import system.StageManager.StageInfo;
import system.StageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoldCoinTracker {
    // 存储每个阶段的金币位置
    private static final Map<String, List<BlockPos>> stageCoins = new HashMap<>();

    // 添加一个金币与阶段的关联
    public static void addCoinPosition(BlockPos pos, String stageId) {
        stageCoins.computeIfAbsent(stageId, k -> new ArrayList<>()).add(pos);
        System.out.println("Added gold coin at " + pos + " for stage " + stageId);
    }

    // 移除指定位置的金币
    public static boolean removeCoinPosition(BlockPos pos) {
        for (Map.Entry<String, List<BlockPos>> entry : stageCoins.entrySet()) {
            List<BlockPos> positions = entry.getValue();
            for (BlockPos coinPos : new ArrayList<>(positions)) {
                if (coinPos.distSqr(pos) <= 2.25) { // 1.5² = 2.25
                    positions.remove(coinPos);
                    System.out.println("Removed gold coin at " + coinPos + " from stage " + entry.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    // 获取特定阶段的金币位置
    public static List<BlockPos> getCoinsForStage(String stageId) {
        return new ArrayList<>(stageCoins.getOrDefault(stageId, new ArrayList<>()));
    }

    // 获取当前阶段的金币位置
    public static List<BlockPos> getCurrentStageCoins() {
        StageManager stageManager = GameController.getInstance().getStageManager();
        StageInfo currentStage = stageManager.getCurrentStage();

        if (currentStage != null) {
            return getCoinsForStage(currentStage.getId());
        }
        return new ArrayList<>();
    }

    // 找到特定阶段的最近金币
    public static BlockPos findNearestCoinForStage(BlockPos playerPos, String stageId) {
        List<BlockPos> positions = getCoinsForStage(stageId);
        if (positions.isEmpty()) {
            return null;
        }

        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (BlockPos pos : positions) {
            double dist = playerPos.distSqr(pos);
            if (dist < nearestDist) {
                nearest = pos;
                nearestDist = dist;
            }
        }

        return nearest;
    }

    // 找到当前阶段的最近金币
    public static BlockPos findNearestCurrentStageCoin(BlockPos playerPos) {
        StageManager stageManager = GameController.getInstance().getStageManager();
        if (stageManager.getCurrentStage() != null) {
            return findNearestCoinForStage(playerPos, stageManager.getCurrentStage().getId());
        }
        return null;
    }

    // 找到任意最近的金币（保留旧方法以兼容现有代码）
    public static BlockPos findNearestCoin(BlockPos playerPos) {
        // 优先查找当前阶段的金币
        BlockPos currentStageCoin = findNearestCurrentStageCoin(playerPos);
        if (currentStageCoin != null) {
            return currentStageCoin;
        }

        // 如果没有找到当前阶段的金币，查找所有金币
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (List<BlockPos> positions : stageCoins.values()) {
            for (BlockPos pos : positions) {
                double dist = playerPos.distSqr(pos);
                if (dist < nearestDist) {
                    nearest = pos;
                    nearestDist = dist;
                }
            }
        }

        return nearest;
    }

    // 获取所有金币位置
    public static List<BlockPos> getAllCoinPositions() {
        List<BlockPos> allCoins = new ArrayList<>();
        for (List<BlockPos> positions : stageCoins.values()) {
            allCoins.addAll(positions);
        }
        return allCoins;
    }

    // 清空所有金币
    public static void clearAllCoins() {
        stageCoins.clear();
        System.out.println("Cleared all gold coin positions");
    }

    // 清空特定阶段的金币
    public static void clearCoinsForStage(String stageId) {
        stageCoins.remove(stageId);
        System.out.println("Cleared coins for stage: " + stageId);
    }
}