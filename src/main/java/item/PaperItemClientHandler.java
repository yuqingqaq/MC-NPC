package item;

import gui.academic.writing.PaperReviewScreen;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import npcopenai.NPCOpenAI;

import java.util.List;

public class PaperItemClientHandler {
    public static void openPaperReviewScreen(String subTaskTitle, List<String> paperContent, NPCModel npcModel) {
        NPCOpenAI.getLogger().info("Opening PaperReviewScreen with title: " + subTaskTitle);
        Minecraft.getInstance().setScreen(new PaperReviewScreen(subTaskTitle, paperContent, npcModel));
    }
}