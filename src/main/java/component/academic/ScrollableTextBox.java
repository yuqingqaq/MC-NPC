package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import component.ColoredText;
import component.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.ArrayList;
import java.util.List;

public class ScrollableTextBox extends ScrollPanel {
    private final Font font; // 字体渲染器
    private List<ColoredText> lines; // 处理后的多行文本
    private String content; // 原始文本内容
    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置

    public ScrollableTextBox(Minecraft mc, int x, int y, int width, int height) {
        super(mc, width, height, y, x); // 黑色边框，白色背景
        this.font = mc.font;
        this.lines = new ArrayList<>();
        this.content = ""; // 初始内容为空
    }

    /**
     * 设置文本内容，并处理换行
     */
    public void setText(String text) {
        this.content = text;
        refreshContent();
    }

    /**
     * 刷新文本内容，将原始内容处理为多行
     */
    private void refreshContent() {
        lines.clear();
        if (content != null && !content.isEmpty()) {
            // 使用 TextUtils.wrapText 进行换行处理，默认白色
            lines = TextUtils.wrapText(content, this.width - 5, true); // 传入内容、最大宽度、是否白色
        }
        maxScrollY = Math.max(0, getContentHeight() - height); // 更新最大滚动范围
        scrollY = Math.min(scrollY, maxScrollY); // 确保滚动位置不超出范围
    }

    /**
     * 获取内容高度
     */
    @Override
    protected int getContentHeight() {
        return lines.size() * 12; // 每行占用 12 像素高度
    }

    /**
     * 绘制面板内容
     */
    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        int yPos = top + 5 - this.scrollY; // 计算起始绘制位置
        for (ColoredText line : lines) {
            if (yPos + 12 > top && yPos < top + height) { // 仅绘制可见部分
                font.draw(poseStack, line.text, left+ 5, yPos, line.color); // 黑色字体
            }
            yPos += 12; // 每行高度
        }
    }

    /**
     * 处理鼠标滚动
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int delta = (int) (scrollAmount * getScrollAmount());
        scrollY = Math.max(0, Math.min(maxScrollY, scrollY - delta)); // 更新滚动位置
        return true; // 表示事件已处理
    }

    /**
     * 滚动条的滚动速度
     */
    @Override
    protected int getScrollAmount() {
        return 12; // 每次滚动 12 像素
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // 实现辅助功能描述
    }
}