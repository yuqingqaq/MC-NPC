package component;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextEditorWidget extends AbstractWidget {
    public static final int FONT_HEIGHT = 9; // 每行的高度
    public static final int MARGIN = 4;     // 编辑器的边距

    private final List<String> lines; // 文本内容按行存储
    private int cursorX;              // 光标的列索引
    private int cursorY;              // 光标的行索引
    private final int maxWidth;       // 编辑器的最大宽度（像素）
    private final int maxHeight;      // 编辑器的最大高度（行数）

    public TextEditorWidget(int x, int y, int width, int height) {
        super(x, y, width, height, new TextComponent("Text Editor"));
        this.lines = new ArrayList<>();
        this.lines.add(""); // 初始化一行
        this.cursorX = 0;
        this.cursorY = 0;
        this.maxWidth = width - MARGIN * 2;
        this.maxHeight = height / FONT_HEIGHT;
    }

    public String getText() {
        return String.join("\n", lines);
    }
    /**
     * 设置编辑器的文本内容
     * @param text 文本内容，可以包含换行符
     */
    public void setText(String text) {
        if (text == null) {
            text = ""; // 防止空指针异常
        }

        // 将文本拆分为行
        String[] textLines = text.split("\n");

        // 清空当前行
        lines.clear();

        // 添加每一行文本
        lines.addAll(Arrays.asList(textLines));

        // 如果没有行，添加一个空行
        if (lines.isEmpty()) {
            lines.add("");
        }

        // 重置光标位置到开头
        cursorX = 0;
        cursorY = 0;

        // 执行自动换行处理
        wrapLines();
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        if (Character.isDefined(ch) && ch != '\t' && ch != '\n') {
            insertText(String.valueOf(ch));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER -> {
                insertNewLine();
                return true;
            }
            case GLFW.GLFW_KEY_BACKSPACE -> {
                deleteCharacter();
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                moveCursorLeft();
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                moveCursorRight();
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                moveCursorUp();
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                moveCursorDown();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // 确保 `lines` 至少有一行
        if (lines.isEmpty()) {
            lines.add("");
        }

        // 限制光标位置合法
        cursorY = Math.max(0, Math.min(cursorY, lines.size() - 1));
        cursorX = Math.min(cursorX, lines.get(cursorY).length());

        var font = Minecraft.getInstance().font;

        // 绘制背景
        fill(poseStack, x, y, x + getWidth(), y + getHeight(), 0xFF202020);

        // 绘制每一行文本
        for (int i = 0; i < lines.size(); i++) {
            font.draw(poseStack, lines.get(i), x + MARGIN, y + MARGIN + i * FONT_HEIGHT, 0xFFFFFF);
        }

        // 绘制光标
        String currentLine = lines.get(cursorY);
        int cursorXPos = x + MARGIN + calculateCursorX(currentLine);
        int cursorYPos = y + MARGIN + cursorY * FONT_HEIGHT;
        fill(poseStack, cursorXPos, cursorYPos, cursorXPos + 1, cursorYPos + FONT_HEIGHT, 0xFFFFFFFF);
    }

    private void insertText(String text) {
        if (lines.isEmpty()) {
            lines.add(""); // 如果没有行，添加一个空行
        }

        String currentLine = lines.get(cursorY);
        cursorX = Math.max(0, Math.min(cursorX, currentLine.length()));

        String beforeCursor = currentLine.substring(0, cursorX);
        String afterCursor = currentLine.substring(cursorX);

        currentLine = beforeCursor + text + afterCursor;
        lines.set(cursorY, currentLine);
        cursorX += text.length();

        // 自动换行
        var font = Minecraft.getInstance().font;
        while (font.width(currentLine) > maxWidth) {
            int breakIndex = findBreakIndex(currentLine);
            String overflow = currentLine.substring(breakIndex);
            currentLine = currentLine.substring(0, breakIndex);

            lines.set(cursorY, currentLine);
            if (cursorY + 1 < maxHeight) {
                cursorY++;
                cursorX = overflow.length();
                lines.add(cursorY, overflow);
                currentLine = overflow;
            } else {
                break;
            }
        }

        // 限制行数
        if (lines.size() > maxHeight) {
            lines.subList(maxHeight, lines.size()).clear();
        }
    }

    private void insertNewLine() {
        String currentLine = lines.get(cursorY);
        String afterCursor = currentLine.substring(cursorX);

        lines.set(cursorY, currentLine.substring(0, cursorX));
        lines.add(cursorY + 1, afterCursor);
        cursorY++;
        cursorX = 0;

        if (lines.size() > maxHeight) {
            lines.remove(lines.size() - 1);
        }
    }

    private void deleteCharacter() {
        if (cursorX > 0) {
            String currentLine = lines.get(cursorY);
            cursorX = Math.max(0, Math.min(cursorX, currentLine.length()));
            lines.set(cursorY, currentLine.substring(0, cursorX - 1) + currentLine.substring(cursorX));
            cursorX--;
        } else if (cursorY > 0) {
            String currentLine = lines.remove(cursorY);
            cursorY--;
            cursorX = lines.get(cursorY).length();
            lines.set(cursorY, lines.get(cursorY) + currentLine);
        }

        wrapLines();

        if (lines.isEmpty()) {
            lines.add("");
            cursorY = 0;
            cursorX = 0;
        }

        cursorY = Math.max(0, Math.min(cursorY, lines.size() - 1));
        cursorX = Math.min(cursorX, lines.get(cursorY).length());
    }

    private void wrapLines() {
        List<String> wrappedLines = new ArrayList<>();
        var font = Minecraft.getInstance().font;

        for (String line : lines) {
            while (font.width(line) > maxWidth) {
                int breakIndex = findBreakIndex(line);
                String overflow = line.substring(breakIndex);
                line = line.substring(0, breakIndex);

                wrappedLines.add(line);
                line = overflow;
            }
            wrappedLines.add(line);
        }

        lines.clear();
        lines.addAll(wrappedLines);

        if (lines.size() > maxHeight) {
            lines.subList(maxHeight, lines.size()).clear();
        }
    }

    private int findBreakIndex(String line) {
        var font = Minecraft.getInstance().font;
        int width = 0;
        int lastValidBreak = -1; // 记录最后一个有效的断点（单词边界）

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            width += font.width(String.valueOf(ch));

            // 如果是空格或标点，记录为可能的断点
            if (Character.isWhitespace(ch) || isPunctuation(ch)) {
                lastValidBreak = i;
            }

            // 如果宽度超过最大宽度
            if (width > maxWidth) {
                // 如果找到过有效断点，优先在该位置断开
                return (lastValidBreak != -1) ? lastValidBreak + 1 : i;
            }
        }
        return line.length(); // 如果不需要换行，返回行的末尾
    }

    private boolean isPunctuation(char ch) {
        // 判断是否为标点符号
        return "!.,;:?".indexOf(ch) != -1;
    }

    private int calculateCursorX(String line) {
        var font = Minecraft.getInstance().font;
        return font.width(line.substring(0, cursorX));
    }


    private void moveCursorLeft() {
        if (cursorX > 0) {
            cursorX--;
        } else if (cursorY > 0) {
            cursorY--;
            cursorX = lines.get(cursorY).length();
        }
    }

    private void moveCursorRight() {
        if (cursorX < lines.get(cursorY).length()) {
            cursorX++;
        } else if (cursorY < lines.size() - 1) {
            cursorY++;
            cursorX = 0;
        }
    }

    private void moveCursorUp() {
        if (cursorY > 0) {
            cursorY--;
            cursorX = Math.min(cursorX, lines.get(cursorY).length());
        }
    }

    private void moveCursorDown() {
        if (cursorY < lines.size() - 1) {
            cursorY++;
            cursorX = Math.min(cursorX, lines.get(cursorY).length());
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}