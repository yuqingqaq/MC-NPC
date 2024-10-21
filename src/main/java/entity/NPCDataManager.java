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
    public static void saveOrUpdateNPC(ServerLevel world, int index, Entity npc) {
        NPCData data = NPCData.forLevel(world);
        data.registerNPC(index, npc.getUUID(), npc.blockPosition());
        activeNPCs.put(index, npc.getUUID());
        data.setDirty();  // 标记为需要保存
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
//    public static void deleteNPCData(ServerLevel world, int index) {
//        NPCData data = NPCData.forLevel(world);
//        data.removeNPC(index);
//        activeNPCs.remove(index);
//    }

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
                data.registerNPC(index, uuid, npc.blockPosition());  // 更新 NPC 数据
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

            // 确保区块已加载
            if (world.isLoaded(pos)) {
                Entity npc = findNPCByUUID(world, uuid);
                if (npc != null) {
                    activeNPCs.put(index, uuid);
                    System.out.println("NPC with index " + index + " and UUID " + uuid + " found on world load.");
                } else {
                    System.out.println("NPC with index " + index + " and UUID " + uuid + " not found on world load.");
                    // 这里可以考虑重新创建NPC或进行其他处理
                }
            } else {
                System.out.println("Chunk not loaded for NPC with index " + index);
            }
        }
    }

}