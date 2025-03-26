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

    // 缓存总内容高度
    private int totalContentHeight = 0;

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
        this.scrollY = 0; // 重置滚动位置
        detailCard.clearNpcLinks(); // 清除旧的NPC链接
        updateMaxScrollY();
    }

    private void updateMaxScrollY() {
        // 计算总内容高度
        totalContentHeight = calculateContentHeight();

        // 计算最大可滚动距离
        this.maxScrollY = Math.max(0, totalContentHeight - height);

        // 确保当前滚动位置不超过最大值
        this.scrollY = Math.min(scrollY, maxScrollY);

        System.out.println("Total content height: " + totalContentHeight);
        System.out.println("Panel height: " + height);
        System.out.println("Max scroll Y: " + maxScrollY);
    }

    private int calculateContentHeight() {
        if (currentTask == null) return 0;

        // 基础高度
        int baseHeight = 120; // 目标卡片(60) + 位置和时间卡片(60)

        // 资源卡片高度：32(标题) + 资源数量*15 + 15(底部填充)
        int resourceHeight = 32 + (currentTask.getResources().size() * 15) + 15;

        // 策略卡片高度
        int strategyHeight = 35 + (currentTask.getStrategies().size() * 15);

        // 总结卡片高度
        int summaryHeight = 35 + (currentTask.getSummary().size() * 15);

        // 额外间距
        int extraPadding = 20; // 卡片之间的额外间距

        // 总高度 = 基础高度 + 资源高度 + 策略高度 + 总结高度 + 额外间距
        return baseHeight + resourceHeight + strategyHeight + summaryHeight + extraPadding;
    }

    @Override
    protected int getContentHeight() {
        return totalContentHeight;
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollTop, int visibleHeight) {
        if (currentTask == null) return;

        // 渲染 10% 透明度的黑色背景
        fill(poseStack, left, top, left + width, top + height, 0x1A000000);

        int x = left + 5;
        int y = top + 5 - this.scrollY; // 起始位置考虑滚动偏移

        // 调试信息 - 在屏幕上显示滚动状态
        // minecraft.font.draw(poseStack, "Scroll: " + this.scrollY + "/" + this.maxScrollY, left + 10, top + height - 15, 0xFFFFFF);

        // 目标卡片
        detailCard.renderNormalCard(poseStack, x, y, contentWidth, "目标",
                currentTask.getTarget(), TaskDetailCard.getTargetIcon(), false, null);
        y += 60;

        // 记录资源卡片的起始位置
        resourceCardY = y - (top - this.scrollY);

        // 资源卡片 - 使用新的自适应高度方法
        detailCard.clearNpcLinks(); // 确保清除旧的链接
        int resourceCardRenderedHeight = detailCard.renderResourceCard(poseStack, x, y, contentWidth, "所需资源",
                currentTask.getResources(), TaskDetailCard.getResourceIcon(), false, null);

        // 更新资源卡片高度和下一个卡片的位置
        resourceCardHeight = resourceCardRenderedHeight;
        y += resourceCardRenderedHeight + 10; // 添加额外间距

        // 位置和时间卡片
        int halfWidth = (contentWidth - 10) / 2;
        detailCard.renderNormalCard(poseStack, x, y, halfWidth, "推荐地点",
                currentTask.getLocation(), TaskDetailCard.getLocationIcon(), false, null);
        detailCard.renderNormalCard(poseStack, x + halfWidth + 10, y, halfWidth, "预计时间",
                currentTask.getEstimatedTime(), TaskDetailCard.getTimeIcon(), false, null);
        y += 60;

        // 完成策略
        strategyCardY = y - (top - this.scrollY); // 保存完成策略卡片的Y坐标
        detailCard.renderWideCard(poseStack, x, y, contentWidth, "完成策略",
                currentTask.getStrategies(), TaskDetailCard.getStrategyIcon(), true, button -> {
                    mc.setScreen(new StrategyScreen(mc));
                });
        y += 35 + currentTask.getStrategies().size() * 15 + 10; // 添加额外间距

        // 完成情况
        summaryCardY = y - (top - this.scrollY); // 保存完成情况卡片的Y坐标
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
        // 计算滚动增量
        int scrollAmount = 20; // 每次滚动的像素数量
        int deltaScroll = (int) (-delta * scrollAmount); // 注意负号，向下滚动为正

        // 计算新的滚动位置
        int newScrollY = this.scrollY + deltaScroll;

        // 确保滚动位置在有效范围内
        if (newScrollY < 0) {
            newScrollY = 0;
        } else if (newScrollY > maxScrollY) {
            newScrollY = maxScrollY;
        }

        // 如果滚动位置改变了，更新并返回true
        if (this.scrollY != newScrollY) {
            this.scrollY = newScrollY;
            return true;
        }

        return false;
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

        // 检查资源卡片中的NPC链接点击
        if (mouseY >= top + (resourceCardY - this.scrollY) &&
                mouseY <= top + (resourceCardY - this.scrollY) + resourceCardHeight) {

            // 转换鼠标坐标到panel内部坐标系，考虑滚动位置
            int npcMouseX = (int)mouseX;
            int npcMouseY = (int)mouseY + this.scrollY;

            int npcId = detailCard.checkNpcLinkHover(npcMouseX, npcMouseY);
            if (npcId != -1) {
                // 执行传送到NPC的命令
                teleportToNPC(npcId);
                return true;
            }
        }

        // 检查 "完成策略" 卡片的按钮
        if (mouseY >= top + (strategyCardY - this.scrollY) &&
                mouseY <= top + (strategyCardY - this.scrollY) + 35) {

            if (detailCard.isMouseOverButton((int)mouseX, (int)mouseY + this.scrollY - top,
                    x, strategyCardY, contentWidth)) {
                mc.setScreen(new StrategyScreen(mc));
                return true;
            }
        }

        // 检查 "完成情况" 卡片的按钮
        if (mouseY >= top + (summaryCardY - this.scrollY) &&
                mouseY <= top + (summaryCardY - this.scrollY) + 35) {

            if (detailCard.isMouseOverButton((int)mouseX, (int)mouseY + this.scrollY - top,
                    x, summaryCardY, contentWidth)) {
                mc.setScreen(new TaskCompletionScreen(mc));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 执行传送到指定NPC的方法
    private void teleportToNPC(int npcId) {
        if (mc.player != null) {
            mc.player.chat("/findnpc " + npcId);
            System.out.println("Teleporting to NPC: " + npcId);
        }
    }
}