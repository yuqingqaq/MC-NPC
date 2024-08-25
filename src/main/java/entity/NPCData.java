package entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;

public class NPCData extends SavedData {
    private static final String DATA_NAME = "npc_unique_data";

    private boolean npcExists;
    private UUID npcUuid;  // UUID to identify the normal NPC
    private UUID professorUuid;  // UUID to identify the Professor NPC
    private int npcX, npcY, npcZ;

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("npcExists", npcExists);
        if (npcExists) {
            if (npcUuid != null) {
                tag.putUUID("npcUuid", npcUuid);
            }
            if (professorUuid != null) {
                tag.putUUID("professorUuid", professorUuid);
            }
        }
        tag.putInt("npcX", npcX);
        tag.putInt("npcY", npcY);
        tag.putInt("npcZ", npcZ);
        return tag;
    }

    public static NPCData load(CompoundTag tag) {
        NPCData data = new NPCData();
        data.npcExists = tag.getBoolean("npcExists");
        if (data.npcExists) {
            data.npcUuid = tag.getUUID("npcUuid");
            data.professorUuid = tag.getUUID("professorUuid");
            data.npcX = tag.getInt("npcX");
            data.npcY = tag.getInt("npcY");
            data.npcZ = tag.getInt("npcZ");
        }
        return data;
    }

    public static NPCData forLevel(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(NPCData::load, NPCData::new, DATA_NAME);
    }

    public void setNpcExists(boolean exists) {
        this.npcExists = exists;
        setDirty();
    }

    public boolean getNpcExists() {
        return this.npcExists;
    }

    public void setNpcUuid(UUID uuid) {
        this.npcUuid = uuid;
        setDirty();
    }

    public UUID getNpcUuid() {
        return this.npcUuid;
    }

    public void setProfessorUuid(UUID uuid) {
        this.professorUuid = uuid;
        setDirty();
    }

    public UUID getProfessorUuid() {
        return this.professorUuid;
    }

    public void deleteData() {
        this.npcExists = false;
        this.npcUuid = null;
        this.professorUuid = null;
        this.npcX = 0;
        this.npcY = 0;
        this.npcZ = 0;
        setDirty();
    }
}