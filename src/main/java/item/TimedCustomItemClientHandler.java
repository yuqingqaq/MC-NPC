// TimedCustomItemClientHandler.java
package item;

import net.minecraft.client.Minecraft;
import gui.screen.NPCTimeSortedScreen;
import npcopenai.NPCOpenAI;

public class TimedCustomItemClientHandler {
    public static void openNPCTimeSortedScreen() {
        try {
            Minecraft.getInstance().setScreen(new NPCTimeSortedScreen());
            NPCOpenAI.getLogger().info("Opening NPCTimeSortedScreen");
        } catch (Exception e) {
            NPCOpenAI.getLogger().error("Failed to open NPCTimeSortedScreen", e);
        }
    }
}