package entity;

import controller.GameController;
import model.NPCModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import npcopenai.NPCOpenAI;

public class LibrarianNPCEntity extends Mob {
    private static final EntityDataAccessor<Integer> NPC_INDEX = SynchedEntityData.defineId(LibrarianNPCEntity.class, EntityDataSerializers.INT);
    private NPCModel npc;
    public LibrarianNPCEntity(EntityType<? extends Mob> type, Level world) {
        super(type, world);
    }
    private String currentSpeechText = "同学们好，欢迎你们来到香港中文大学（深圳）!";
    private boolean isTalking = true;

    public void startTalking(String text) {
        this.currentSpeechText = text;
        this.isTalking = true;
    }

    public void stopTalking() {
        this.isTalking = false;
    }

    public String getCurrentSpeechText() {
        return this.currentSpeechText;
    }

    public boolean isTalking() {
        return this.isTalking;
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
        this.npc = GameController.getInstance().getNPC(this.entityData.get(NPC_INDEX));
        this.setCustomName(new TextComponent(npc.getNPCName()));
        this.setCustomNameVisible(true);
        this.currentSpeechText = this.npc.getDialogues().get(0);
        this.registerGoals();

    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        int index = this.entityData.get(NPC_INDEX);

        if (!this.level.isClientSide) {
            // 服务端逻辑：直接发送消息给玩家
            NPCModel npc = GameController.getInstance().getNPC(index);
            if (npc != null) {
                player.displayClientMessage(new TextComponent("你好，我是" + npc.getNPCName()), false);
            } else {
                player.displayClientMessage(new TextComponent("无法找到 NPC 数据。"), false);
            }
            return InteractionResult.sidedSuccess(false);
        }

        // 客户端逻辑
        return DistExecutor.unsafeRunForDist(() -> () -> {
            NPCModel npc = GameController.getInstance().getNPC(index);
            if (npc != null) {
                NPCEntityClientHandler.handleInteraction(player, npc);
            }
            return InteractionResult.sidedSuccess(true);
        }, () -> () -> InteractionResult.PASS);

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

}