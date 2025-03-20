package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import gui.academic.StrategyScreen;
import gui.academic.TaskCompletionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import model.AdaptiveTaskModel;
import net.minecraftforge.client.gui.ScrollPanel;
import system.UIScreenManager;
import component.academic.TaskDetailCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskDetailPanel extends ScrollPanel {
    private final TaskDetailCard detailCard;
    private AdaptiveTaskModel currentTask;
    private int contentWidth;
    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置
    private final Minecraft mc;
    
    private static final int BUTTON_WIDTH = 60;  // 定义按钮宽度
    private static final int BUTTON_HEIGHT = 20; // 定义按钮高度
    
    private int strategyCardY; // 完成策略卡片的Y坐标
    private int summaryCardY;   // 完成情况卡片的Y坐标
    
    public TaskDetailPanel(Minecraft mc, int width, int height, int top, int left, AdaptiveTaskModel initialTask) {
        super(mc, width, height, top, left, 0, 0, 0, 0, 0x00000000, 0x00000000, 0x00000000);
        this.mc = mc;
        this.detailCard = new TaskDetailCard(mc);
        this.contentWidth = width - 20;
        this.currentTask = initialTask;
        updateMaxScrollY();
    }
    
    public void setTask(AdaptiveTaskModel task) {
        this.currentTask = task;
        updateMaxScrollY();
    }
    
    private void updateMaxScrollY() {
        this.maxScrollY = Math.max(0, getContentHeight() - height);
        this.scrollY = Math.min(scrollY, maxScrollY);
    }
    
    @Override
    protected int getContentHeight() {
        if (currentTask == null) return 0;
        // 调整总高度，加上summary的高度
        return 450 + (currentTask.getSummary().size() * 15); 
    }
    
    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        if (currentTask == null) return;
        
        // 渲染 10% 透明度的黑色背景
        fill(poseStack, left, top, left + width, top + height, 0x1A000000);
        
        int x = left + 5;
        int y = top - this.scrollY;
        
        // 使用对应的图标
        detailCard.renderNormalCard(poseStack, x, y, contentWidth, "目标",
                currentTask.getTarget(), TaskDetailCard.getTargetIcon(), false, null);
        y += 60;
        
        detailCard.renderWideCard(poseStack, x, y, contentWidth, "所需资源",
                currentTask.getResources(), TaskDetailCard.getResourceIcon(), false, null);
        y += 80;
        
        int halfWidth = (contentWidth - 10) / 2;
        detailCard.renderNormalCard(poseStack, x, y, halfWidth, "推荐地点",
                currentTask.getLocation(), TaskDetailCard.getLocationIcon(), false, null);
        detailCard.renderNormalCard(poseStack, x + halfWidth + 10, y, halfWidth, "预计时间",
                currentTask.getEstimatedTime(), TaskDetailCard.getTimeIcon(), false, null);
        y += 60;
        
        // 完成策略
        strategyCardY = y; // 保存完成策略卡片的Y坐标
        detailCard.renderWideCard(poseStack, x, y, contentWidth, "完成策略",
                currentTask.getStrategies(), TaskDetailCard.getStrategyIcon(), true, button -> {
            mc.setScreen(new StrategyScreen(mc));
        });
        y += 90;
        
        // 完成情况
        summaryCardY = y; // 保存完成情况卡片的Y坐标
        List<String> summaryList = new ArrayList<>();
        for (Map.Entry<String, String> entry : currentTask.getSummary().entrySet()) {
            summaryList.add(entry.getKey() + ": " + entry.getValue());
        }
        detailCard.renderWideCard(poseStack, x, y, contentWidth, "完成情况",
                summaryList, TaskDetailCard.getSummaryIcon(), true, button -> {
            //mc.setScreen(new TaskPlanningScreen(mc));
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int deltaScroll = (int) (delta * getScrollAmount());
        if (scrollY + deltaScroll >= 0 && scrollY + deltaScroll <= maxScrollY) {
            scrollY += deltaScroll;
        } else if (scrollY + deltaScroll < 0) {
            scrollY = 0;
        } else if (scrollY + deltaScroll > maxScrollY) {
            scrollY = maxScrollY;
        }
        return true; // 表示事件已处理
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // 实现辅助功能描述
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentTask == null) return false;

        int x = left + 5;
        int y = top - this.scrollY;

        // 检查 "完成策略" 卡片的按钮
        if (detailCard.isMouseOverButton((int) mouseX, (int) mouseY, x, strategyCardY, contentWidth)) {
            mc.setScreen(new StrategyScreen(mc));
            return true;
        }

        // 检查 "完成情况" 卡片的按钮
        if (detailCard.isMouseOverButton((int) mouseX, (int) mouseY, x, summaryCardY, contentWidth)) {
            mc.setScreen(new TaskCompletionScreen(mc));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}

