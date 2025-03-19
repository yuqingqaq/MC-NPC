package gui.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import component.academic.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import metadata.NPCMessage;
import system.UIScreenManager;
import system.UITaskManager;
import model.AdaptiveTaskModel;
import model.AdaptiveSubTaskModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyScreen extends Screen {
    private final Minecraft minecraft;
    private AbstractWidget rightPanel = null; // Change to Widget to hold any panel type
    private StrategyPanel strategyPanel; // Left strategy panel

    public StrategyScreen(Minecraft minecraft) {
        super(new TextComponent("策略沙盘"));
        this.minecraft = minecraft;
    }

    @Override
    protected void init() {
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);
        
        // 获取当前任务
        AdaptiveTaskModel currentTask = UITaskManager.getInstance().getCurrentTaskInOverview();
        if (currentTask == null) {
            // 处理没有当前任务的情况
            return;
        }

        // 从当前任务中获取策略
        Map<String, List<String>> strategies = new HashMap<>();
        List<String> taskStrategies = currentTask.getStrategies();
        if (taskStrategies != null && !taskStrategies.isEmpty()) {
            for (String strategy : taskStrategies) {
                strategies.put(strategy, List.of("相关描述"));
            }
        } else {
            // 处理没有策略的情况
            return;
        }

        // 创建左侧的策略面板
        this.strategyPanel = new StrategyPanel(minecraft, 80, this.height - 40, 40, 20, strategies, this::onStrategySelected);
        this.addRenderableWidget(strategyPanel);
    }

    private void onStrategySelected(String strategy) {
        // Remove the old right panel (if it exists)
        if (rightPanel != null) {
            this.removeWidget(rightPanel); // Remove the current panel
            rightPanel = null;  // Clear the reference
        }
        int rightPanelX = 110; // Left panel width + spacing
        int rightPanelWidth = this.width - rightPanelX - 20;

        // 获取当前任务
        AdaptiveTaskModel currentTask = UITaskManager.getInstance().getCurrentTaskInOverview();

        // Create the new right panel based on the selected strategy
        if ("辅助规划".equals(strategy)) {
            // Initialize DialoguePanel
            List<NPCMessage> chatHistory = new ArrayList<>();
            chatHistory.add(new NPCMessage("npc", "欢迎开始对话！"));
            
            this.rightPanel = new DialoguePanel(
                minecraft,
                rightPanelX,         // x
                40,                  // y
                rightPanelWidth,     // width
                this.height - 60,    // height
                chatHistory
            );
        } else if ("自行规划".equals(strategy)) {
            // 从当前任务中获取子任务
            List<AdaptiveSubTaskModel> tasks = new ArrayList<>();
            for (AdaptiveSubTaskModel subTask : currentTask.getSubTasks()) {
                tasks.add(subTask);
            }
            
            this.rightPanel = new TaskPlanningPanel(rightPanelX + 40, 40, (int)(rightPanelWidth/1.5), this.height - 60, tasks);
        } else if ("协作规划".equals(strategy)) {
            // 从当前任务中获取子任务
            List<AdaptiveSubTaskModel> tasks = new ArrayList<>();
            for (AdaptiveSubTaskModel subTask : currentTask.getSubTasks()) {
                tasks.add(subTask);
            }

            this.rightPanel = new TaskPlanningSupportPanel(rightPanelX, 40, rightPanelWidth, this.height - 60, tasks);
        }
        else if ("问问专家".equals(strategy)) {
            // 使用 ExpertPanel
            this.rightPanel = new ExpertPanel(rightPanelX, 40, rightPanelWidth, this.height - 60);
        }


        // Add the new right panel if it is not null
        if (rightPanel != null) {
            this.addRenderableWidget(rightPanel); // Add directly without casting
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 渲染标题
        drawCenteredString(poseStack, this.font, "策略沙盘", this.width / 2, 10, 0xFFFFFF);

        // 渲染左侧策略面板
        this.strategyPanel.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染右侧面板（如果存在）
        if (this.rightPanel != null) {
            this.rightPanel.render(poseStack, mouseX, mouseY, partialTicks);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 将鼠标点击事件传递给右侧面板
        if (this.rightPanel != null && this.rightPanel.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 将键盘事件传递给右侧面板
        if (this.rightPanel != null && this.rightPanel.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // 将字符输入事件传递给右侧面板
        if (this.rightPanel != null && this.rightPanel.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // 将鼠标滚轮事件传递给右侧面板
        if (this.rightPanel != null && this.rightPanel.mouseScrolled(mouseX, mouseY, amount)) {
            return true;
        }
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