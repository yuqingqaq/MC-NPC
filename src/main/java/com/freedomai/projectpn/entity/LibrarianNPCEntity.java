package com.freedomai.projectpn.entity;

import com.freedomai.projectpn.controller.GameController;
import com.freedomai.projectpn.model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.TextComponent;
import com.freedomai.projectpn.NPCInteractionScreen;
import com.freedomai.projectpn.ProjectPhoenixNPC;

public class LibrarianNPCEntity extends Mob {
    public LibrarianNPCEntity(EntityType<? extends Mob> type, Level world) {
        super(type, world);

        this.setCustomName(new TextComponent("Librarian"));
        this.setCustomNameVisible(true); // 名称始终显示
        this.registerGoals(); // 初始化 AI 行为

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
            // 服务器端逻辑
            player.displayClientMessage(new TextComponent("Hello, I am Librarian!"), false);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        // 客户端逻辑
        try {
            NPCModel npc = GameController.getInstance().getNpcs().get(0);
            Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
            ProjectPhoenixNPC.getLogger().info("Interacting with NPC: " + npc.getNPCName());
        } catch (IndexOutOfBoundsException e) {
            ProjectPhoenixNPC.getLogger().error("No NPCs available for interaction.");
            return InteractionResult.FAIL;
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }
}