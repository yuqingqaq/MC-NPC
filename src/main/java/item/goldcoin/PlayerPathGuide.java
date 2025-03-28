package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import system.StageManager.StageInfo;
import system.StageManager;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerPathGuide {

    // 当前目标金币位置
    private static BlockPos currentTarget = null;

    // 上一次生成粒子的时间
    private static long lastParticleTime = 0;

    // 冷却时间（毫秒）
    private static final long PARTICLE_COOLDOWN = 3000; // 3秒

    // 上一次玩家的位置
    private static Vec3 lastPlayerPos = null;

    // 玩家移动的距离阈值
    private static final double MOVE_THRESHOLD = 5; // 5格

    public static BlockPos getCurrentTarget() {
        return currentTarget;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        // 确保逻辑只在服务器端执行
        if (!player.level.isClientSide && event.phase == TickEvent.Phase.END) {
            ServerLevel serverLevel = (ServerLevel) player.level;

            // 获取玩家位置
            Vec3 playerPos = player.position();

            // 获取阶段管理器
            StageManager stageManager = GameController.getInstance().getStageManager();
            StageInfo currentStage = stageManager.getCurrentStage();

            if (currentStage != null) {
                // 找到当前阶段的最近金币
                BlockPos nearestCoin = GoldCoinTracker.findNearestCoinForStage(player.blockPosition(), currentStage.getId());

                if (nearestCoin != null) {
                    currentTarget = nearestCoin; // 更新目标位置

                    // 计算时间条件是否满足
                    long currentTime = System.currentTimeMillis();
                    boolean isCooldownReady = (currentTime - lastParticleTime >= PARTICLE_COOLDOWN);

                    // 检查玩家是否移动足够的距离
                    boolean hasMovedEnough = (lastPlayerPos == null || playerPos.distanceTo(lastPlayerPos) >= MOVE_THRESHOLD);

                    // 如果满足冷却时间或移动距离条件，生成粒子效果
                    if (isCooldownReady || hasMovedEnough) {
                        lastParticleTime = currentTime; // 更新最后生成时间
                        lastPlayerPos = playerPos;     // 更新玩家位置

                        // 生成粒子路径
                        PathFinding.generatePathParticles(serverLevel, playerPos, nearestCoin);
                    }
                } else {
                    currentTarget = null; // 没有目标时清空
                    lastPlayerPos = null; // 重置玩家位置
                }
            } else {
                currentTarget = null;
                lastPlayerPos = null;
            }
        }
    }
}