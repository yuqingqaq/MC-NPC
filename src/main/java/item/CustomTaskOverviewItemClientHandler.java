package item;

import gui.screen.TaskOverviewScreen;
import net.minecraft.client.Minecraft;
import npcopenai.NPCOpenAI;

public class CustomTaskOverviewItemClientHandler {
    public static void openTaskOverviewScreen() {
        NPCOpenAI.getLogger().info("Opening TaskOverviewScreen");
        Minecraft.getInstance().setScreen(new TaskOverviewScreen());
    }
}