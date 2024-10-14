package component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import metadata.NPCMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.ArrayList;
import java.util.List;

public class ChatScrollPanel extends ScrollPanel {
    private List<NPCMessage> chatHistory;
    private boolean needsRefresh = false;
    private final Font font;
    private List<ColoredText> wrappedChatLines = new ArrayList<>();
    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置

    // 辅助类，用于存储文本及其颜色
    private static class ColoredText {
        String text;
        int color;

        ColoredText(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }
    public ChatScrollPanel(Minecraft mc, int width, int height, int top, int left, int border, int barWidth, List<NPCMessage> chatHistory) {
        super(mc, width, height, top, left, border, barWidth, 0, 0, 0x00000000, 0x00000000, 0x00000000);
        this.chatHistory = chatHistory;
        this.font = mc.font;  // 获取 Minecraft 的字体渲染器实例
    }

    public void refreshPanel() {
        needsRefresh = true;
        updateWrappedChatLines(this.width);  // 假设边距为10，总共20
        maxScrollY = Math.max(0, getContentHeight() - height); // 更新最大滚动位置
        scrollY = Math.min(scrollY, maxScrollY); // 保证滚动位置不超出最大值

    }

    public void updateWrappedChatLines(int maxWidth) {
        wrappedChatLines.clear();
        for (NPCMessage chatLine : chatHistory) {
            wrapTextToFitWidth(chatLine, maxWidth);
        }
    }
    private void wrapTextToFitWidth(NPCMessage message, int maxWidth) {
        StringBuilder currentLine = new StringBuilder();
        int currentLineWidth = 0;

        int widthPerEnglishChar = 6;
        int widthPerChineseChar = 11;

        String sender = message.getSender();
        String prefix = sender.equals("user") ? "You: " : sender + ": ";  // 根据发送者添加前缀
        String text = prefix + message.getContent();
        int textColor = message.getSender().equals("user") ? 0xFFFFFFFF : 0xFFFFAA00; // 黄色或白色

        for (char ch : text.toCharArray()) {
            int charWidth = (isChinese(ch) ? widthPerChineseChar : widthPerEnglishChar);

            if (currentLineWidth + charWidth > maxWidth) {
                int lastSpace = currentLine.lastIndexOf(" ");
                if (lastSpace != -1 && !isChinese(ch)) {
                    String lineToAdd = currentLine.substring(0, lastSpace);
                    wrappedChatLines.add(new ColoredText(lineToAdd, textColor));
                    currentLine = new StringBuilder(currentLine.substring(lastSpace + 1));
                } else {
                    wrappedChatLines.add(new ColoredText(currentLine.toString(), textColor));
                    currentLine = new StringBuilder();
                }
                currentLineWidth = 0;
            }

            currentLine.append(ch);
            currentLineWidth += charWidth;
        }

        if (currentLine.length() > 0) {
            wrappedChatLines.add(new ColoredText(currentLine.toString(), textColor));
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
        if (needsRefresh) {
            needsRefresh = false;
        }
        scrollY = this.scrollY;
        int yPos = top + 5 - scrollY;

        for (ColoredText line : wrappedChatLines) {
//            if (yPos + 10 > top && yPos < top + visibleHeight) {
                drawString(poseStack, this.font, line.text, left + 10, yPos, line.color);
//            }
            yPos += 12;
        }

        drawScrollbar(poseStack);
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // 实现辅助功能描述
    }

    private void drawScrollbar(PoseStack poseStack) {
        int scrollbarHeight = Math.max(10, (int) ((float) height * (height / (float) getContentHeight())));
        int scrollbarTop = top + (int) ((float) scrollY / maxScrollY * (height - scrollbarHeight));
        int scrollbarRight = left + width + 10; // Assuming the scrollbar is 5 pixels wide
        int scrollbarLeft = scrollbarRight - 5;

        fill(poseStack, scrollbarLeft, scrollbarTop, scrollbarRight, scrollbarTop + scrollbarHeight, 0xFFAAAAAA); // Grey scrollbar
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int delta = (int) (scroll * getScrollAmount());
        if (scrollY + delta >= 0 && scrollY + delta <= maxScrollY) {
            scrollY += delta;
        } else if (scrollY + delta < 0) {
            scrollY = 0;
        } else if (scrollY + delta > maxScrollY) {
            scrollY = maxScrollY;
        }
        return true; // 表示事件已处理
    }

}