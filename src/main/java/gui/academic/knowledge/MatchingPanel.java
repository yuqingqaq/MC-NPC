package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.gui.GuiComponent.fill;

public class MatchingPanel extends BaseQuestionPanel {
    private final String conceptCategory;      // 概念分类
    private final List<String> leftItems;      // 左侧项目
    private final List<String> rightItems;     // 右侧项目
    private final Map<Integer, Integer> correctMatches; // 正确匹配(左索引->右索引)

    // 用户选择的匹配
    private Map<Integer, Integer> userMatches = new HashMap<>();
    // 当前选中的左侧项目
    private int selectedLeftItem = -1;

    // 左右两侧按钮
    private List<Button> leftButtons = new ArrayList<>();
    private List<Button> rightButtons = new ArrayList<>();

    // 布局常量
    private static final int ITEM_HEIGHT = 25; // 项目间距

    public MatchingPanel(String conceptCategory, List<String> leftItems,
                         List<String> rightItems, Map<Integer, Integer> correctMatches) {
        this.conceptCategory = conceptCategory;
        this.leftItems = new ArrayList<>(leftItems);
        this.rightItems = new ArrayList<>(rightItems);
        this.correctMatches = new HashMap<>(correctMatches);
    }

    @Override
    protected void initPanel() {
        // 清空按钮列表
        leftButtons.clear();
        rightButtons.clear();

        // 左侧面板宽度
        int halfWidth = width / 2;

        // 添加左侧项目按钮
        for (int i = 0; i < leftItems.size(); i++) {
            final int leftIndex = i;
            Button leftButton = addButton(
                    x + 20,
                    y + CONTENT_START_Y + 10 + (i * ITEM_HEIGHT),
                    halfWidth - 40,
                    20,
                    new TextComponent(leftItems.get(i)),
                    button -> selectLeftItem(leftIndex)
            );
            leftButtons.add(leftButton);
        }

        // 添加右侧项目按钮
        for (int i = 0; i < rightItems.size(); i++) {
            final int rightIndex = i;
            Button rightButton = addButton(
                    x + halfWidth + 20,
                    y + CONTENT_START_Y + 10 + (i * ITEM_HEIGHT),
                    halfWidth - 40,
                    20,
                    new TextComponent(rightItems.get(i)),
                    button -> selectRightItem(rightIndex)
            );
            rightButtons.add(rightButton);
        }

        // 初始状态更新
        updateMatchDisplay();
    }

    private void selectLeftItem(int index) {
        // 检查索引是否有效
        if (index < 0 || index >= leftItems.size()) {
            return; // 索引无效，不做任何处理
        }

        // 如果该项已经匹配，则取消匹配
        if (userMatches.containsKey(index)) {
            userMatches.remove(index);
            updateMatchDisplay();
            return;
        }

        // 重置所有左侧按钮样式（但保留已匹配项的样式）
        for (int i = 0; i < leftButtons.size(); i++) {
            if (!userMatches.containsKey(i)) {
                leftButtons.get(i).setMessage(new TextComponent(leftItems.get(i)));
            }
        }

        // 高亮选中的左侧按钮
        leftButtons.get(index).setMessage(new TextComponent("→ " + leftItems.get(index)));
        selectedLeftItem = index;
    }

    private void selectRightItem(int index) {
        // 检查索引是否有效
        if (index < 0 || index >= rightItems.size()) {
            return; // 索引无效，不做任何处理
        }

        // 如果没有选中左侧项，检查是否已有右侧项匹配，若有则移除匹配
        if (selectedLeftItem == -1) {
            // 查找是否有匹配到这个右侧项的左侧项
            Integer leftIndex = null;
            for (Map.Entry<Integer, Integer> entry : userMatches.entrySet()) {
                if (entry.getValue() == index) {
                    leftIndex = entry.getKey();
                    break;
                }
            }

            if (leftIndex != null) {
                userMatches.remove(leftIndex);
                updateMatchDisplay();
            }
            return;
        }

        // 如果右侧项已被匹配，先移除旧匹配
        Integer existingLeftIndex = null;
        for (Map.Entry<Integer, Integer> entry : userMatches.entrySet()) {
            if (entry.getValue() == index) {
                existingLeftIndex = entry.getKey();
                break;
            }
        }

        if (existingLeftIndex != null) {
            userMatches.remove(existingLeftIndex);
        }

        // 如果左侧项已有匹配，先移除旧匹配
        if (userMatches.containsKey(selectedLeftItem)) {
            userMatches.remove(selectedLeftItem);
        }

        // 建立新匹配
        userMatches.put(selectedLeftItem, index);

        // 更新按钮样式显示匹配
        updateMatchDisplay();

        // 重置选择
        selectedLeftItem = -1;
    }

