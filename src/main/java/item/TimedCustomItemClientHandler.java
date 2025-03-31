// TimedCustomItemClientHandler.java
package item;

import gui.screen.NPCStageBasedScreen;
import gui.screen.GlobalGuideBookScreen;
import net.minecraft.client.Minecraft;
import gui.screen.NPCTimeSortedScreen;
import npcopenai.NPCOpenAI;

public class TimedCustomItemClientHandler {
    public static void openNPCTimeSortedScreen() {
        try {
            Minecraft.getInstance().setScreen(new GlobalGuideBookScreen());
            NPCOpenAI.getLogger().info("Opening GlobalGuideBookScreen");
        } catch (Exception e) {
            NPCOpenAI.getLogger().error("Failed to open GlobalGuideBookScreen", e);
        }
    }
}