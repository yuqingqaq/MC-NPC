package gui.academic.knowledge;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import controller.GameController;
import controller.KnowledgeGraphManager;
import controller.KnowledgeTaskMonitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import system.UIScreenManager;

import java.util.List;

public class KnowledgeGraphScreen extends Screen {
    private static final String TITLE = "智能体知识图谱";

    // 界面元素
    private Button closeButton;
    private Button agentDefinitionButton;
    private Button llmsButton;
    private Button toolsButton;
    private Button workflowButton;
    private Button checkTaskButton;

    // 当前选中的类别
    private String selectedCategory = "智能体定义";

    // 布局常量
    private int leftPanelWidth;
    private int rightPanelX;

    // 进度条刷新计时器
    private int updateTimer = 0;

    public KnowledgeGraphScreen() {
        super(new TextComponent(TITLE));
    }

    @Override
    protected void init() {
        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        // 设置左右面板宽度（左侧较窄，右侧较宽）
        leftPanelWidth = this.width / 3 - 20; // 左侧面板宽度约为屏幕的1/3
        rightPanelX = leftPanelWidth + 30;    // 右侧面板起始位置

        // 关闭按钮
        this.closeButton = this.addRenderableWidget(new Button(
                this.width - 30,
                5,
                20,
                20,
                new TextComponent("X"),
                button -> onClose()
        ));

        // 类别选择按钮 - 移到顶部横向排列
        int buttonWidth = 100;
        int buttonsY = 25; // 向上移动按钮

        this.agentDefinitionButton = this.addRenderableWidget(new Button(
                this.width / 5 - buttonWidth / 2 - 15,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("智能体定义"),
                button -> selectCategory("智能体定义")
        ));

        this.llmsButton = this.addRenderableWidget(new Button(
                2 * this.width / 5 - buttonWidth / 2 - 5,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("大型语言模型"),
                button -> selectCategory("大型语言模型")
        ));

        this.toolsButton = this.addRenderableWidget(new Button(
                3 * this.width / 5 - buttonWidth / 2 + 5,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("智能体工具"),
                button -> selectCategory("智能体工具")
        ));

        this.workflowButton = this.addRenderableWidget(new Button(
                4 * this.width / 5 - buttonWidth / 2 + 15,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("智能体工作流"),
                button -> selectCategory("智能体工作流")
        ));

        // 高亮当前选中的类别
        updateButtonStyles();

        // 添加检查任务按钮 - 放在左上角
        this.checkTaskButton = this.addRenderableWidget(new Button(
                20,
                5,
                100,
                20,
                new TextComponent("检查任务"),
                button -> KnowledgeTaskMonitor.getInstance().manualCheck()
        ));
    }

    private void selectCategory(String category) {
        this.selectedCategory = category;
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        // 重置所有按钮样式
        agentDefinitionButton.setMessage(new TextComponent("智能体定义"));
        llmsButton.setMessage(new TextComponent("大型语言模型"));
        toolsButton.setMessage(new TextComponent("智能体工具"));
        workflowButton.setMessage(new TextComponent("智能体工作流"));

        // 高亮选中的按钮
        switch(selectedCategory) {
            case "智能体定义":
                agentDefinitionButton.setMessage(new TextComponent("✓ 智能体定义"));
                break;
            case "大型语言模型":
                llmsButton.setMessage(new TextComponent("✓ 大型语言模型"));
                break;
            case "智能体工具":
                toolsButton.setMessage(new TextComponent("✓ 智能体工具"));
                break;
            case "智能体工作流":
                workflowButton.setMessage(new TextComponent("✓ 智能体工作流"));
                break;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);
    }

