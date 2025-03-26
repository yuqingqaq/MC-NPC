package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// 所有题目面板的基类
public abstract class BaseQuestionPanel {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected QuestionScreen parentScreen;

    // 统一的布局常量
    protected static final int TITLE_Y_OFFSET = 5;  // 标题位置
    protected static final int SUBTITLE_Y_OFFSET = 25; // 子标题/说明位置
    protected static final int CONTENT_START_Y = 45; // 内容起始位置
    protected static final int BUTTON_MARGIN = 20;  // 提交按钮与内容的间距

    // 默认的内容区高度 - 子类可以覆盖
    protected static final int DEFAULT_CONTENT_HEIGHT = 300;

    // 初始化面板
    public void init(Screen parentScreen, int x, int y, int width, int height) {
        if (!(parentScreen instanceof QuestionScreen)) {
            throw new IllegalArgumentException("Parent screen must be a QuestionScreen");
        }

        this.parentScreen = (QuestionScreen) parentScreen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        initPanel();
    }

    // 添加组件的便捷方法
    protected <T extends AbstractWidget> T addRenderableWidget(T widget) {
        return parentScreen.addPanelWidget(widget);
    }

    // 添加按钮的便捷方法
    protected Button addButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
        return addRenderableWidget(new Button(x, y, width, height, message, onPress));
    }

    // 绘制背景的辅助方法
    protected void renderBackground(PoseStack poseStack) {
        parentScreen.renderPanelBackground(poseStack, x, y, width, height);
    }

    // 绘制文本的辅助方法
    protected void drawString(PoseStack poseStack, String text, int x, int y, int color) {
        Font font = parentScreen.getMinecraft().font;
        parentScreen.getMinecraft().font.draw(poseStack, text, x, y, color);
    }

    // 计算Submit按钮Y坐标的方法 - 统一位置
    public int getSubmitButtonY() {
        return y + CONTENT_START_Y + getContentHeight() + BUTTON_MARGIN;
    }

    // 获取内容高度的方法
    public int getContentHeight() {
        return DEFAULT_CONTENT_HEIGHT; // 默认实现，子类应该覆盖
    }

    // 绘制标题和说明 - 统一格式
    protected void renderTitleAndInstructions(PoseStack poseStack, String title, String instructions) {
        drawString(poseStack, title, x + 10, y + TITLE_Y_OFFSET, 0xFFFFFF);

        if (instructions != null && !instructions.isEmpty()) {
            String[] lines = instructions.split("\n");
            for (int i = 0; i < lines.length; i++) {
                drawString(poseStack, lines[i], x + 10, y + SUBTITLE_Y_OFFSET + (i * 15), 0xFFFFFF);
            }
        }
    }

    // 各子类实现自己的初始化逻辑
    protected abstract void initPanel();

    // 渲染面板
    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

    // 处理鼠标点击
    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

    // 处理鼠标滚轮
    public abstract boolean mouseScrolled(double mouseX, double mouseY, double delta);

    // 检查答案是否正确
    public abstract boolean isAnswerCorrect();

    // 获取正确答案时的反馈
    public abstract String getCorrectFeedback();

    // 获取错误答案时的反馈
    public abstract String getIncorrectFeedback();

    // 获取提示提示词
    public abstract String getHintPrompt();

    // 正确答案的操作
    public abstract void onCorrectAnswer();
}