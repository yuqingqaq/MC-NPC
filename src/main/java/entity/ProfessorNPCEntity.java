package entity;

import controller.GameController;
import model.NPCModel;
import net.minecraft.client.Minecraft;
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
    private int npcIndex; // Store the index of the NPC
    private NPCModel npc;
    public ProfessorNPCEntity(EntityType<? extends Mob> type, Level world) {
        super(type, world);
    }

    // 初始化方法，用于设置索引
    public void initialize(int index) {
        this.npcIndex = index;
        NPCModel npc = GameController.getInstance().getNPC(this.npcIndex);
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

        if (!this.level.isClientSide) {
            NPCModel npc = GameController.getInstance().getNPC(this.npcIndex); // Use the stored index to get the NPC model
            player.displayClientMessage(new TextComponent("Hello, I am " + npc.getNPCName()), false);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        // 客户端逻辑
        try {
            NPCModel npc = GameController.getInstance().getNPC(this.npcIndex);
            Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
            NPCOpenAI.getLogger().info("Interacting with NPC: " + npc.getNPCName());
        } catch (IndexOutOfBoundsException e) {
            NPCOpenAI.getLogger().error("No NPCs available for interaction.");
            return InteractionResult.FAIL;
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }
}