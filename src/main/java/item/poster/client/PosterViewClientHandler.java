package item.poster.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class PosterViewClientHandler {
    public static void openPosterScreen(String title, List<String> content,
                                        String imagePath, String expertType) {

        // 防止 null 值
        if (title == null) title = "未知标题";
        if (content == null) content = new ArrayList<>();
        if (imagePath == null) imagePath = "";
        if (expertType == null) expertType = "未知专家";

        // 创建屏幕并显示
        final String finalTitle = title;
        final List<String> finalContent = new ArrayList<>(content); // 创建副本以避免并发修改
        final String finalImagePath = imagePath;
        final String finalExpertType = expertType;

        // 确保在主线程执行
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            PosterViewScreen screen = new PosterViewScreen(
                    new TextComponent("学术海报: " + finalTitle),
                    finalTitle,
                    finalContent,
                    finalImagePath,
                    finalExpertType
            );
            minecraft.setScreen(screen);
        });
    }

    // 显示错误信息屏幕
    public static void openErrorScreen(String posterType) {
        List<String> errorContent = new ArrayList<>();
        errorContent.add("无法加载海报内容");
        errorContent.add("海报类型: " + posterType);
        errorContent.add("请联系模组作者修复此问题");

        openPosterScreen("数据加载错误", errorContent, "", "系统");
    }
}