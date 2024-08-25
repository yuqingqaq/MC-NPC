package entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.nbt.CompoundTag;

public class NPCData extends SavedData {
    private static final String DATA_NAME = "npc_unique_data";

    private boolean npcExists;
    private int npcX, npcY, npcZ;

    public NPCData() {
        // 默认构造函数
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("npcExists", npcExists);
        tag.putInt("npcX", npcX);
        tag.putInt("npcY", npcY);
        tag.putInt("npcZ", npcZ);
        return tag;
    }

    public static NPCData load(CompoundTag tag) {
        NPCData data = new NPCData();
        data.npcExists = tag.getBoolean("npcExists");
        data.npcX = tag.getInt("npcX");
        data.npcY = tag.getInt("npcY");
        data.npcZ = tag.getInt("npcZ");
        return data;
    }

    public static NPCData forLevel(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(NPCData::load, NPCData::new, DATA_NAME);
    }

    // Getter methods
    public boolean getNpcExists() {
        return npcExists;
    }

    public int getNpcX() {
        return npcX;
    }

    public int getNpcY() {
        return npcY;
    }

    public int getNpcZ() {
        return npcZ;
    }

    // Setter methods
    public void setNpcExists(boolean npcExists) {
        this.npcExists = npcExists;
    }

    public void setNpcX(int npcX) {
        this.npcX = npcX;
    }

    public void setNpcY(int npcY) {
        this.npcY = npcY;
    }

    public void setNpcZ(int npcZ) {
        this.npcZ = npcZ;
    }
}