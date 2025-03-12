package component;

import component.ColoredText;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    // Constants for colors and character widths
    private static final int WIDTH_PER_ENGLISH_CHAR = 6;
    private static final int WIDTH_PER_CHINESE_CHAR = 11;
    private static final int DEFAULT_COLOR = 0xFFFFFF;  // White for hints
    private static final int NPC_COLOR = 0xFFFFAA00;    // Yellow for NPC
    private static final int PLAYER_COLOR = 0xFFFFFFFF; // White for player
    private static final int QUOTE_COLOR = 0xFFAAFF;    // Light purple for quotes

    public static List<ColoredText> wrapText(String text, int maxWidth, boolean isWhite) {
        List<ColoredText> wrappedLines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        StringBuilder currentWord = new StringBuilder(); // 用于暂存当前单词
        int currentLineWidth = 0;
        int currentWordWidth = 0;
        int currentColor = isWhite ? PLAYER_COLOR : NPC_COLOR;

        boolean inQuotes = false;
        boolean inHighlight = false;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // Handle line breaks
            if (ch == '\n') {
                if (currentWord.length() > 0) {
                    // 将当前单词添加到行中
                    currentLine.append(currentWord);
                    currentLineWidth += currentWordWidth;
                    currentWord = new StringBuilder();
                    currentWordWidth = 0;
                }
                if (currentLine.length() > 0) {
                    wrappedLines.add(new ColoredText(currentLine.toString(), currentColor));
                    currentLine = new StringBuilder();
                    currentLineWidth = 0;
                }
                continue;
            }

            // Detect highlight markers (**)
            if (ch == '*' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
                inHighlight = !inHighlight;
                currentColor = inHighlight ? QUOTE_COLOR : (isWhite ? PLAYER_COLOR : NPC_COLOR);
                i++; // Skip the second '*'
                continue;
            }

            // Handle special text formatting
            if (ch == '\"' || ch == '“' || ch == '”') {
                inQuotes = !inQuotes;
                currentColor = inQuotes ? QUOTE_COLOR : (isWhite ? PLAYER_COLOR : NPC_COLOR);
                continue;
            }

            // Calculate character width
            int charWidth = isChinese(ch) ? WIDTH_PER_CHINESE_CHAR : WIDTH_PER_ENGLISH_CHAR;

            // Check if we need to wrap the line
            if (currentLineWidth + currentWordWidth + charWidth > maxWidth) {
                if (currentLine.length() > 0) {
                    wrappedLines.add(new ColoredText(currentLine.toString(), currentColor));
                    currentLine = new StringBuilder();
                    currentLineWidth = 0;
                }

                // 如果当前单词的宽度超出了行宽，直接换行
                if (currentWordWidth + charWidth > maxWidth) {
                    wrappedLines.add(new ColoredText(currentWord.toString(), currentColor));
                    currentWord = new StringBuilder();
                    currentWordWidth = 0;
                }
            }

            // Append to current word or directly to the line
            if (Character.isWhitespace(ch)) {
                // 如果是空白字符，将当前单词添加到当前行中
                currentLine.append(currentWord).append(ch);
                currentLineWidth += currentWordWidth + charWidth;
                currentWord = new StringBuilder();
                currentWordWidth = 0;
            } else {
                // 非空白字符，追加到当前单词中
                currentWord.append(ch);
                currentWordWidth += charWidth;
            }
        }

        // 添加剩余的单词和行
        if (currentWord.length() > 0) {
            currentLine.append(currentWord);
        }
        if (currentLine.length() > 0) {
            wrappedLines.add(new ColoredText(currentLine.toString(), currentColor));
        }

        return wrappedLines;
    }

    private static boolean isChinese(char ch) {
        return (ch >= '\u4E00' && ch <= '\u9FFF') || (ch >= '\u3400' && ch <= '\u4DBF') ||
                (ch >= '\uF900' && ch <= '\uFAFF');
    }
}