    private void updateMatchDisplay() {
        // 重置所有按钮样式
        for (int i = 0; i < leftButtons.size(); i++) {
            leftButtons.get(i).setMessage(new TextComponent(leftItems.get(i)));
        }

        for (int i = 0; i < rightButtons.size(); i++) {
            rightButtons.get(i).setMessage(new TextComponent(rightItems.get(i)));
        }

        // 显示当前匹配
        for (Map.Entry<Integer, Integer> match : userMatches.entrySet()) {
            int leftIndex = match.getKey();
            int rightIndex = match.getValue();

            if (leftIndex < leftButtons.size() && rightIndex < rightButtons.size()) {
                // 使用编号标记匹配对
                String matchNumber = String.valueOf(leftIndex + 1); // 从1开始编号
                leftButtons.get(leftIndex).setMessage(
                        new TextComponent("(" + matchNumber + ") " + leftItems.get(leftIndex)));
                rightButtons.get(rightIndex).setMessage(
                        new TextComponent("(" + matchNumber + ") " + rightItems.get(rightIndex)));
            }
        }

        // 如果有选中的左侧项，高亮显示
        if (selectedLeftItem != -1 && selectedLeftItem < leftButtons.size()) {
            leftButtons.get(selectedLeftItem).setMessage(
                    new TextComponent("→ " + leftItems.get(selectedLeftItem)));
        }
    }

    @Override
    public int getContentHeight() {
        return Math.max(leftItems.size(), rightItems.size()) * ITEM_HEIGHT + 20; // 额外空间用于进度显示
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        renderBackground(poseStack);

        // 绘制标题和说明
        String title = "概念连线：" + conceptCategory;
        String instructions = "单击两侧的项目即可创建或取消连线。\n完成所有匹配，然后点击“提交”。";
        renderTitleAndInstructions(poseStack, title, instructions);

        // 绘制连线
        for (Map.Entry<Integer, Integer> match : userMatches.entrySet()) {
            int leftIndex = match.getKey();
            int rightIndex = match.getValue();

            if (leftIndex < leftButtons.size() && rightIndex < rightButtons.size()) {
                int halfWidth = width / 2;

                // 计算线条起点和终点
                int startX = x + halfWidth - 40;
                int startY = y + CONTENT_START_Y + (leftIndex * ITEM_HEIGHT) + 10;
                int endX = x + halfWidth + 20;
                int endY = y + CONTENT_START_Y + (rightIndex * ITEM_HEIGHT) + 10;

                // 绘制线条
                drawLine(poseStack, startX, startY, endX, endY, 0xFFFFFFFF);
            }
        }

        // 显示匹配进度
        String progressText = "Matches: " + userMatches.size() + "/" + correctMatches.size();
        drawString(poseStack, progressText,
                x + width - 120, y + CONTENT_START_Y + 10 + (Math.max(leftItems.size(), rightItems.size()) * ITEM_HEIGHT) + 5,
                0xFFAAAA);
    }

    // 绘制线条的辅助方法
    private void drawLine(PoseStack poseStack, int startX, int startY, int endX, int endY, int color) {
        // 使用Bresenham算法绘制线条
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        while (true) {
            drawPixel(poseStack, startX, startY, color);
            if (startX == endX && startY == endY) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                startX = startX + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                startY = startY + sy;
            }
        }
    }

    // 绘制像素的辅助方法
    private void drawPixel(PoseStack poseStack, int x, int y, int color) {
        fill(poseStack, x, y, x + 1, y + 1, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false; // 按钮点击已由Screen处理
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false; // 不需要滚动
    }

    @Override
    public boolean isAnswerCorrect() {
        // 检查是否完成所有匹配
        if (userMatches.size() < correctMatches.size()) {
            return false;
        }

        // 检查所有匹配是否正确
        for (Map.Entry<Integer, Integer> correctMatch : correctMatches.entrySet()) {
            Integer userMatch = userMatches.get(correctMatch.getKey());
            if (userMatch == null || !userMatch.equals(correctMatch.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getCorrectFeedback() {
        return "完美！所有匹配均正确。" +
                "你已经掌握了这些概念之间的关系。";
    }

    @Override
    public String getIncorrectFeedback() {
        int correctCount = 0;
        for (Map.Entry<Integer, Integer> correctMatch : correctMatches.entrySet()) {
            Integer userMatch = userMatches.get(correctMatch.getKey());
            if (userMatch != null && userMatch.equals(correctMatch.getValue())) {
                correctCount++;
            }
        }

        return "你做对了 " + correctCount + "/" + correctMatches.size() + " 连线题目！" +
                "再检查一下吧！";
    }

    @Override
    public String getHintPrompt() {
        return "提供关于概念之间关系的提示： " + conceptCategory;
    }

    @Override
    public void onCorrectAnswer() {
        // 记录玩家掌握了这组概念关系
        GameController.getInstance().addRelationshipsToKnowledgeGraph(conceptCategory);
    }
}