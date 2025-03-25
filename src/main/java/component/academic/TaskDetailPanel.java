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

    // 记录资源卡片的位置和高度，用于点击检测
    private int resourceCardY;
    private int resourceCardHeight;

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
        // 重新计算最大滚动距离，考虑资源卡片的动态高度
        this.maxScrollY = Math.max(0, getContentHeight() - height);
        this.scrollY = Math.min(scrollY, maxScrollY);
    }

    @Override
    protected int getContentHeight() {
        if (currentTask == null) return 0;

        // 基础高度
        int baseHeight = 120; // 目标卡片(60) + 位置和时间卡片(60)

        // 资源卡片高度：32(标题) + 资源数量*15 + 15(底部填充)
        int resourceHeight = 32 + (currentTask.getResources().size() * 15) + 15;

        // 策略卡片高度
        int strategyHeight = 35 + (currentTask.getStrategies().size() * 15);

        // 总结卡片高度
        int summaryHeight = 35 + (currentTask.getSummary().size() * 15);

        // 总高度 = 基础高度 + 资源高度 + 策略高度 + 总结高度
        return baseHeight + resourceHeight + strategyHeight + summaryHeight;
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        if (currentTask == null) return;

        // 渲染 10% 透明度的黑色背景
        fill(poseStack, left, top, left + width, top + height, 0x1A000000);

        int x = left + 5;
        int y = top - this.scrollY;

        // 目标卡片
        detailCard.renderNormalCard(poseStack, x, y, contentWidth, "目标",
                currentTask.getTarget(), TaskDetailCard.getTargetIcon(), false, null);
        y += 60;

        // 记录资源卡片的起始位置
        resourceCardY = y;

        // 资源卡片 - 使用新的自适应高度方法
        int resourceCardRenderedHeight = detailCard.renderResourceCard(poseStack, x, y, contentWidth, "所需资源",
                currentTask.getResources(), TaskDetailCard.getResourceIcon(), false, null);

        // 更新资源卡片高度和下一个卡片的位置
        resourceCardHeight = resourceCardRenderedHeight;
        y += resourceCardRenderedHeight;

        y += 10;
        // 位置和时间卡片
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
        y += 35 + currentTask.getStrategies().size() * 15;

        y += 10;
        // 完成情况
        summaryCardY = y; // 保存完成情况卡片的Y坐标
        List<String> summaryList = new ArrayList<>();
        for (Map.Entry<String, String> entry : currentTask.getSummary().entrySet()) {
            summaryList.add(entry.getKey() + ": " + entry.getValue());
        }
        detailCard.renderWideCard(poseStack, x, y, contentWidth, "完成情况",
                summaryList, TaskDetailCard.getSummaryIcon(), true, button -> {
                    mc.setScreen(new TaskCompletionScreen(mc));
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

        // 检查资源卡片中的NPC链接点击
        if (mouseY >= resourceCardY && mouseY <= resourceCardY + resourceCardHeight) {
            int npcId = detailCard.checkNpcLinkHover((int)mouseX, (int)mouseY);
            if (npcId != -1) {
                // 执行传送到NPC的命令
                teleportToNPC(npcId);
                return true;
            }
        }

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

    // 执行传送到指定NPC的方法
    private void teleportToNPC(int npcId) {
        if (mc.player != null) {
            mc.player.chat("/findnpc " + npcId);
        }
    }
}