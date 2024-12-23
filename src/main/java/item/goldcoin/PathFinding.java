package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class PathFinding {

    /**
     * 在玩家与目标之间生成粒子效果
     *
     * @param level       服务端世界
     * @param playerPos   玩家位置
     * @param targetPos   目标位置
     */
    public static void generatePathParticles(ServerLevel level, Vec3 playerPos, BlockPos targetPos) {
        // 转换目标位置为 Vec3
        Vec3 targetVec = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);

        // 每隔固定距离生成一个粒子
        double distance = playerPos.distanceTo(targetVec);
        int steps = (int) (distance / 5); // 每两格生成一个粒子

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = playerPos.x + t * (targetVec.x - playerPos.x);
            double y = playerPos.y + t * (targetVec.y - playerPos.y);
            double z = playerPos.z + t * (targetVec.z - playerPos.z);

            // 在服务端生成粒子，粒子类型可以自定义
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}