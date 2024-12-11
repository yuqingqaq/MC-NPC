// CustomItemClientHandler.java
package item;

import gui.screen.NPCTaskScreen;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import controller.GameController;
import npcopenai.NPCOpenAI;

public class CustomItemClientHandler {
    public static void openNPCTaskScreen() {
        if (GameController.getInstance().getNpcs().size() > 0) {
            NPCModel npc = GameController.getInstance().getNpcs().get(0);
            NPCOpenAI.getLogger().info("Opening NPCTaskScreen for NPC. First NPC: " + npc.getNPCName());
            Minecraft.getInstance().setScreen(new NPCTaskScreen());
        } else {
            NPCOpenAI.getLogger().info("No NPCs available or this is a local server");
        }
    }
}