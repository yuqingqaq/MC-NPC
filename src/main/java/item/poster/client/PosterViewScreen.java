package item.poster.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.academic.ScrollableTextBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import npcopenai.NPCOpenAI;
import system.UIScreenManager;

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
    private static final int WIDTH = 320; // 增加总宽度
    private static final int HEIGHT = 200; // 增加总高度

    // 滚动文本框
    private ScrollableTextBox scrollableTextBox;

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
    }

    @Override
    protected void init() {
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);
        this.leftPos = (this.width - WIDTH) / 2;
        this.topPos = (this.height - HEIGHT) / 2;

        // 计算左侧和右侧区域
        int leftSectionWidth = WIDTH / 3; // 左侧1/3宽度
        int rightSectionWidth = WIDTH - leftSectionWidth; // 右侧宽度

        // 创建滚动文本框
        this.scrollableTextBox = new ScrollableTextBox(
                minecraft,
                leftPos + leftSectionWidth, // x坐标从左侧区域右边开始
                topPos + 30, // 留出顶部空间给标题
                rightSectionWidth - 10, // 右侧区域宽度减去边距
                HEIGHT - 60 // 高度减去顶部和底部空间
        );

        // 设置文本内容
        StringBuilder contentBuilder = new StringBuilder();
        for (String line : this.content) {
            contentBuilder.append(line).append("\n");
        }
        scrollableTextBox.setText(contentBuilder.toString());

        // 将滚动文本框添加到渲染列表
        this.addRenderableWidget(scrollableTextBox);

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

        // 计算左侧和右侧区域
        int leftSectionWidth = WIDTH / 3;

        // 绘制背景（白色矩形）
        fillGradient(poseStack, leftPos, topPos, leftPos + WIDTH, topPos + HEIGHT,
                0xFFFFFFFF, 0xFFEEEEEE);

        // 绘制边框
        fill(poseStack, leftPos, topPos, leftPos + WIDTH, topPos + 1, 0xFF000000);
        fill(poseStack, leftPos, topPos + HEIGHT - 1, leftPos + WIDTH, topPos + HEIGHT, 0xFF000000);
        fill(poseStack, leftPos, topPos, leftPos + 1, topPos + HEIGHT, 0xFF000000);
        fill(poseStack, leftPos + WIDTH - 1, topPos, leftPos + WIDTH, topPos + HEIGHT, 0xFF000000);

        // 分隔线
        fill(poseStack, leftPos + leftSectionWidth, topPos, leftPos + leftSectionWidth + 1, topPos + HEIGHT, 0xFFCCCCCC);

        // 绘制标题
        drawCenteredString(poseStack, font, title, leftPos + WIDTH / 2, topPos + 10, 0xFF000000);

        // 绘制图像（在左侧区域）
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

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, imageResource);

                // 图像尺寸适应左侧区域
                int imageWidth = leftSectionWidth - 20; // 左右边距
                int imageHeight = imageWidth * 3 / 4; // 保持一定的宽高比
                int imageX = leftPos + 10; // 左边距
                int imageY = topPos + 40; // 顶部边距

                // 使用blit渲染图像
                blit(poseStack, imageX, imageY, 0, 0, imageWidth, imageHeight,
                        imageWidth, imageHeight);
            } else {
                drawCenteredString(poseStack, font, "无图像",
                        leftPos + leftSectionWidth / 2, topPos + 80, 0xFF666666);
            }
        } catch (Exception e) {
            e.printStackTrace();
            drawCenteredString(poseStack, font, "图像加载失败",
                    leftPos + leftSectionWidth / 2, topPos + 80, 0xFFFF0000);
        }

        // 绘制专家信息（在左侧底部）
        String expertText = "专家: " + expertType;
        drawCenteredString(poseStack, font, expertText,
                leftPos + leftSectionWidth / 2,
                topPos + HEIGHT - 35, 0xFF666666);

        // 渲染所有组件（包括滚动文本框）
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        super.onClose();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}