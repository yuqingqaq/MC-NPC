package com.freedomai.projectpn.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import java.util.UUID;

public class NPCDataManager {
    public static UUID uniqueLibrarianUuid;
    public static UUID uniqueProfessorUUID;

    public static void saveNPC(UUID uuid, ServerLevel world) {
        uniqueLibrarianUuid = uuid;
        updateNPCData(world, uuid);
    }

    public static void saveProfessor(UUID uuid, ServerLevel world) {
        uniqueProfessorUUID = uuid;
        updateNPCData(world, uuid);
    }

    private static void updateNPCData(ServerLevel world, UUID uuid) {
        NPCData data = NPCData.forLevel(world);
        data.setNpcExists(true);
        data.setLibrarianUuid(uuid);
        data.setDirty();
    }

    public static Entity findNPCByUUID(ServerLevel world, UUID uuid) {
        for (Entity entity : world.getAllEntities()) {
            if (entity.getUUID().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    public static void deleteNPCData(ServerLevel world) {
        NPCData data = NPCData.forLevel(world);

        // 如果存在NPC数据，则删除
        if (data.getNpcExists()) {
            data.deleteData();
            System.out.println("NPC data has been completely removed from storage.");
        } else {
            System.out.println("No NPC data to remove.");
        }

        // 也应该重置任何静态UUID存储
        uniqueLibrarianUuid = null;
        uniqueProfessorUUID = null;
    }

    public static void checkNPCExists(ServerLevel world, NPCData data) {
// 检查普通NPC
        UUID LibrarianUuid = data.getLibrarianUuid();
        if (LibrarianUuid != null) {
            Entity npc = findNPCByUUID(world, LibrarianUuid);
            if (npc != null) {
                uniqueLibrarianUuid = LibrarianUuid;  // 更新静态UUID以保持引用
            } else {
                uniqueLibrarianUuid = null;  // NPC不存在，清除UUID
            }
        }

        // 检查教授NPC，假设有类似的方法和属性
        UUID professorUuid = data.getProfessorUuid();
        if (professorUuid != null) {
            Entity professor = findNPCByUUID(world, professorUuid);
            if (professor != null) {
                uniqueProfessorUUID = professorUuid;
            } else {
                uniqueProfessorUUID = null;
            }
        }
    }
}