package block.poster.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class PosterViewClientHandler {
    public static void openPosterScreen(String title, List<String> content, 
                                      String imagePath, String expertType) {
        Minecraft.getInstance().setScreen(
            new PosterViewScreen(
                new TextComponent("学术海报: " + title),
                title,
                content,
                imagePath,
                expertType
            )
        );
    }
}