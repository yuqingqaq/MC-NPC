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
        int currentLineWidth = 0;
        int currentColor = isWhite ? PLAYER_COLOR : NPC_COLOR;

        boolean inQuotes = false;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // Handle line breaks
            if (ch == '\n') {
                if (currentLine.length() > 0) {
                    wrappedLines.add(new ColoredText(currentLine.toString(), currentColor));
                    currentLine = new StringBuilder();
                    currentLineWidth = 0;
                }
                continue;
            }

            // Handle special text formatting
            if (ch == '\"' || ch == '“' || ch == '”') {
                inQuotes = !inQuotes;
                currentColor = inQuotes ? QUOTE_COLOR : (isWhite ? PLAYER_COLOR : NPC_COLOR);
                continue;
            }

            // Calculate width and wrap text if necessary
            int charWidth = isChinese(ch) ? WIDTH_PER_CHINESE_CHAR : WIDTH_PER_ENGLISH_CHAR;
            if (currentLineWidth + charWidth > maxWidth) {
                wrappedLines.add(new ColoredText(currentLine.toString(), currentColor));
                currentLine = new StringBuilder();
                currentLineWidth = 0;
            }

            currentLine.append(ch);
            currentLineWidth += charWidth;
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