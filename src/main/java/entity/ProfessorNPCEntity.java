package entity;

import controller.GameController;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.TextComponent;
import npcopenai.NPCInteractionScreen;
import npcopenai.NPCOpenAI;

public class ProfessorNPCEntity extends Mob {
    private static final EntityDataAccessor<Integer> NPC_INDEX = SynchedEntityData.defineId(ProfessorNPCEntity.class, EntityDataSerializers.INT);
    private NPCModel npc;
    public ProfessorNPCEntity(EntityType<? extends Mob> type, Level world) {
        super(type, world);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("NPCIndex", this.entityData.get(NPC_INDEX));
        // 添加日志记录以确认数据被正确写入
        System.out.println("Saving NPC Index: " + this.entityData.get(NPC_INDEX));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("NPCIndex", 3)) {
            this.entityData.set(NPC_INDEX, tag.getInt("NPCIndex"));
            // 添加日志记录以确认数据被正确读取
            System.out.println("Loaded NPC Index: " + this.entityData.get(NPC_INDEX));
        } else {
            // 如果没有找到预期的数据，记录一个警告
            System.out.println("No NPC Index found in save data.");
        }
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NPC_INDEX, 0);
    }
    // 初始化方法，用于设置索引
    public void initialize(int index) {
        if (!this.entityData.get(NPC_INDEX).equals(index)) {
            this.entityData.set(NPC_INDEX, index);
        }

        NPCModel npc = GameController.getInstance().getNPC(index);
        this.setCustomName(new TextComponent(npc.getNPCName()));
        this.setCustomNameVisible(true);
        this.registerGoals();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new CustomRandomStrollGoal(this, 0.1));
    }

    // 定义实体的基本属性
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)  // 增加健康值，表示更高的耐力
                .add(Attributes.MOVEMENT_SPEED, 0.25);  // 正常移动速度
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        int index = this.entityData.get(NPC_INDEX);

        if (!this.level.isClientSide) {
            System.out.println("Index of server: "+ index);
            NPCModel npc = GameController.getInstance().getNPC(index); // Use the stored index to get the NPC model
            player.displayClientMessage(new TextComponent("Hello, I am " + npc.getNPCName()), false);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        // 客户端逻辑
        try {
            System.out.println("Index of client: "+ index);
            NPCModel npc = GameController.getInstance().getNPC(index);
            Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
            NPCOpenAI.getLogger().info("Interacting with NPC: " + npc.getNPCName());
        } catch (IndexOutOfBoundsException e) {
            NPCOpenAI.getLogger().error("No NPCs available for interaction.");
            return InteractionResult.FAIL;
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }
}