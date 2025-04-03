package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;

import java.util.ArrayList;
import java.util.List;

public class FeedbackPanel extends GuiComponent {
    private static final int PADDING = 10;
    private static final int CLOSE_BUTTON_SIZE = 20;
    private static final int LINE_HEIGHT = 12;
    private static final int TITLE_HEIGHT = 20;

    private int width;
    private int height;
    private int x;
    private int y;

    private boolean isCorrect;
    private String feedback;
    private List<ColoredText> wrappedLines = new ArrayList<>();

    public FeedbackPanel(int width, int x, int y) {
        this.width = width;
        this.x = x;
        this.y = y;
        // 高度将在setFeedback中计算
    }

    public void setFeedback(boolean isCorrect, String feedback) {
        this.isCorrect = isCorrect;
        this.feedback = feedback;

        // 使用TextUtils处理文本换行和着色
        int maxTextWidth = width - (PADDING * 2);
        wrappedLines = TextUtils.wrapText(feedback, maxTextWidth, false);

        // 计算所需的高度
        this.height = PADDING * 2 + TITLE_HEIGHT + (wrappedLines.size() * LINE_HEIGHT);

        // 确保最小高度
        this.height = Math.max(this.height, PADDING * 2 + TITLE_HEIGHT + LINE_HEIGHT);
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 获取实际屏幕尺寸，确保面板在可视范围内
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        // 调整位置，确保面板完全可见
        int adjustedX = Math.min(x, screenWidth - width);
        adjustedX = Math.max(adjustedX, 0);

        int adjustedY = Math.min(y, screenHeight - height);
        adjustedY = Math.max(adjustedY, 0);

        // 临时存储调整后的位置，以便在isMouseOver等方法中使用
        int renderX = adjustedX;
        int renderY = adjustedY;

        // 绘制背景
        int backgroundColor = isCorrect ? 0xFF4CAF50 : 0xFFF44336; // 绿色或红色
        this.fillGradient(poseStack, renderX, renderY, renderX + width, renderY + height, backgroundColor, backgroundColor);

        // 绘制边框
        this.fillGradient(poseStack, renderX, renderY, renderX + width, renderY + 2, 0xFF000000, 0xFF000000); // 顶部边框
        this.fillGradient(poseStack, renderX, renderY, renderX + 2, renderY + height, 0xFF000000, 0xFF000000); // 左侧边框
        this.fillGradient(poseStack, renderX + width - 2, renderY, renderX + width, renderY + height, 0xFF000000, 0xFF000000); // 右侧边框
        this.fillGradient(poseStack, renderX, renderY + height - 2, renderX + width, renderY + height, 0xFF000000, 0xFF000000); // 底部边框

        // 绘制标题
        Font font = Minecraft.getInstance().font;
        String title = isCorrect ? "正确!" : "再想想...";
        font.draw(poseStack, title, renderX + PADDING, renderY + PADDING, 0xFFFFFFFF);

        // 绘制处理过的彩色文本
        int textY = renderY + PADDING + TITLE_HEIGHT; // 标题下方开始
        for (ColoredText line : wrappedLines) {
            font.draw(poseStack, line.text, renderX + PADDING, textY, line.color);
            textY += LINE_HEIGHT;
        }

        // 绘制关闭按钮
        this.fillGradient(poseStack, renderX + width - CLOSE_BUTTON_SIZE - PADDING, renderY + PADDING,
                renderX + width - PADDING, renderY + PADDING + CLOSE_BUTTON_SIZE, 0xFF333333, 0xFF333333);
        font.draw(poseStack, "X", renderX + width - CLOSE_BUTTON_SIZE/2 - PADDING - 4,
                renderY + PADDING + 6, 0xFFFFFFFF);

        // 更新实际渲染位置
        x = renderX;
        y = renderY;
    }

    // 判断鼠标是否在面板上
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // 判断是否点击了关闭按钮
    public boolean isCloseButtonClicked(double mouseX, double mouseY) {
        return mouseX >= x + width - CLOSE_BUTTON_SIZE - PADDING &&
                mouseX <= x + width - PADDING &&
                mouseY >= y + PADDING &&
                mouseY <= y + PADDING + CLOSE_BUTTON_SIZE;
    }

    // 获取反馈是否为正确
    public boolean isCorrect() {
        return isCorrect;
    }

    // 获取面板高度
    public int getHeight() {
        return height;
    }

    // 获取面板宽度
    public int getWidth() {
        return width;
    }
}