package entity;

import controller.GameController;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

import net.minecraft.network.chat.TextComponent;
import npcopenai.NPCInteractionScreen;
import npcopenai.NPCOpenAI;

public class NPCEntity extends Villager {
    public NPCEntity(EntityType<? extends Mob> type, Level world) {
        super((EntityType<? extends Villager>) type, world);
        this.setCustomName(new TextComponent("librarian")); // 设置自定义名称
        this.setCustomNameVisible(true); // 始终显示名称
        this.registerGoals(); // 初始化 AI 行为

    }

//    public static AttributeSupplier.Builder createAttributes() {
//        return Mob.createMobAttributes()
//                .add(Attributes.MAX_HEALTH, 20.0)
//                .add(Attributes.MOVEMENT_SPEED, 0.25);
//    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new CustomRandomStrollGoal(this, 0.05));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (!this.level.isClientSide) {
            // 服务器端逻辑
            player.displayClientMessage(new TextComponent("Hello, I am the Librarian!"), false);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        // 客户端逻辑
        try {
            NPCModel npc = GameController.getInstance().getNpcs().get(0);
            Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
            NPCOpenAI.getLogger().info("Interacting with NPC: " + npc.getNPCName());
        } catch (IndexOutOfBoundsException e) {
            NPCOpenAI.getLogger().error("No NPCs available for interaction.");
            return InteractionResult.FAIL;
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }
}