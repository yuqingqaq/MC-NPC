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
        this.title = posterTitle;
        this.content = content;
        this.imagePath = imagePath;
        this.expertType = expertType;
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
                ResourceLocation imageResource = new ResourceLocation(imagePath);
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
            }
        } catch (Exception e) {
            // 图像加载失败，记录日志但继续渲染其他内容
           System.out.println("Failed to render poster image: " + imagePath + e);
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