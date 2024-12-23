package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerPathGuide {

    // 当前目标金币位置
    private static BlockPos currentTarget = null;

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

            // 找到最近的金币
            BlockPos nearestCoin = GoldCoinTracker.findNearestCoin(player.blockPosition());
            if (nearestCoin != null) {
                currentTarget = nearestCoin; // 更新目标位置
                // 生成粒子路径
                PathFinding.generatePathParticles(serverLevel, playerPos, nearestCoin);
            } else {
                currentTarget = null; // 没有目标时清空
            }
        }
    }
}