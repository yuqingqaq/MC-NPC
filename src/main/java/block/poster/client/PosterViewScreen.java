package block.poster.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import npcopenai.NPCOpenAI;

import java.util.ArrayList;
import java.util.List;

public class PosterViewScreen extends Screen {
    private final String title;
    private final List<String> content;
    private final String imagePath;
    private final String expertType;

    // 界面元素布局
    private int leftPos;
    private int topPos;
    private static final int WIDTH = 256;
    private static final int HEIGHT = 166;

    // 背景纹理
    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation(NPCOpenAI.MODID, "textures/gui/poster_background.png");

    public PosterViewScreen(Component screenTitle, String posterTitle,
                            List<String> content, String imagePath, String expertType) {
        super(screenTitle);

        // 防止null值
        this.title = posterTitle != null ? posterTitle : "未知标题";

        // 如果内容为空，使用临时测试内容
        if (content == null || content.isEmpty()) {
            List<String> testContent = new ArrayList<>();
            testContent.add("无法加载海报内容");
            testContent.add("请尝试重新放置海报");
            this.content = testContent;
            System.out.println("Using default content for empty poster");
        } else {
            this.content = content;
        }

        this.imagePath = imagePath != null ? imagePath : "";
        this.expertType = expertType != null ? expertType : "未知专家";

        // 添加调试输出
        System.out.println("PosterViewScreen constructed with:");
        System.out.println("  Title: " + this.title);
        System.out.println("  Content size: " + this.content.size());
        if (!this.content.isEmpty()) {
            System.out.println("  First content line: " + this.content.get(0));
        }
        System.out.println("  Image path: " + this.imagePath);
        System.out.println("  Expert type: " + this.expertType);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;

        // 添加关闭按钮
        this.addRenderableWidget(new Button(
                this.leftPos + WIDTH / 2 - 30,
                this.topPos + HEIGHT - 25,
                60, 20,
                new TextComponent("关闭"),
                button -> this.onClose()
        ));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);

        // 绘制背景（白色矩形）
        fillGradient(poseStack, leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT,
                0xFFFFFFFF, 0xFFEEEEEE);

        // 绘制边框
        fill(poseStack, leftPos, topPos, leftPos + WIDTH, topPos + 1, 0xFF000000);
        fill(poseStack, leftPos, topPos + HEIGHT - 1, leftPos + WIDTH, topPos + HEIGHT, 0xFF000000);
        fill(poseStack, leftPos, topPos, leftPos + 1, topPos + HEIGHT, 0xFF000000);
        fill(poseStack, leftPos + WIDTH - 1, topPos, leftPos + WIDTH, topPos + HEIGHT, 0xFF000000);

        // 绘制标题
        drawCenteredString(poseStack, font, title, leftPos + WIDTH / 2, topPos + 10, 0xFF000000);

        // 尝试绘制图像（如果加载失败则跳过）
        try {
            if (!imagePath.isEmpty()) {
                // 确保路径格式正确
                ResourceLocation imageResource;
                if (imagePath.contains(":")) {
                    // 已经是完整的资源路径
                    imageResource = new ResourceLocation(imagePath);
                } else {
                    // 添加默认前缀
                    imageResource = new ResourceLocation(NPCOpenAI.MODID, imagePath);
                }

                //System.out.println("Loading image resource: " + imageResource);

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, imageResource);

                int imageWidth = 120;
                int imageHeight = 60;
                int imageX = leftPos + (WIDTH - imageWidth) / 2;
                int imageY = topPos + 25;

                // 使用blit渲染图像
                blit(poseStack, imageX, imageY, 0, 0, imageWidth, imageHeight,
                        imageWidth, imageHeight);

                //System.out.println("Rendered image: " + imagePath);
            } else {
                //System.out.println("No image path provided or empty path");
                drawCenteredString(poseStack, font, "无图像",
                        leftPos + WIDTH / 2, topPos + 50, 0xFF666666);
            }
        } catch (Exception e) {
            // 图像加载失败，记录日志但继续渲染其他内容
            //System.out.println("Failed to render poster image: " + imagePath);
            e.printStackTrace();

            // 显示错误信息
            drawCenteredString(poseStack, font, "图像加载失败",
                    leftPos + WIDTH / 2, topPos + 50, 0xFFFF0000);
        }

        // 绘制内容
        int textStartY = topPos + 90;
        int textColor = 0xFF000000;

        for (int i = 0; i < content.size() && i < 4; i++) {
            String line = content.get(i);
            // 如果文本过长，截断并添加...
            String renderText = font.plainSubstrByWidth(line, WIDTH - 20);
            if (!renderText.equals(line)) {
                renderText = renderText + "...";
            }

            drawString(poseStack, font, renderText, leftPos + 10, textStartY + i * 12, textColor);
            //System.out.println("Rendered line " + i + ": " + renderText);
        }

        // 绘制专家信息
        String expertText = "专家: " + expertType;
        drawString(poseStack, font, expertText,
                leftPos + WIDTH - 10 - font.width(expertText),
                topPos + HEIGHT - 35, 0xFF666666);

        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}