package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class OrderingPanel extends BaseQuestionPanel {
    private final String orderingTopic;        // 排序主题
    private final List<String> correctOrder;   // 正确排序

    private List<String> currentOrder;         // 当前排序
    private int selectedItemIndex = -1;        // 当前选中的项目索引

    private List<Button> itemButtons = new ArrayList<>();

    // 布局常量
    private static final int ITEM_HEIGHT = 25; // 项目间距
    private static final int COLUMN_ITEMS = 3; // 每列显示的项目数

    public OrderingPanel(String orderingTopic, List<String> correctOrder) {
        this.orderingTopic = orderingTopic;
        this.correctOrder = new ArrayList<>(correctOrder);

        // 创建一个打乱顺序的副本作为初始排序
        this.currentOrder = new ArrayList<>(correctOrder);
        java.util.Collections.shuffle(this.currentOrder);
    }

    @Override
    protected void initPanel() {
        // 清空按钮列表
        itemButtons.clear();

        // 计算需要的列数
        int totalItems = currentOrder.size();
        int columns = (totalItems + COLUMN_ITEMS - 1) / COLUMN_ITEMS; // 向上取整
        int columnWidth = width / columns;

        // 添加项目按钮 - 使用多列布局
        for (int i = 0; i < currentOrder.size(); i++) {
            final int itemIndex = i;
            int column = i / COLUMN_ITEMS;
            int row = i % COLUMN_ITEMS;

            Button itemButton = addButton(
                    x + 20 + (column * columnWidth),
                    y + CONTENT_START_Y + (row * ITEM_HEIGHT),
                    columnWidth - 40,
                    20,
                    new TextComponent((i+1) + ". " + currentOrder.get(i)),
                    button -> selectItem(itemIndex)
            );
            itemButtons.add(itemButton);
        }

        // 添加上移/下移按钮 - 使用屏幕底部的固定位置
        int buttonWidth = 100;
        int buttonSpacing = 20;

        addButton(
                x + (width / 2) - buttonWidth - buttonSpacing,
                y + CONTENT_START_Y + getContentHeight() - 30,
                buttonWidth,
                20,
                new TextComponent("上移"),
                button -> moveItem(-1)
        );

        addButton(
                x + (width / 2) + buttonSpacing,
                y + CONTENT_START_Y + getContentHeight() - 30,
                buttonWidth,
                20,
                new TextComponent("下移"),
                button -> moveItem(1)
        );
    }

    private void selectItem(int index) {
        // 重置所有按钮样式
        for (int i = 0; i < itemButtons.size(); i++) {
            itemButtons.get(i).setMessage(new TextComponent((i+1) + ". " + currentOrder.get(i)));
        }

        // 高亮选中的按钮
        if (selectedItemIndex == index) {
            // 如果再次点击同一项，取消选择
            selectedItemIndex = -1;
        } else {
            // 选择新项目
            itemButtons.get(index).setMessage(new TextComponent("→ " + (index+1) + ". " + currentOrder.get(index)));
            selectedItemIndex = index;
        }
    }

    private void moveItem(int direction) {
        if (selectedItemIndex == -1) {
            return; // 没有选中项目
        }

        int newIndex = selectedItemIndex + direction;

        // 检查新索引是否有效
        if (newIndex >= 0 && newIndex < currentOrder.size()) {
            // 交换项目
            String temp = currentOrder.get(selectedItemIndex);
            currentOrder.set(selectedItemIndex, currentOrder.get(newIndex));
            currentOrder.set(newIndex, temp);

            // 更新选中的索引
            selectedItemIndex = newIndex;

            // 更新所有按钮文本
            for (int i = 0; i < itemButtons.size(); i++) {
                if (i == selectedItemIndex) {
                    itemButtons.get(i).setMessage(
                            new TextComponent("→ " + (i+1) + ". " + currentOrder.get(i)));
                } else {
                    itemButtons.get(i).setMessage(
                            new TextComponent((i+1) + ". " + currentOrder.get(i)));
                }
            }
        }
    }

    @Override
    public int getContentHeight() {
        // 计算需要的行数
        int rows = Math.min(COLUMN_ITEMS, currentOrder.size());
        return (rows * ITEM_HEIGHT) + 40; // 额外空间用于移动按钮
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        renderBackground(poseStack);

        // 绘制标题和说明
        String title = "按正确顺序排列： " + orderingTopic;
        String instructions = "选择一个项目，然后使用“上移/下移”按钮重新排序。";
        renderTitleAndInstructions(poseStack, title, instructions);

        // 如果没有选择项，添加提示
        if (selectedItemIndex == -1) {
            drawString(poseStack, "单击某个项目首先选择它",
                    x + (width / 2) - 100,
                    y + CONTENT_START_Y + getContentHeight(),
                    0xFFAAAA);
        }
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
        // 检查当前顺序是否正确
        for (int i = 0; i < correctOrder.size(); i++) {
            if (!currentOrder.get(i).equals(correctOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getCorrectFeedback() {
        return "太棒了！你把所有东西都按正确的顺序排列了! \n" ; }

    @Override
    public String getIncorrectFeedback() {
        // 找出第一个错误的位置
        int firstErrorIndex = -1;
        for (int i = 0; i < correctOrder.size(); i++) {
            if (!currentOrder.get(i).equals(correctOrder.get(i))) {
                firstErrorIndex = i;
                break;
            }
        }

        if (firstErrorIndex != -1) {
            if(GameController.getInstance().isSRLQuestAvailable()){
                return "顺序不太正确。" +
                        "检查 " + currentOrder.get(firstErrorIndex) + "的位置";
            }
            else{
                return "顺序不太正确。" ;
            }

        } else {
        return "顺序不太正确。" +
        "考虑一下逻辑顺序。";
        }
    
    }
    @Override
    public String getHintPrompt() {
        // 找出第一个错误的位置来提供针对性提示
        int firstErrorIndex = -1;
        for (int i = 0; i < correctOrder.size(); i++) {
            if (!currentOrder.get(i).equals(correctOrder.get(i))) {
                firstErrorIndex = i;
                break;
            }
        }

        if (firstErrorIndex != -1) {
            String hintItem = currentOrder.get(firstErrorIndex);
            return "提示: " + hintItem + " 应放在 " + orderingTopic + " 时间轴的不同位置。";
        } else {
            // 格式化完整的正确顺序
            StringBuilder correctSequence = new StringBuilder();
            correctSequence.append("正确的").append(orderingTopic).append("顺序是: \n");
            for (int i = 0; i < correctOrder.size(); i++) {
                correctSequence.append((i+1)).append(". ").append(correctOrder.get(i));
                if (i < correctOrder.size() - 1) {
                    correctSequence.append("\n");
                }
            }
            return correctSequence.toString();
        }
    }
    @Override
    public void onCorrectAnswer() {
        // 记录玩家掌握了这个顺序
        GameController.getInstance().addTimelineToKnowledgeGraph(orderingTopic);
    }

}