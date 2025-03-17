package component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.ArrayList;
import java.util.List;

public class ContentPanel extends ScrollPanel {
    private List<String> content; // 原始内容
    private boolean needsRefresh = false;
    private final Font font;
    private List<ColoredText> wrappedLines = new ArrayList<>(); // 包装后的内容
    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置

    public ContentPanel(Minecraft mc, int width, int height, int top, int left, int border, int barWidth) {
        super(mc, width, height, top, left, border, barWidth, 0, 0, 0x00000000, 0x00000000, 0x00000000);
        this.font = mc.font; // 获取 Minecraft 的字体渲染器实例
        this.content = new ArrayList<>();
    }

    public void setContent(List<String> content) {
        this.content = content != null ? content : new ArrayList<>();
        refreshPanel();
    }

    public void refreshPanel() {
        needsRefresh = true;
        updateWrappedLines(this.width - 10); // 假设左右留 5px 边距
        maxScrollY = Math.max(0, getContentHeight() - this.height); // 更新最大滚动位置
        scrollY = Math.min(scrollY, maxScrollY); // 保证滚动位置不超出最大值
    }

    private void updateWrappedLines(int maxWidth) {
        wrappedLines.clear();
        for (String line : content) {
            List<ColoredText> wrapped = TextUtils.wrapText(line, maxWidth, true);
            wrappedLines.addAll(wrapped); // 使用工具方法进行换行处理
        }
    }

    @Override
    protected int getContentHeight() {
        // 包装后的内容行数决定了面板的总高度
        return this.wrappedLines.size() * this.font.lineHeight + 5;
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleAreaHeight) {
        int yPos = this.top + 5 - this.scrollY; // 使用成员变量 scrollY，而不是参数 scrollY
        for (ColoredText line : wrappedLines) {
            if (yPos + this.font.lineHeight > this.top && yPos < this.top + this.height) {
                this.font.draw(poseStack, line.text, this.left + 5, yPos, line.color); // 绘制带颜色的文本
            }
            yPos += this.font.lineHeight;
        }

        // 绘制滚动条
        drawScrollbar(poseStack);
    }

    private void drawScrollbar(PoseStack poseStack) {
        if (getContentHeight() <= this.height) {
            return; // 如果内容高度小于面板高度，则不需要滚动条
        }

        int scrollbarHeight = Math.max(10, (int) ((float) this.height * (this.height / (float) getContentHeight())));
        int scrollbarTop = this.top + (int) ((float) scrollY / maxScrollY * (this.height - scrollbarHeight));
        int scrollbarRight = this.left + this.width + 5; // 滚动条宽度为5像素
        int scrollbarLeft = scrollbarRight - 5;

        fill(poseStack, scrollbarLeft, scrollbarTop, scrollbarRight, scrollbarTop + scrollbarHeight, 0xFFAAAAAA); // 灰色滚动条
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int delta = (int) (-scroll * getScrollAmount()); // 这里将滚动方向翻转
        if (scrollY + delta >= 0 && scrollY + delta <= maxScrollY) {
            scrollY += delta;
        } else if (scrollY + delta < 0) {
            scrollY = 0;
        } else if (scrollY + delta > maxScrollY) {
            scrollY = maxScrollY;
        }
        return true; // 表示事件已处理
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