package item.paper;

import gui.academic.writing.PaperReviewScreen;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import gui.academic.writing.PaperReviewScreen;
import java.util.List;

public class PaperItemClientHandler {

    public static void openPaperReviewScreen(String title, List<String> content, NPCModel npcModel) {
        // 为了向后兼容，调用新的方法并提供默认值
        openPaperReviewScreen(title, content, npcModel, "", "");
    }

    public static void openPaperReviewScreen(String title, List<String> content,
                                             NPCModel npcModel, String paperTitle,
                                             String paperAuthors) {
        PaperReviewScreen screen = new PaperReviewScreen(
                title,
                content,
                npcModel
        );
        Minecraft.getInstance().setScreen(screen);
    }
}