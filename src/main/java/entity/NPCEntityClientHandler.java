package entity;

import gui.screen.NPCDetailScreen;
import gui.screen.NPCInteractionScreen;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import npcopenai.NPCOpenAI;

public class NPCEntityClientHandler {

    public static void handleInteraction(Player player, NPCModel npc) {
        interactWithNPC(player, npc);
    }

    public static void interactWithNPC(Player player, NPCModel npc) {
        try {
            // 显示交互界面
            Minecraft.getInstance().setScreen(new NPCDetailScreen(npc));
            NPCOpenAI.getLogger().info("Interacting with NPC: " + npc.getNPCName());
        } catch (Exception e) {
            NPCOpenAI.getLogger().error("Failed to interact with NPC: " + npc.getNPCName(), e);
        }
    }
}