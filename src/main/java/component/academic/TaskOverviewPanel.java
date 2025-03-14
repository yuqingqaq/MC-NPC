package component.adaptive;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;


import java.util.List;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class TaskOverviewPanel extends ScrollPanel {
    private List<String> taskList; // 保存任务的数据
    private Consumer<String> onTaskClick; // 点击任务时的回调函数
    private final Font font;
    private int selectedTaskIndex = -1; // 当前选中的任务索引
    private String selectedTaskTitle; // 当前选中的任务标题

    private int scrollY = 0; // 当前滚动位置
    private int maxScrollY = 0; // 最大滚动位置

    private boolean isExpanded = false; // 是否展开
    private String currentCategory = ""; // 当前分类
    private Map<String, List<String>> categoryTasks = new HashMap<>(); // 分类及其子任务

    public TaskOverviewPanel(Minecraft mc, int width, int height, int top, int left, int border, int barWidth, 
            Map<String, List<String>> categoryTasks, Consumer<String> onTaskClick, String initialTaskTitle) {
        super(mc, width, height, top, left, border, barWidth, 0, 0, 0x00000000, 0x00000000, 0x00000000);
        this.categoryTasks = categoryTasks;
        this.onTaskClick = onTaskClick;
        this.font = mc.font;
        this.selectedTaskTitle = initialTaskTitle;
        // 初始化显示所有分类
        this.taskList = new ArrayList<>(categoryTasks.keySet());
        expandCategoryForTask(initialTaskTitle);
        this.maxScrollY = Math.max(0, getContentHeight() - height); // 初始化最大滚动值
    }

    private void expandCategoryForTask(String taskTitle) {
        for (Map.Entry<String, List<String>> entry : categoryTasks.entrySet()) {
            if (entry.getValue().contains(taskTitle)) {
                currentCategory = entry.getKey();
                isExpanded = true;
                updateTaskList();
                break;
            }
        }
    }

    @Override
    protected int getContentHeight() {
        // 根据任务的数量计算内容总高度，每行20像素
        return taskList.size() * 20;
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        scrollY = this.scrollY;
        int yPos = top - scrollY;

        for (int i = 0; i < taskList.size(); i++) {
            String item = taskList.get(i);
            
            if (yPos + 20 > top && yPos < top + height) {
                int color = (item.equals(selectedTaskTitle)) ? 0xFFFFFF : 0xAAAAAA; // 高亮选中任务为纯白色
                
                // 判断是否为分类项
                if (categoryTasks.containsKey(item)) {
                    // 绘制分类箭头
                    String arrow = isExpanded && item.equals(currentCategory) ? "▼" : "▶";
                    drawString(poseStack, this.font, arrow, left + 5, yPos + 5, color);
                    drawString(poseStack, this.font, item, left + 20, yPos + 5, color);
                } else {
                    // 子任务缩进显示
                    drawString(poseStack, this.font, item, left + 30, yPos + 5, color);
                }
            }
            
            yPos += 20;
        }
        
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
            int yOffset = (int) mouseY - top + scrollY;
            int clickedIndex = yOffset / 20;

            if (clickedIndex >= 0 && clickedIndex < taskList.size()) {
                String clickedItem = taskList.get(clickedIndex);
                
                // 点击分类项时展开/收起
                if (categoryTasks.containsKey(clickedItem)) {
                    if (currentCategory.equals(clickedItem)) {
                        isExpanded = !isExpanded;
                    } else {
                        currentCategory = clickedItem;
                        isExpanded = true;
                    }
                    updateTaskList();
                } else {
                    // 点击子任务时触发回调
                    selectedTaskIndex = clickedIndex;
                    selectedTaskTitle = clickedItem;
                    onTaskClick.accept(clickedItem);
                }
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

    private void updateTaskList() {
        List<String> newList = new ArrayList<>();
        // 添加所有分类
        newList.addAll(categoryTasks.keySet());
        
        // 如果有展开的分类，添加其子任务
        if (isExpanded && !currentCategory.isEmpty()) {
            int insertIndex = newList.indexOf(currentCategory) + 1;
            newList.addAll(insertIndex, categoryTasks.get(currentCategory));
        }
        
        this.taskList = newList;
        this.maxScrollY = Math.max(0, getContentHeight() - height);
    }
}