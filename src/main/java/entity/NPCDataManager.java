package entity;

import entity.NPCData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class NPCDataManager {
    // 保存 NPC 数据到世界的持久存储中
    public static void saveNPC(ServerLevel world, Entity npc) {
        NPCData data = NPCData.forLevel(world);
        data.setNpcExists(true);
        data.setNpcX(npc.getBlockX());
        data.setNpcY(npc.getBlockY());
        data.setNpcZ(npc.getBlockZ());
        data.setDirty(); // 标记数据为已更改，需要保存
    }

    // 检查 NPC 是否存在，并可以根据需要执行进一步的逻辑
    public static void checkNPCExists(ServerLevel world) {
        NPCData data = NPCData.forLevel(world);
        if (data.getNpcExists()) {
            // NPC 已存在
            int x = data.getNpcX();
            int y = data.getNpcY();
            int z = data.getNpcZ();
            // 根据这些坐标做一些操作，比如生成 NPC 或验证位置等
        }
    }
}