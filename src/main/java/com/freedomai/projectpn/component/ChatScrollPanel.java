package com.freedomai.projectpn.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.ArrayList;
import java.util.List;

import static io.netty.util.ResourceLeakDetector.isEnabled;

public class ChatScrollPanel extends ScrollPanel {
    private List<String> chatHistory;
    private boolean needsRefresh = false;
    private final Font font;
    private List<String> wrappedChatLines = new ArrayList<>();
    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置

    public ChatScrollPanel(Minecraft mc, int width, int height, int top, int left, int border, int barWidth, List<String> chatHistory) {
        super(mc, width, height, top, left, border, barWidth);
        this.chatHistory = chatHistory;
        this.font = mc.font;  // 获取 Minecraft 的字体渲染器实例
    }

    public void refreshPanel() {
        needsRefresh = true;
        updateWrappedChatLines(this.width);  // 假设边距为10，总共20
        maxScrollY = Math.max(0, getContentHeight() - height); // 更新最大滚动位置
        scrollY = Math.min(scrollY, maxScrollY); // 保证滚动位置不超出最大值
        System.out.println("Height: " + height +" Panel refreshed, maxScrollY set to " + maxScrollY);

    }

    public void updateWrappedChatLines(int maxWidth) {
        wrappedChatLines.clear(); // 清空之前的换行结果
        for (String chatLine : chatHistory) {
            wrapTextToFitWidth(chatLine, maxWidth);
        }
    }

    private void wrapTextToFitWidth(String text, int maxWidth) {
        StringBuilder currentLine = new StringBuilder(); // 当前行的文本
        int currentLineWidth = 0; // 当前行的宽度

        int widthPerEnglishChar = 6; // 英文字符的宽度
        int widthPerChineseChar = 11; // 中文字符的宽度

        for (char ch : text.toCharArray()) {
            int charWidth = (isChinese(ch) ? widthPerChineseChar : widthPerEnglishChar);

            // 检查是否需要换行
            if (currentLineWidth + charWidth > maxWidth) {
                // 检查能否在较早位置断行
                int lastSpace = currentLine.lastIndexOf(" ");
                if (lastSpace != -1 && !isChinese(ch)) {
                    String lineToAdd = currentLine.substring(0, lastSpace);
                    wrappedChatLines.add(lineToAdd);
                    currentLine = new StringBuilder(currentLine.substring(lastSpace + 1)); // 创建新行
                } else {
                    wrappedChatLines.add(currentLine.toString());
                    currentLine = new StringBuilder(); // 创建新行
                }
                currentLineWidth = 0; // 重置行宽
            }

            // 添加字符到当前行
            currentLine.append(ch);
            currentLineWidth += charWidth;
        }

        // 添加最后一行（如果有的话）
        if (currentLine.length() > 0) {
            wrappedChatLines.add(currentLine.toString());
        }
    }

    // 辅助方法：判断字符是否为中文
    private boolean isChinese(char ch) {
        return (ch >= '\u4E00' && ch <= '\u9FFF') || (ch >= '\u3400' && ch <= '\u4DBF') ||
                (ch >= '\uF900' && ch <= '\uFAFF') ;
    }
    
    @Override
    protected int getContentHeight() {
        // 根据聊天历史计算内容的总高度，每行10像素
        int contentHeight = wrappedChatLines.size() * 12;
        return contentHeight;

    }


    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        //System.out.println("Drawing panel with scrollY: " + scrollY + ", Visible Height: " + visibleHeight);

        if (needsRefresh) {
            needsRefresh = false;
            System.out.println("Panel refreshed due to content change or resizing.");
        }
        scrollY = this.scrollY;
        int yPos = top + 5 - scrollY;  // 起始绘制的y位置，随滚动条滚动调整
        for (String chatLine : wrappedChatLines) {
            if (yPos + 10 > top && yPos < top + visibleHeight) { // 仅绘制当前可见部分
                drawString(poseStack, this.font, chatLine, left + 10, yPos, 0xFFFFFF);
                //System.out.println("Drawing chat line at yPos: " + yPos + " | Content: " + chatLine);
            }
            yPos += 12; // 更新y位置到下一行
        }
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // 实现辅助功能描述
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int delta = (int) (scroll * getScrollAmount()); // 这里的 getScrollAmount() 可以调整为你希望的滚动增量
        System.out.println("Mouse scrolled: " + scroll + ", calculated delta: " + delta);
        if (scrollY + delta >= 0 && scrollY + delta <= maxScrollY) {
            scrollY += delta;
        } else if (scrollY + delta < 0) {
            scrollY = 0;
        } else if (scrollY + delta > maxScrollY) {
            scrollY = maxScrollY;
        }
        System.out.println("ScrollY updated to " + scrollY);
        return true; // 表示事件已处理
    }

}