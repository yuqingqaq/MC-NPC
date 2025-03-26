package item.knowledge;

// 相应的客户端处理类

import gui.academic.knowledge.KnowledgeGraphScreen;
import net.minecraft.client.Minecraft;

public class KnowledgeJournalClientHandler {
    public static void openKnowledgeGraphScreen() {
        Minecraft.getInstance().setScreen(new KnowledgeGraphScreen());
    }
}
