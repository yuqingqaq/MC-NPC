package gui.adaptive;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import component.adaptive.NavigationBar;
import component.adaptive.ScrollableTextBox;
import component.adaptive.NoteModule;
import model.AdaptiveTaskModel;
import system.UIScreenManager;

import java.util.List;

public class TaskCompletionScreen extends Screen {
    private final Minecraft minecraft;
    private final AdaptiveTaskModel task;
    private NavigationBar navigationBar;
    private ScrollableTextBox summaryBox;
    private NoteModule noteModule;

    public TaskCompletionScreen(Minecraft minecraft, AdaptiveTaskModel task) {
        super(new TextComponent("任务完成情况"));
        this.minecraft = minecraft;
        this.task = task;
    }

    @Override
    protected void init() {
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        // 初始化导航栏
        List<String> strategies = task.getStrategies();
        this.navigationBar = new NavigationBar(minecraft, strategies, button -> {
            // 切换策略时的逻辑
            System.out.println("切换到策略：" + strategies.get(navigationBar.getSelectedIndex()));

        });

        // 初始化任务总结文本框
        this.summaryBox = new ScrollableTextBox(minecraft, this.width / 2 + 10, 40, this.width / 2 - 20, this.height / 2 - 60);
        // 在初始化中设置任务总结文本框的内容
        this.summaryBox.setText(task.getSummaryAsString());
        // 初始化笔记模块
        this.noteModule = new NoteModule(minecraft, this.width / 2 + 10, this.height / 2 + 10, this.width / 2 - 20, this.height / 2 - 60);
        this.noteModule.setText(task.getNotes()); // 显示玩家保存的笔记
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 渲染标题
        drawCenteredString(poseStack, this.font, "任务完成情况", this.width / 2, 10, 0xFFFFFF);

        // 渲染导航栏
        this.navigationBar.render(poseStack, 10, 40);

        // 渲染任务总结文本框
        this.summaryBox.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染笔记模块
        this.noteModule.render(poseStack, mouseX, mouseY, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 处理导航栏的点击
        if (this.navigationBar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理任务总结文本框的滚动
        if (this.summaryBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理笔记模块的交互
        if (this.noteModule.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 处理任务总结文本框的输入
        if (this.summaryBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // 处理笔记模块的输入
        if (this.noteModule.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // 处理任务总结文本框的字符输入
        if (this.summaryBox.charTyped(codePoint, modifiers)) {
            return true;
        }

        // 处理笔记模块的字符输入
        if (this.noteModule.charTyped(codePoint, modifiers)) {
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 界面不会暂停游戏
    }

    @Override
    public void onClose() {
        super.onClose();

        // 保存玩家的笔记到任务模型中
        task.setNotes(this.noteModule.getText());

        UIScreenManager.getInstance().switchToTaskOverviewScreen();
    }
}