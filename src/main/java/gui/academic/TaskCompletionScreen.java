package gui.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import component.academic.NavigationBar;
import component.academic.ScrollableTextBox;
import component.academic.NoteModule;
import model.AcademicTaskModel;
import system.UIScreenManager;

import java.util.List;

public class TaskCompletionScreen extends Screen {
    private final Minecraft minecraft;
    private final AcademicTaskModel task;
    private NavigationBar navigationBar;
    private ScrollableTextBox summaryBox;
    private NoteModule noteModule;

    public TaskCompletionScreen(Minecraft minecraft, AcademicTaskModel task) {
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
        });

        // 初始化任务总结文本框
        this.summaryBox = new ScrollableTextBox(minecraft, 10, 40, this.width / 2 - 20, this.height / 2 - 60);
        this.summaryBox.setText(task.getSummary().toString());

        // 初始化笔记模块
        this.noteModule = new NoteModule(minecraft, 10, this.height / 2, this.width / 2 - 20, this.height / 2 - 60);
        this.noteModule.setText(task.getNotes());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 渲染标题
        drawCenteredString(poseStack, this.font, "任务完成情况", this.width / 2, 10, 0xFFFFFF);

        // 渲染导航栏
        this.navigationBar.render(poseStack, 10, 20);

        // 渲染任务总结文本框
        this.summaryBox.render(poseStack);

        // 渲染笔记模块
        this.noteModule.render(poseStack);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 处理鼠标点击事件
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 处理键盘事件
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // 处理字符输入事件
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // 处理鼠标滚轮事件
        return super.mouseScrolled(mouseX, mouseY, amount);
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
        UIScreenManager.getInstance().switchToTaskOverviewScreen();
    }
} 