    @Override
    public void tick() {
        super.tick();

        // 每10个tick刷新一次进度条数据（约0.5秒）
        updateTimer++;
        if (updateTimer >= 10) {
            updateTimer = 0;
            // 这里不需要做什么，但保留计时器逻辑以备将来需要
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // 绘制标题 - 上移
        drawCenteredString(poseStack, this.font, TITLE, this.width / 2, 5, 0xFFFFFF);

        // 绘制左侧背景面板
        fill(poseStack, 10, 50, leftPanelWidth + 10, this.height - 10, 0x33000000);

        // 绘制右侧背景面板 - 增宽
        fill(poseStack, rightPanelX, 50, this.width - 10, this.height - 10, 0x33000000);

        // 使用原来版本的进度条样式，确保每次都重新获取最新数据
        renderProgressBars(poseStack);

        // 绘制右侧内容区域
        int contentX = rightPanelX + 10;
        int contentY = 60; // 内容上移
        int contentWidth = this.width - contentX - 20;

        // 根据当前选择的类别渲染内容
        switch(selectedCategory) {
            case "智能体定义":
                renderAgentDefinitionView(poseStack, contentX, contentY, contentWidth);
                break;
            case "大型语言模型":
                renderLLMsView(poseStack, contentX, contentY, contentWidth);
                break;
            case "智能体工具":
                renderToolsView(poseStack, contentX, contentY, contentWidth);
                break;
            case "智能体工作流":
                renderWorkflowView(poseStack, contentX, contentY, contentWidth);
                break;
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderProgressBars(PoseStack poseStack) {
        // 每次渲染都从控制器获取最新数据
        GameController controller = GameController.getInstance();

        // 左侧面板标题
        drawString(poseStack, this.font, "知识进度", 20, 60, 0xFFFFAA);

        // 整体进度
        int overallProgress = controller.getKnowledgeGraphCompletion();
        String overallText = "总体进度: " + overallProgress + "%";
        drawString(poseStack, this.font, overallText, 20, 80, 0xFFFFFF);
        renderProgressBar(poseStack, 20, 90, leftPanelWidth - 20, 10, overallProgress);

        // 各领域进度
        String[] areas = {"智能体定义", "大型语言模型", "智能体工具", "智能体工作流"};
        String[] labels = {"智能体定义", "大型语言模型", "智能体工具", "智能体工作流"};

        for (int i = 0; i < areas.length; i++) {
            // 确保每次都重新获取最新进度
            int progress = controller.getKnowledgeAreaCompletion(areas[i]);
            String text = labels[i] + ": " + progress + "%";
            drawString(poseStack, this.font, text, 20, 110 + i * 30, 0xFFFFFF);
            renderProgressBar(poseStack, 20, 120 + i * 30, leftPanelWidth - 20, 10, progress);
        }
    }

    private void renderProgressBar(PoseStack poseStack, int x, int y, int width, int height, int progress) {
        // 绘制背景
        fill(poseStack, x, y, x + width, y + height, 0xFF555555);

        // 绘制进度
        int progressWidth = (int)(width * (progress / 100.0));
        if (progressWidth > 0) {
            fill(poseStack, x, y, x + progressWidth, y + height, 0xFF00FF00);
        }
    }

    // 绘制智能体定义区域
    private void renderAgentDefinitionView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> concepts = manager.getAcquiredConcepts();

        // 标题上移
        drawString(poseStack, this.font, "已获取的智能体概念", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        int availableHeight = this.height - y - 40; // 减去底部提示的高度
        int conceptDisplayLimit = Math.min(concepts.size(), Math.max(1, availableHeight / 25)); // 计算可显示的概念数量

        int itemsPerColumn = 0;
        if (concepts.isEmpty()) {
            drawString(poseStack, this.font, "尚未获取任何智能体概念", x, y, 0xAAAAAA);
        } else {
            // 使用两列布局展示概念，限制显示数量
            itemsPerColumn = (conceptDisplayLimit + 1) / 2;
            int column1X = x;
            int column2X = x + width / 2;

            for (int i = 0; i < conceptDisplayLimit; i++) {
                int itemX = (i < itemsPerColumn) ? column1X : column2X;
                int itemY = y + (i % itemsPerColumn) * 25;

                // 确保项目不会超出底部
                if (itemY + 20 > this.height - 40) {
                    break;
                }

                // 绘制概念条目背景
                String conceptText = concepts.get(i);
                // 截断过长的概念名称
                if (this.font.width("• " + conceptText) > width / 2 - 20) {
                    conceptText = conceptText.substring(0, Math.min(20, conceptText.length())) + "...";
                }

                int textWidth = this.font.width("• " + conceptText) + 10;
                fill(poseStack, itemX - 2, itemY - 2, itemX + textWidth, itemY + 18, 0x33000000);

                // 绘制概念文本
                drawString(poseStack, this.font, "• " + conceptText, itemX + 5, itemY + 5, 0xFFFFFF);
            }

            // 如果概念太多，显示"更多..."
            if (concepts.size() > conceptDisplayLimit) {
                int moreY = y + Math.min(itemsPerColumn, conceptDisplayLimit % itemsPerColumn) * 25;
                if (conceptDisplayLimit >= itemsPerColumn) {
                    drawString(poseStack, this.font, "(还有 " + (concepts.size() - conceptDisplayLimit) + " 个概念...)",
                            column2X, moreY + 5, 0xAAAAAA);
                } else {
                    drawString(poseStack, this.font, "(还有 " + (concepts.size() - conceptDisplayLimit) + " 个概念...)",
                            column1X, moreY + 5, 0xAAAAAA);
                }
            }
        }

        // 计算网络图的位置和大小
        int remainingHeight = this.height - y - 40 - (concepts.isEmpty() ? 20 : Math.min(itemsPerColumn, concepts.size()) * 25);
        if (remainingHeight >= 100) { // 只有当有足够空间时才显示网络图
            int networkY = this.height - 150; // 在底部上方150像素处
            int networkRadius = Math.min(width / 6, remainingHeight / 3); // 自适应半径

            renderKnowledgeNetwork(poseStack, x + width / 2, networkY, networkRadius, concepts.size());
        }

        // 底部提示，使用换行处理
        String tipText = "收集发光的书籍来学习更多智能体概念！";
        drawWrappedText(poseStack, tipText, x, this.height - 30, width, 0xAAAAAA, false);
    }

    // 修改知识网络可视化以适应面板大小
    private void renderKnowledgeNetwork(PoseStack poseStack, int centerX, int centerY, int radius, int nodeCount) {
        // 确保网络图不会太大也不会太小
        radius = Math.min(radius, 80); // 最大半径
        radius = Math.max(radius, 30); // 最小半径

        if (nodeCount == 0) {
            // 如果没有节点，绘制提示信息
            drawCenteredString(poseStack, this.font, "知识网络将在此处显示", centerX, centerY, 0xAAAAAA);
            return;
        }

        // 绘制标题
        drawCenteredString(poseStack, this.font, "知识关联网络", centerX, centerY - radius - 15, 0xFFFFAA);

        // 绘制中心节点
        fill(poseStack, centerX - 8, centerY - 8, centerX + 8, centerY + 8, 0xFFFFDD00);
        drawCenteredString(poseStack, this.font, "AI", centerX, centerY - 3, 0xFF000000);

        // 限制显示的节点数量
        int displayCount = Math.min(nodeCount, 6); // 最多显示6个节点以避免拥挤

        // 绘制外围节点和连接线
        for (int i = 0; i < displayCount; i++) {
            double angle = (Math.PI * 2 * i) / displayCount;
            int nodeX = (int)(centerX + Math.cos(angle) * radius);
            int nodeY = (int)(centerY + Math.sin(angle) * radius);

            // 绘制连接线
            drawLine(poseStack, centerX, centerY, nodeX, nodeY, 0xAAFFFFFF);

            // 绘制节点
            fill(poseStack, nodeX - 5, nodeY - 5, nodeX + 5, nodeY + 5, 0xFF00AAFF);

            // 绘制节点标签 (C1, C2 等)
            String label = "C" + (i+1);
            // 避免标签超出屏幕边界
            if (nodeY - 15 > 60) { // 确保标签在顶部菜单下方
                drawCenteredString(poseStack, this.font, label, nodeX, nodeY - 15, 0xFFFFFF);
            } else {
                drawCenteredString(poseStack, this.font, label, nodeX, nodeY + 15, 0xFFFFFF);
            }
        }
    }

    // 辅助绘制线段的方法
    private void drawLine(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {
        // 简化的线段绘制
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        float x = x1;
        float y = y1;

        for (int i = 0; i <= steps; i++) {
            fill(poseStack, (int)x, (int)y, (int)x + 1, (int)y + 1, color);
            x += xIncrement;
            y += yIncrement;
        }
    }

    // 绘制LLMs视图
    private void renderLLMsView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> relationships = manager.getMasteredRelationships();

        // 标题上移
        drawString(poseStack, this.font, "掌握的大型语言模型特性", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        int availableHeight = this.height - y - 40; // 减去底部提示的高度

        if (relationships.isEmpty()) {
            drawString(poseStack, this.font, "尚未掌握任何LLM特性", x, y, 0xAAAAAA);

            y += 30;
            drawCenteredString(poseStack, this.font, "示例LLM结构:", x + width/2, y, 0xFFFFAA);
            renderLLMExample(poseStack, x, y + 20, width);

        } else {
            // 计算可以显示的关系数量
            int relationshipLimit = Math.min(relationships.size(), Math.max(1, availableHeight / 40));

            // 分两部分：左侧是关系列表，右侧是关系图
            int listWidth = width / 2 - 20;

            // 绘制关系列表标题
            drawString(poseStack, this.font, "已掌握特性:", x, y, 0xFFFFAA);

            y += 15;
            // 绘制关系列表
            for (int i = 0; i < relationshipLimit; i++) {
                String relationship = relationships.get(i);
                int relationY = y + i * 40;

                // 确保项目不会超出底部
                if (relationY + 30 > this.height - 40) {
                    break;
                }

                // 截断过长的关系名称
                if (this.font.width(relationship) > listWidth - 20) {
                    relationship = relationship.substring(0, Math.min(15, relationship.length())) + "...";
                }

                // 绘制关系框背景
                fill(poseStack, x, relationY, x + listWidth, relationY + 30, 0x44000000);

                // 绘制关系名称
                drawString(poseStack, this.font, relationship, x + 10, relationY + 10, 0xFFFFFF);
            }

            // 如果关系太多，显示"更多..."
            if (relationships.size() > relationshipLimit) {
                int moreY = y + relationshipLimit * 40;
                if (moreY + 20 <= this.height - 40) {
                    drawString(poseStack, this.font, "(还有 " + (relationships.size() - relationshipLimit) + " 项...)",
                            x + 10, moreY, 0xAAAAAA);
                }
            }

            // 只有当有足够空间时才显示关系图
            int graphHeight = Math.min(200, availableHeight - 40);
            if (graphHeight >= 100) {
                renderLLMStructure(poseStack, x + listWidth + 30, y + 20, listWidth, relationships, graphHeight);
            }
        }

        // 底部提示，使用换行处理
        String tipText = "使用概念工作台构建更多LLM知识联系！";
        drawWrappedText(poseStack, tipText, x, this.height - 30, width, 0xAAAAAA, false);
    }

    // 绘制LLM示例
    private void renderLLMExample(PoseStack poseStack, int x, int y, int width) {
        int centerX = x + width / 2;

        // 绘制简化的LLM结构图
        int boxY = y + 20;
        int boxWidth = 100;
        int boxHeight = 30;

        // 系统消息框
        fill(poseStack, centerX - boxWidth/2, boxY, centerX + boxWidth/2, boxY + boxHeight, 0x66AA0000);
        drawCenteredString(poseStack, this.font, "系统消息", centerX, boxY + 10, 0xFFFFFF);

        // 用户消息框
        int userBoxY = boxY + boxHeight + 20;
        fill(poseStack, centerX - boxWidth/2, userBoxY, centerX + boxWidth/2, userBoxY + boxHeight, 0x6600AA00);
        drawCenteredString(poseStack, this.font, "用户消息", centerX, userBoxY + 10, 0xFFFFFF);

        // 助手消息框
        int assistantBoxY = userBoxY + boxHeight + 20;
        fill(poseStack, centerX - boxWidth/2, assistantBoxY, centerX + boxWidth/2, assistantBoxY + boxHeight, 0x660000AA);
        drawCenteredString(poseStack, this.font, "助手消息", centerX, assistantBoxY + 10, 0xFFFFFF);

        // 绘制连接线
        drawLine(poseStack, centerX, boxY + boxHeight, centerX, userBoxY, 0xFFFFFFFF);
        drawLine(poseStack, centerX, userBoxY + boxHeight, centerX, assistantBoxY, 0xFFFFFFFF);

        // 绘制说明文本
        drawCenteredString(poseStack, this.font, "LLM使用不同类型的消息构建对话",
                centerX, assistantBoxY + boxHeight + 20, 0xAAAAAA);
    }

    // 绘制LLM结构
    private void renderLLMStructure(PoseStack poseStack, int x, int y, int width, List<String> relationships, int height) {
        // 绘制LLM结构图
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        // 绘制LLM核心
        fill(poseStack, centerX - 40, centerY - 20, centerX + 40, centerY + 20, 0x66AA0000);
        drawCenteredString(poseStack, this.font, "LLM核心", centerX, centerY, 0xFFFFFF);

        // 计算半径
        int radius = Math.min(width / 2 - 30, height / 2 - 30);
        radius = Math.max(radius, 40); // 最小半径

        // 绘制已掌握的特性
        int relationCount = Math.min(relationships.size(), 4); // 最多显示4个
        for (int i = 0; i < relationCount; i++) {
            double angle = (Math.PI * 2 * i) / relationCount;
            int featureX = (int)(centerX + Math.cos(angle) * radius);
            int featureY = (int)(centerY + Math.sin(angle) * radius);

            // 绘制连接线
            drawLine(poseStack, centerX, centerY, featureX, featureY, 0xFFFFFFFF);

            // 特性框
            int boxWidth = 60;
            int boxHeight = 20;
            fill(poseStack, featureX - boxWidth/2, featureY - boxHeight/2,
                    featureX + boxWidth/2, featureY + boxHeight/2, 0x660000AA);

            // 截短特性名称
            String featureName = relationships.get(i);
            if (this.font.width(featureName) > boxWidth - 10) {
                featureName = featureName.substring(0, Math.min(8, featureName.length())) + "...";
            }

            drawCenteredString(poseStack, this.font, featureName, featureX, featureY - 3, 0xFFFFFF);
        }
    }

    // 绘制Tools视图
    private void renderToolsView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> tools = manager.getValidatedPrinciples();

        // 标题上移
        drawString(poseStack, this.font, "已验证的智能体工具原则", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        // 计算可用空间
        int availableHeight = this.height - y - 40; // 减去底部提示的空间
        int toolHeight = 80; // 每个工具卡片的高度
        int displayLimit = Math.max(1, availableHeight / toolHeight);

        if (tools.isEmpty()) {
            drawString(poseStack, this.font, "尚未验证任何工具原则", x, y, 0xAAAAAA);

            // 添加工具示例图
            y += 30;
            renderToolExample(poseStack, x, y, width);
        } else {
            for (int i = 0; i < Math.min(tools.size(), displayLimit); i++) {
                // 为每个工具创建一个卡片样式的展示
                int cardY = y + i * toolHeight;

                // 卡片背景
                fill(poseStack, x, cardY, x + width - 10, cardY + 70, 0x44000000);

                // 工具编号和标题
                drawString(poseStack, this.font, "原则 " + (i+1) + ":", x + 10, cardY + 10, 0xFFFFAA);

                // 截断过长的工具名称
                String toolName = tools.get(i);
                if (this.font.width(toolName) > width - 120) {
                    toolName = toolName.substring(0, Math.min(25, toolName.length())) + "...";
                }
                drawString(poseStack, this.font, toolName, x + 100, cardY + 10, 0xFFFFFF);

                // 分隔线
                fill(poseStack, x + 10, cardY + 25, x + width - 20, cardY + 26, 0x44FFFFFF);

                // 工具描述
                String description = "该原则定义了智能体工具的基本行为和使用方式。";
                drawString(poseStack, this.font, description, x + 10, cardY + 35, 0xCCCCCC);

                // 工具状态
                drawString(poseStack, this.font, "状态: ", x + 10, cardY + 55, 0xCCCCCC);
                drawString(poseStack, this.font, "已验证 ✓", x + 60, cardY + 55, 0xFF00FF00);
            }

            // 如果工具太多，显示"更多..."
            if (tools.size() > displayLimit) {
                int moreY = y + displayLimit * toolHeight;
                drawString(poseStack, this.font, "(还有 " + (tools.size() - displayLimit) + " 个原则...)",
                        x + 10, moreY + 5, 0xAAAAAA);
            }
        }

        // 底部提示，使用换行处理
        String tipText = "激活验证设备来测试你的工具知识！";
        drawWrappedText(poseStack, tipText, x, this.height - 30, width, 0xAAAAAA, false);
    }

    // 绘制工具示例
    private void renderToolExample(PoseStack poseStack, int x, int y, int width) {
        int centerX = x + width / 2;

        // 绘制工具流程图
        // LLM框
        int boxY = y + 20;
        int boxWidth = 80;
        int boxHeight = 30;
        fill(poseStack, centerX - 100 - boxWidth/2, boxY, centerX - 100 + boxWidth/2, boxY + boxHeight, 0x66AA0000);
        drawCenteredString(poseStack, this.font, "LLM", centerX - 100, boxY + 10, 0xFFFFFF);

        // 工具框
        fill(poseStack, centerX + 100 - boxWidth/2, boxY, centerX + 100 + boxWidth/2, boxY + boxHeight, 0x660000AA);
        drawCenteredString(poseStack, this.font, "工具", centerX + 100, boxY + 10, 0xFFFFFF);

        // 箭头1：LLM -> 工具
        drawLine(poseStack, centerX - 100 + boxWidth/2, boxY + boxHeight/2,
                centerX + 100 - boxWidth/2, boxY + boxHeight/2, 0xFFFFFFFF);
        drawCenteredString(poseStack, this.font, "调用", centerX, boxY + boxHeight/2 - 10, 0xFFFFAA);

        // 箭头2：工具 -> LLM（返回结果）
        int arrowY = boxY + boxHeight + 20;
        drawLine(poseStack, centerX + 100, boxY + boxHeight,
                centerX + 100, arrowY, 0xFFFFFFFF);
        drawLine(poseStack, centerX + 100, arrowY,
                centerX - 100, arrowY, 0xFFFFFFFF);
        drawLine(poseStack, centerX - 100, arrowY,
                centerX - 100, boxY + boxHeight, 0xFFFFFFFF);
        drawCenteredString(poseStack, this.font, "返回结果", centerX, arrowY + 10, 0xFFFFAA);

        // 描述
        drawCenteredString(poseStack, this.font, "工具通过补充LLM能力扩展智能体功能",
                centerX, arrowY + 40, 0xAAAAAA);
    }

    // 绘制Workflow视图
    private void renderWorkflowView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> workflows = manager.getMasteredTimelines();

        // 标题上移
        drawString(poseStack, this.font, "智能体工作流程", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        if (workflows.isEmpty()) {
            drawString(poseStack, this.font, "尚未掌握任何工作流程", x, y, 0xAAAAAA);

            // 显示示例工作流程
            y += 30;
            drawCenteredString(poseStack, this.font, "示例工作流程结构:", x + width/2, y - 10, 0xFFFFAA);
            renderWorkflowExample(poseStack, x, y + 20, width);
        } else {
            // 显示工作流程列表
            for (int i = 0; i < workflows.size(); i++) {
                drawString(poseStack, this.font, "• " + workflows.get(i), x, y + i * 25, 0xFFFFFF);
            }

            // 工作流程可视化
            y += Math.max(60, workflows.size() * 25 + 10);
            renderWorkflowVisualization(poseStack, x, y, width, workflows);
        }

        // 底部提示
        String tipText = "访问时间走廊来掌握更多工作流程步骤！";
        fill(poseStack, x, this.height - 30, x + width - 10, this.height - 10, 0x33000000);
        drawString(poseStack, this.font, tipText, x + 10, this.height - 25, 0xAAAAAA);
    }

    // 绘制工作流程示例
    private void renderWorkflowExample(PoseStack poseStack, int x, int y, int width) {
        int centerX = x + width / 2;

        // 绘制工作流循环图
        String[] steps = {"思考", "行动", "观察"};
        int radius = 80;
        int centerY = y + radius + 20;

        // 绘制中心点
        fill(poseStack, centerX - 5, centerY - 5, centerX + 5, centerY + 5, 0xFFFFFFFF);

        // 绘制三个步骤点和连接线
        for (int i = 0; i < 3; i++) {
            double angle = (Math.PI * 2 * i) / 3 - Math.PI / 2; // 从顶部开始
            int stepX = (int)(centerX + Math.cos(angle) * radius);
            int stepY = (int)(centerY + Math.sin(angle) * radius);

            // 绘制步骤点
            fill(poseStack, stepX - 30, stepY - 15, stepX + 30, stepY + 15, 0x66000000 | (0x0000FF << (8*i)));
            drawCenteredString(poseStack, this.font, steps[i], stepX, stepY, 0xFFFFFF);

            // 绘制到中心的连接线
            drawLine(poseStack, centerX, centerY, stepX, stepY, 0xAAFFFFFF);

            // 绘制步骤间的弧线（简化为直线）
            int nextI = (i + 1) % 3;
            double nextAngle = (Math.PI * 2 * nextI) / 3 - Math.PI / 2;
            int nextX = (int)(centerX + Math.cos(nextAngle) * radius);
            int nextY = (int)(centerY + Math.sin(nextAngle) * radius);

            drawLine(poseStack, stepX, stepY, nextX, nextY, 0xAAFFFFFF);
        }

        // 绘制说明
        drawCenteredString(poseStack, this.font, "智能体通过思考-行动-观察循环解决问题",
                centerX, centerY + radius + 20, 0xAAAAAA);
    }

    // 绘制工作流程可视化
    private void renderWorkflowVisualization(PoseStack poseStack, int x, int y, int width, List<String> workflows) {
        drawString(poseStack, this.font, "工作流程细节:", x, y, 0xFFFFAA);

        y += 20;

        // 工作流程容器
        fill(poseStack, x, y, x + width - 10, y + 180, 0x22FFFFFF);

        // 绘制ReAct流程详情
        // 思考步骤
        int stepY = y + 20;
        fill(poseStack, x + 10, stepY, x + width - 20, stepY + 30, 0x44AA0000);
        drawCenteredString(poseStack, this.font, "1. 思考(Thought)", x + width/2, stepY + 10, 0xFFFFFF);
        drawString(poseStack, this.font, "• 分析当前状态并规划下一步", x + 20, stepY + 40, 0xCCCCCC);

        // 箭头
        int arrowY = stepY + 60;
        drawCenteredString(poseStack, this.font, "↓", x + width/2, arrowY, 0xFFFFFF);

        // 行动步骤
        int actionY = arrowY + 10;
        fill(poseStack, x + 10, actionY, x + width - 20, actionY + 30, 0x4400AA00);
        drawCenteredString(poseStack, this.font, "2. 行动(Action)", x + width/2, actionY + 10, 0xFFFFFF);
        drawString(poseStack, this.font, "• 执行选定的操作或工具调用", x + 20, actionY + 40, 0xCCCCCC);

        // 箭头
        int arrow2Y = actionY + 60;
        drawCenteredString(poseStack, this.font, "↓", x + width/2, arrow2Y, 0xFFFFFF);

        // 观察步骤
        int observeY = arrow2Y + 10;
        fill(poseStack, x + 10, observeY, x + width - 20, observeY + 30, 0x440000AA);
        drawCenteredString(poseStack, this.font, "3. 观察(Observation)", x + width/2, observeY + 10, 0xFFFFFF);
        drawString(poseStack, this.font, "• 获取行动结果并整合反馈", x + 20, observeY + 40, 0xCCCCCC);

        // 循环箭头回到顶部
        drawString(poseStack, this.font, "循环直到目标完成", x + width - 150, observeY + 40, 0xFFFFAA);
    }

    // 绘制带换行功能的文本
    private void drawWrappedText(PoseStack poseStack, String text, int x, int y, int maxWidth, int color, boolean isWhite) {
        List<ColoredText> lines = TextUtils.wrapText(text, maxWidth, isWhite);
        for (int i = 0; i < lines.size(); i++) {
            ColoredText line = lines.get(i);
            drawString(poseStack, this.font, line.text, x, y + i * 12, line.color);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}