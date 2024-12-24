// TimedCustomItemClientHandler.java
package item;

import gui.screen.NPCStageBasedScreen;
import net.minecraft.client.Minecraft;
import gui.screen.NPCTimeSortedScreen;
import npcopenai.NPCOpenAI;

public class TimedCustomItemClientHandler {
    public static void openNPCTimeSortedScreen() {
        try {
            //Minecraft.getInstance().setScreen(new NPCTimeSortedScreen());
            Minecraft.getInstance().setScreen(new NPCStageBasedScreen());
            NPCOpenAI.getLogger().info("Opening NPCStageSortedScreen");
        } catch (Exception e) {
            NPCOpenAI.getLogger().error("Failed to open NPCTimeSortedScreen", e);
        }
    }
}