package component.academic;
import com.mojang.blaze3d.vertex.PoseStack;

import model.SubTaskModel;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import system.TaskManager;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;

public class TaskPlanningPanel extends AbstractWidget implements Widget {
    private final List<SubTaskModel> subTasks; // 子任务列表
    private final List<Button> taskButtons; // 每个子任务对应的按钮
    private final int panelWidth;
    private final int panelHeight;

    private int draggingTaskIndex = -1; // 当前正在拖动的子任务索引
    private int dragY = 0; // 当前拖动的 Y 坐标
    private Button startButton; // 开始行动按钮

    public TaskPlanningPanel(int x, int y, int width, int height, List<SubTaskModel> initialSubTasks) {
        super(x, y, width, height, new TextComponent("Task Planning Panel"));
        this.subTasks = new ArrayList<>(initialSubTasks); // 初始化子任务列表
        this.taskButtons = new ArrayList<>();
        this.panelWidth = width;
        this.panelHeight = height;
        init();
    }

    // 初始化子任务按钮和开始行动按钮
    private void init() {
        int buttonHeight = 20; // 每个子任务按钮的高度
        int buttonSpacing = 5; // 按钮之间的间距
        int startY = this.y + 20; // 第一个按钮的起始 Y 坐标

        taskButtons.clear(); // 清空按钮列表

        for (int i = 0; i < subTasks.size(); i++) {
            final int index = i;  // 创建一个 final 变量
            int buttonY = startY + i * (buttonHeight + buttonSpacing);
            SubTaskModel subTask = subTasks.get(i);

            // 创建子任务按钮
            Button button = new Button(this.x + 10, buttonY, this.panelWidth - 20, buttonHeight,
                new TextComponent(subTask.getTitle() + " (" + subTask.getEstimatedTime() + ")"),
                btn -> draggingTaskIndex = index  // 使用 final 变量
            );

            taskButtons.add(button);
        }

        // 创建开始行动按钮
        this.startButton = new Button(this.x + 10, startY + subTasks.size() * (buttonHeight + buttonSpacing) + 20, this.panelWidth - 20, buttonHeight,
                new TextComponent("开始行动"), btn -> startTask());
    }

    // 开始行动的逻辑
    private void startTask() {
        // 获取排序后的第一个子任务
        if (!subTasks.isEmpty()) {
            SubTaskModel selectedSubTask = subTasks.get(0); // 获取第一个子任务
            // 这里可以调用 TaskManager 的方法来开始任务
            TaskManager.getInstance().startSubTask(selectedSubTask); // 假设 startTask 方法可以处理 SubTaskModel
            System.out.println("开始子任务: " + selectedSubTask.getTitle());
        }
    }

    // 计算鼠标释放时的目标索引
    private int calculateTargetIndex(int mouseY) {
        int startY = this.y + 10; // 第一个按钮的起始 Y 坐标
        int buttonHeight = 20; // 按钮高度
        int buttonSpacing = 5; // 按钮间距
        int totalHeight = buttonHeight + buttonSpacing; // 每个按钮占用的总高度

        int relativeY = mouseY - startY;
        return Math.min(subTasks.size() - 1, Math.max(0, relativeY / totalHeight));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingTaskIndex != -1) {
            dragY = (int) mouseY; // 更新拖动的 Y 坐标
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingTaskIndex != -1) {
            // 鼠标释放时，计算目标索引，并重新排序任务
            int targetIndex = calculateTargetIndex((int) mouseY);
            SubTaskModel draggedSubTask = subTasks.remove(draggingTaskIndex); // 移除拖动的子任务
            subTasks.add(targetIndex, draggedSubTask); // 插入到目标位置

            // 重置拖动状态
            draggingTaskIndex = -1;
            dragY = 0;

            // 重新初始化按钮
            init();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染背景面板
        fill(poseStack, this.x, this.y, this.x + this.panelWidth, this.y + this.panelHeight, 0x33000000); // 半透明背景

        // 渲染标题
        Minecraft.getInstance().font.draw(
            poseStack,
            "拖动子任务以排序",
            this.x + 10,
            this.y + 5,
            0xFFFFFF
        );

        // 渲染子任务按钮
        for (int i = 0; i < taskButtons.size(); i++) {
            if (i == draggingTaskIndex) continue; // 跳过正在拖动的子任务
            taskButtons.get(i).render(poseStack, mouseX, mouseY, partialTicks);
        }

        // 渲染开始行动按钮
        startButton.render(poseStack, mouseX, mouseY, partialTicks);

        // 渲染拖动的子任务
        if (draggingTaskIndex != -1) {
            SubTaskModel draggingSubTask = subTasks.get(draggingTaskIndex);

            // 获取文字宽度
            int textWidth = Minecraft.getInstance().font.width(draggingSubTask.getTitle() + " (" + draggingSubTask.getEstimatedTime() + ")");

            // 调整拖动子任务文字的渲染位置，使其显示在鼠标左侧
            Minecraft.getInstance().font.draw(
                poseStack,
                draggingSubTask.getTitle() + " (" + draggingSubTask.getEstimatedTime() + ")",
                mouseX - textWidth - 5, // 将文字向左偏移文字宽度和额外的 5 像素
                mouseY - 5, // 文字相对于鼠标位置的 Y 坐标
                0xFFFFFF // 白色文字
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否有按钮被点击
        for (Button taskButton : taskButtons) {
            if (taskButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return startButton.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        // 暂时不需要实现旁白支持
    }
}