package component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.List;
import java.util.function.Consumer;

public class TaskScrollPanel extends ScrollPanel {
    private List<String> taskList; // 保存任务的数据
    private Consumer<String> onTaskClick; // 点击任务时的回调函数
    private final Font font;
    private int selectedTaskIndex = -1; // 当前选中的任务索引

    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置

    public TaskScrollPanel(Minecraft mc, int width, int height, int top, int left, int border, int barWidth, List<String> taskList, Consumer<String> onTaskClick) {
        super(mc, width, height, top, left, border, barWidth, 0, 0, 0x00000000, 0x00000000, 0x00000000); // 添加背景、边框颜色
        this.taskList = taskList;
        this.onTaskClick = onTaskClick;
        this.font = mc.font; // 获取字体渲染器实例
        this.maxScrollY = Math.max(0, getContentHeight() - height); // 初始化最大滚动值
    }

    @Override
    protected int getContentHeight() {
        // 根据任务的数量计算内容总高度，每行20像素
        return taskList.size() * 20;
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        scrollY = this.scrollY; // 使用当前滚动位置
        int yPos = top - scrollY; // 起始绘制任务的 Y 坐标

        for (int i = 0; i < taskList.size(); i++) {
            String task = taskList.get(i);

            // 如果任务在当前可见范围，则渲染
            if (yPos + 20 > top && yPos < top + height) {
                int color = (i == selectedTaskIndex) ? 0xFFFFFF : 0xAAAAAA; // 当前选中任务高亮显示
                //fill(poseStack, left, yPos, left + width, yPos + 20, 0xFF333333); // 绘制任务项的背景
                drawString(poseStack, this.font, task, left + 5, yPos + 5, color); // 绘制任务文本
            }

            yPos += 20; // 下一个任务的 Y 坐标
        }

        // 绘制滚动条
        drawScrollbar(poseStack);
    }

    private void drawScrollbar(PoseStack poseStack) {
        int scrollbarHeight = Math.max(10, (int) ((float) height * (height / (float) getContentHeight())));
        int scrollbarTop = top + (int) ((float) scrollY / maxScrollY * (height - scrollbarHeight));
        int scrollbarRight = left + width + 6; // 滚动条右侧位置
        int scrollbarLeft = scrollbarRight - 6;

        fill(poseStack, scrollbarLeft, scrollbarTop, scrollbarRight, scrollbarTop + scrollbarHeight, 0xFFAAAAAA); // 绘制滚动条
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        int delta = (int) (scroll * getScrollAmount());
        if (scrollY + delta >= 0 && scrollY + delta <= maxScrollY) {
            scrollY += delta;
        } else if (scrollY + delta < 0) {
            scrollY = 0;
        } else if (scrollY + delta > maxScrollY) {
            scrollY = maxScrollY;
        }
        return true; // 表示事件已处理
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            // 计算点击的任务索引
            int yOffset = (int) mouseY - top + scrollY;
            int clickedIndex = yOffset / 20;

            if (clickedIndex >= 0 && clickedIndex < taskList.size()) {
                selectedTaskIndex = clickedIndex; // 更新选中任务索引
                onTaskClick.accept(taskList.get(clickedIndex)); // 触发点击回调
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // 实现辅助功能描述
    }
}