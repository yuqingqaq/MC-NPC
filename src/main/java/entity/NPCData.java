package entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCData extends SavedData {
    private static final String DATA_NAME = "npc_data";
    private Map<Integer, NPCDetails> npcDetailsMap = new HashMap<>();

    public static class NPCDetails {
        public UUID uuid;
        public BlockPos position;
        public String type; // 添加类型字段

        public NPCDetails(UUID uuid, BlockPos position, String type) {
            this.uuid = uuid;
            this.position = position;
            this.type = type; // 初始化类型
        }
    }
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        npcDetailsMap.forEach((index, details) -> {
            CompoundTag npcTag = new CompoundTag();
            npcTag.putInt("Index", index);
            npcTag.putUUID("UUID", details.uuid);
            npcTag.putLong("Pos", details.position.asLong());
            npcTag.putString("Type", details.type); // 保存类型信息
            list.add(npcTag);
        });
        tag.put("NPCs", list);
        return tag;
    }

    public static NPCData load(CompoundTag tag) {
        NPCData data = new NPCData();
        ListTag list = tag.getList("NPCs", 10); // 10 for CompoundTag
        list.forEach(n -> {
            CompoundTag npcTag = (CompoundTag) n;
            int index = npcTag.getInt("Index");
            UUID uuid = npcTag.getUUID("UUID");
            BlockPos position = BlockPos.of(npcTag.getLong("Pos"));
            String type = npcTag.getString("Type"); // 加载类型信息
            data.npcDetailsMap.put(index, new NPCDetails(uuid, position, type));
        });
        return data;
    }

    public void registerNPC(int index, UUID uuid, BlockPos position, String type) {
        npcDetailsMap.put(index, new NPCDetails(uuid, position, type));
        setDirty();
        System.out.println("Registered NPC with index: " + index + " and UUID: " + uuid);
    }

    public Map<Integer, NPCDetails> getNpcDetailsMap() {
        return npcDetailsMap;
    }

    public static NPCData forLevel(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(NPCData::load, NPCData::new, DATA_NAME);
    }

    private static BlockPos getNpcPositionByUUID(ServerLevel world, UUID uuid) {
        NPCData data = NPCData.forLevel(world);
        for (NPCData.NPCDetails details : data.getNpcDetailsMap().values()) {
            if (details.uuid.equals(uuid)) {
                return details.position;
            }
        }
        return null; // Return null if the UUID is not found
    }

}