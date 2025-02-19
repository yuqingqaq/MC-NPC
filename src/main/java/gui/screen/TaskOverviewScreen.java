package gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import controller.GameController;
import model.AcademicTaskModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskOverviewScreen extends Screen {

    private List<AcademicTaskModel> academicTasks; // 所有任务数据
    private AcademicTaskModel currentTask; // 当前选中的任务
    private Map<String, List<AcademicTaskModel>> categorizedTasks = new HashMap<>(); // 按类别分组的任务
    private Map<String, Boolean> expandedCategories = new HashMap<>(); // 记录每个类别是否展开

    public TaskOverviewScreen() {
        super(new TextComponent("任务总览"));
    }

    @Override
    protected void init() {
        // 获取任务列表
        academicTasks = GameController.getInstance().getAcademicTasks();

        // 按类别分组任务
        for (AcademicTaskModel task : academicTasks) {
            categorizedTasks.computeIfAbsent(task.getCategory(), k -> new ArrayList<>()).add(task);
            expandedCategories.putIfAbsent(task.getCategory(), false); // 默认所有类别收起
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        int x = 20; // 左侧起始 x 坐标
        int y = 40; // 左侧起始 y 坐标

        // 渲染左侧任务类别和任务列表
        for (Map.Entry<String, List<AcademicTaskModel>> entry : categorizedTasks.entrySet()) {
            String category = entry.getKey();
            List<AcademicTaskModel> tasks = entry.getValue();

            // 渲染类别标题
            boolean isExpanded = expandedCategories.get(category);
            drawString(poseStack, this.font, (isExpanded ? "▼ " : "▶ ") + category, x, y, 0xFFFFFF);
            y += 20;

            // 如果类别展开，渲染该类别下的任务
            if (isExpanded) {
                for (AcademicTaskModel task : tasks) {
                    drawString(poseStack, this.font, "- " + task.getTitle(), x + 20, y, 0xFFFFFF);
                    y += 20;
                }
            }
        }

        // 渲染右侧任务详情
        if (currentTask != null) {
            renderTaskDetails(poseStack, currentTask);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderTaskDetails(PoseStack poseStack, AcademicTaskModel task) {
        int x = this.width / 2 + 20; // 右侧任务详情起始 x 坐标
        int y = 40; // 右侧任务详情起始 y 坐标

        drawString(poseStack, this.font, "任务详情", x, y, 0xFFFFFF);
        drawString(poseStack, this.font, "标题: " + task.getTitle(), x, y + 20, 0xFFFFFF);
        drawString(poseStack, this.font, "目标: " + task.getTarget(), x, y + 40, 0xFFFFFF);
        drawString(poseStack, this.font, "资源: " + String.join(", ", task.getResources()), x, y + 60, 0xFFFFFF);
        drawString(poseStack, this.font, "地点: " + task.getLocation(), x, y + 80, 0xFFFFFF);
        drawString(poseStack, this.font, "时间: " + task.getEstimatedTime(), x, y + 100, 0xFFFFFF);
        drawString(poseStack, this.font, "策略: " + String.join(", ", task.getStrategies()), x, y + 120, 0xFFFFFF);

        // 渲染总结
        drawString(poseStack, this.font, "任务总结：", x, y + 140, 0xFFFFFF);
        for (Map.Entry<String, String> entry : task.getSummary().entrySet()) {
            y += 20;
            drawString(poseStack, this.font, entry.getKey() + ": " + entry.getValue(), x, y + 140, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = 20; // 左侧起始 x 坐标
        int y = 40; // 左侧起始 y 坐标

        for (Map.Entry<String, List<AcademicTaskModel>> entry : categorizedTasks.entrySet()) {
            String category = entry.getKey();
            List<AcademicTaskModel> tasks = entry.getValue();

            // 检查是否点击了类别标题
            if (mouseX >= x && mouseX <= x + 100 && mouseY >= y && mouseY <= y + 10) {
                expandedCategories.put(category, !expandedCategories.get(category)); // 切换展开状态
                return true;
            }
            y += 20;

            // 如果类别展开，检查任务点击
            if (expandedCategories.get(category)) {
                for (AcademicTaskModel task : tasks) {
                    if (mouseX >= x + 20 && mouseX <= x + 120 && mouseY >= y && mouseY <= y + 10) {
                        currentTask = task; // 设置当前选中的任务
                        return true;
                    }
                    y += 20;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}