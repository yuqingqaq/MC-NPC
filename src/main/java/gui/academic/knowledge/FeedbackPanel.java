package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;

import java.util.ArrayList;
import java.util.List;

public class FeedbackPanel extends GuiComponent { // 继承GuiComponent以使用其方法
    private static final int PADDING = 10;
    private static final int CLOSE_BUTTON_SIZE = 20;
    private static final int LINE_HEIGHT = 12;

    private final int width;
    private final int height;
    private int x;
    private int y;

    private boolean isCorrect;
    private String feedback;
    private List<ColoredText> wrappedLines = new ArrayList<>(); // 存储处理后的彩色文本

    public FeedbackPanel(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public void setFeedback(boolean isCorrect, String feedback) {
        this.isCorrect = isCorrect;
        this.feedback = feedback;

        // 使用TextUtils处理文本换行和着色
        int maxTextWidth = width - (PADDING * 2);
        wrappedLines = TextUtils.wrapText(feedback, maxTextWidth, false); // feedback使用NPC颜色

        // 限制最大行数
        int maxLines = (height - PADDING * 2 - 20) / LINE_HEIGHT; // 减去标题空间
        if (wrappedLines.size() > maxLines) {
            List<ColoredText> truncatedLines = wrappedLines.subList(0, maxLines - 1);
            truncatedLines.add(new ColoredText("... (content truncated)", 0xFFAAAA));
            wrappedLines = new ArrayList<>(truncatedLines);
        }
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 获取实际屏幕尺寸，确保面板在可视范围内
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        // 调整位置，确保面板完全可见
        x = Math.min(x, screenWidth - width);
        y = Math.min(y, screenHeight - height);
        x = Math.max(x, 0);
        y = Math.max(y, 0);

        // 绘制背景 - 使用继承的fillGradient方法
        int backgroundColor = isCorrect ? 0xFF4CAF50 : 0xFFF44336; // 绿色或红色
        this.fillGradient(poseStack, x, y, x + width, y + height, backgroundColor, backgroundColor);

        // 绘制边框
        this.fillGradient(poseStack, x, y, x + width, y + 2, 0xFF000000, 0xFF000000); // 顶部边框
        this.fillGradient(poseStack, x, y, x + 2, y + height, 0xFF000000, 0xFF000000); // 左侧边框
        this.fillGradient(poseStack, x + width - 2, y, x + width, y + height, 0xFF000000, 0xFF000000); // 右侧边框
        this.fillGradient(poseStack, x, y + height - 2, x + width, y + height, 0xFF000000, 0xFF000000); // 底部边框

        // 绘制标题
        Font font = Minecraft.getInstance().font;
        String title = isCorrect ? "Correct!" : "Not quite right...";
        font.draw(poseStack, title, x + PADDING, y + PADDING, 0xFFFFFFFF);

        // 绘制处理过的彩色文本
        int textY = y + PADDING + 20; // 标题下方开始
        for (ColoredText line : wrappedLines) {
            // 确保文本在可视范围内
            if (textY + LINE_HEIGHT > y + height - PADDING) {
                break;
            }
            font.draw(poseStack, line.text, x + PADDING, textY, line.color);
            textY += LINE_HEIGHT;
        }

        // 绘制关闭按钮
        this.fillGradient(poseStack, x + width - CLOSE_BUTTON_SIZE - PADDING, y + PADDING,
                x + width - PADDING, y + PADDING + CLOSE_BUTTON_SIZE, 0xFF333333, 0xFF333333);
        font.draw(poseStack, "X", x + width - CLOSE_BUTTON_SIZE/2 - PADDING - 4,
                y + PADDING + 6, 0xFFFFFFFF);
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
}