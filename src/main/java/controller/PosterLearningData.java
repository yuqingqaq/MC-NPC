package controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import npcopenai.NPCOpenAI;

/**
 * 管理玩家的海报学习状态（非持久化，仅会话内有效）
 */
@Mod.EventBusSubscriber(modid = NPCOpenAI.MODID)
public class PosterLearningData {
    private static final Logger LOGGER = Logger.getLogger("PosterLearningData");

    // 使用线程安全的Map来存储每个玩家学习过的海报
    private static final Map<UUID, Set<String>> PLAYER_LEARNED_POSTERS = new ConcurrentHashMap<>();

    /**
     * 检查玩家是否已学习指定海报
     */
    public static boolean hasLearned(Player player, String posterType) {
        if (player == null || posterType == null) return false;

        UUID playerId = player.getUUID();
        Set<String> learnedPosters = PLAYER_LEARNED_POSTERS.get(playerId);

        return learnedPosters != null && learnedPosters.contains(posterType);
    }

    /**
     * 标记玩家已学习指定海报
     */
    public static void markAsLearned(Player player, String posterType) {
        if (player == null || posterType == null) return;

        UUID playerId = player.getUUID();

        // 获取或创建玩家的学习记录集合
        Set<String> learnedPosters = PLAYER_LEARNED_POSTERS.computeIfAbsent(
                playerId, k -> new HashSet<>()
        );

        // 添加到学习记录
        if (learnedPosters.add(posterType)) {
            System.out.println("Player " + player.getName().getString() + " learned poster: " + posterType);
        }
    }

    /**
     * 获取玩家已学习的所有海报类型
     */
    public static String[] getLearnedPosters(Player player) {
        if (player == null) return new String[0];

        UUID playerId = player.getUUID();
        Set<String> learnedPosters = PLAYER_LEARNED_POSTERS.get(playerId);

        if (learnedPosters != null && !learnedPosters.isEmpty()) {
            return learnedPosters.toArray(new String[0]);
        }

        return new String[0];
    }

    /**
     * 清除玩家的学习记录
     */
    public static void clearPlayerLearning(UUID playerId) {
        if (playerId != null) {
            PLAYER_LEARNED_POSTERS.remove(playerId);
        }
    }

    /**
     * 清除所有学习记录
     */
    public static void clearAllLearning() {
        PLAYER_LEARNED_POSTERS.clear();
        LOGGER.info("All player poster learning records cleared");
    }

    /**
     * 在服务器关闭时清除所有学习记录
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        clearAllLearning();
    }
}