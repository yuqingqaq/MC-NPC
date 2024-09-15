package com.freedomai.projectpn;

import com.freedomai.projectpn.controller.GameController;
import com.freedomai.projectpn.model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public class CustomItem extends Item {
    public CustomItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        ProjectPhoenixNPC.getLogger().info("projectpn.CustomItem used on: " + context.getClickedPos());

        if (world.isClientSide) {
            ProjectPhoenixNPC.getLogger().info("Executing on client side");
            if (context.getPlayer() != null) {
                NPCModel npc = GameController.getInstance().getNpcs().get(0); // Fetch the first NPC for demonstration
                ProjectPhoenixNPC.getLogger().info("projectpn.CustomItem used on: " + npc.getNPCName());
                Minecraft.getInstance().setScreen(new NPCInteractionScreen(npc));
                return InteractionResult.SUCCESS;
            } else {
                ProjectPhoenixNPC.getLogger().info("No player found");
            }
        } else {
            ProjectPhoenixNPC.getLogger().info("This action is server-side, passing");
        }
        return InteractionResult.PASS;
    }
}