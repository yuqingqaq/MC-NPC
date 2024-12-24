package entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NPCDataManager {
    // 用于存储每个NPC的index和其UUID
    public static Map<Integer, UUID> activeNPCs = new HashMap<>();

    public static void saveOrUpdateNPC(ServerLevel world, int index, Entity npc, String type) {
        NPCData data = NPCData.forLevel(world);
        data.registerNPC(index, npc.getUUID(), npc.blockPosition(), type);
        activeNPCs.put(index, npc.getUUID());
        data.setDirty();
    }
    // 通过index返回对应的UUID
    public static UUID getUUIDByIndex(int index) {
        return activeNPCs.get(index);
    }

    public static BlockPos getNPCLastPositionByIndex(ServerLevel world, int index) {
        NPCData data = NPCData.forLevel(world);
        NPCData.NPCDetails details = data.getNpcDetailsMap().get(index);
        if (details != null) {
            return details.position;
        }
        return null;
    }

    public static Entity findNPCByIndex(ServerLevel world, int index) {
        UUID uuid = activeNPCs.get(index);
        if (uuid == null) {
            return null;
        }
        return world.getEntity(uuid);
    }

    public static Entity findNPCByUUID(ServerLevel world, UUID uuid) {
        for (Entity entity : world.getAllEntities()) {
            if (entity.getUUID().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    public static void clearAllNPCData(ServerLevel world) {
        NPCData data = NPCData.forLevel(world);
        data.getNpcDetailsMap().clear();
        data.setDirty();
        activeNPCs.clear();
    }

    public static void saveAllNPCs(ServerLevel world) {
        NPCData data = NPCData.forLevel(world);
        activeNPCs.forEach((index, uuid) -> {
            Entity npc = findNPCByUUID(world, uuid);
            if (npc != null) {
                NPCData.NPCDetails details = data.getNpcDetailsMap().get(index);
                if (details != null) {
                    data.registerNPC(index, uuid, npc.blockPosition(), details.type);  // 使用存储的类型信息更新 NPC 数据
                }
            }
        });
        data.setDirty();  // 标记为需要保存
    }

    public static void loadAllNPCs(ServerLevel world, NPCData data) {

        for (Map.Entry<Integer, NPCData.NPCDetails> entry : data.getNpcDetailsMap().entrySet()) {
            int index = entry.getKey();
            NPCData.NPCDetails details = entry.getValue();
            UUID uuid = details.uuid;
            BlockPos pos = details.position; // 直接从 NPCDetails 获取位置
            activeNPCs.put(index, uuid);

            // 强制加载区块
            if (!world.isLoaded(pos)) {
                world.getChunk(pos.getX() >> 4, pos.getZ() >> 4); // 强制加载区块
                System.out.println("Chunk loaded for NPC at index " + index);
            }

            // 检查实体是否存在
            Entity npc = world.getEntity(uuid);
            if (npc != null) {
                activeNPCs.put(index, uuid);
                data.updateNPCLoadState(index, true); // 更新为已加载
                System.out.println("NPC with index " + index + " and UUID " + uuid + " is active.");
            } else {
                data.updateNPCLoadState(index, false); // 更新为未加载
                System.out.println("NPC with index " + index + " and UUID " + uuid + " not found.");
            }
        }
    }

    public static String getNPCTypeByIndex(ServerLevel world, int index) {
        NPCData data = NPCData.forLevel(world);
        NPCData.NPCDetails details = data.getNpcDetailsMap().get(index);
        if (details != null) {
            return details.type;
        }
        return null; // 如果没有找到对应的 NPC 记录，返回 null
    }
}