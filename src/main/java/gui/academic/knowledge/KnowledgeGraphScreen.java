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
    private static final String TITLE = "Agent Knowledge Graph";

    // 界面元素
    private Button closeButton;
    private Button conceptsButton;
    private Button relationshipsButton;
    private Button principlesButton;
    private Button timelinesButton;
    private Button checkTaskButton;

    // 当前选中的类别
    private String selectedCategory = "Concepts";

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

        this.conceptsButton = this.addRenderableWidget(new Button(
                this.width / 5 - buttonWidth / 2 - 15,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("Concepts"),
                button -> selectCategory("Concepts")
        ));

        this.relationshipsButton = this.addRenderableWidget(new Button(
                2 * this.width / 5 - buttonWidth / 2 - 5,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("Relationships"),
                button -> selectCategory("Relationships")
        ));

        this.principlesButton = this.addRenderableWidget(new Button(
                3 * this.width / 5 - buttonWidth / 2 + 5,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("Principles"),
                button -> selectCategory("Principles")
        ));

        this.timelinesButton = this.addRenderableWidget(new Button(
                4 * this.width / 5 - buttonWidth / 2 + 15,
                buttonsY,
                buttonWidth,
                20,
                new TextComponent("Timelines"),
                button -> selectCategory("Timelines")
        ));

        // 高亮当前选中的类别
        updateButtonStyles();

        // 添加检查任务按钮 - 放在左上角
        this.checkTaskButton = this.addRenderableWidget(new Button(
                20,
                5,
                100,
                20,
                new TextComponent("Check Task"),
                button -> KnowledgeTaskMonitor.getInstance().manualCheck()
        ));
    }

    private void selectCategory(String category) {
        this.selectedCategory = category;
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        // 重置所有按钮样式
        conceptsButton.setMessage(new TextComponent("Concepts"));
        relationshipsButton.setMessage(new TextComponent("Relationships"));
        principlesButton.setMessage(new TextComponent("Principles"));
        timelinesButton.setMessage(new TextComponent("Timelines"));

        // 高亮选中的按钮
        switch(selectedCategory) {
            case "Concepts":
                conceptsButton.setMessage(new TextComponent("✓ Concepts"));
                break;
            case "Relationships":
                relationshipsButton.setMessage(new TextComponent("✓ Relationships"));
                break;
            case "Principles":
                principlesButton.setMessage(new TextComponent("✓ Principles"));
                break;
            case "Timelines":
                timelinesButton.setMessage(new TextComponent("✓ Timelines"));
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
            case "Concepts":
                renderConceptsView(poseStack, contentX, contentY, contentWidth);
                break;
            case "Relationships":
                renderRelationshipsView(poseStack, contentX, contentY, contentWidth);
                break;
            case "Principles":
                renderPrinciplesView(poseStack, contentX, contentY, contentWidth);
                break;
            case "Timelines":
                renderTimelinesView(poseStack, contentX, contentY, contentWidth);
                break;
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderProgressBars(PoseStack poseStack) {
        // 每次渲染都从控制器获取最新数据
        GameController controller = GameController.getInstance();

        // 左侧面板标题
        drawString(poseStack, this.font, "Knowledge Progress", 20, 60, 0xFFFFAA);

        // 整体进度
        int overallProgress = controller.getKnowledgeGraphCompletion();
        String overallText = "Overall Progress: " + overallProgress + "%";
        drawString(poseStack, this.font, overallText, 20, 80, 0xFFFFFF);
        renderProgressBar(poseStack, 20, 90, leftPanelWidth - 20, 10, overallProgress);

        // 各领域进度
        String[] areas = {"基础概念", "关系理解", "原则验证", "发展时间线"};
        String[] labels = {"Concepts", "Relationships", "Principles", "Timelines"};

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

    // 绘制概念区域时保证内容不超出面板
    private void renderConceptsView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> concepts = manager.getAcquiredConcepts();

        // 标题上移
        drawString(poseStack, this.font, "Acquired Agent Concepts", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        int availableHeight = this.height - y - 40; // 减去底部提示的高度
        int conceptDisplayLimit = Math.min(concepts.size(), Math.max(1, availableHeight / 25)); // 计算可显示的概念数量

        int itemsPerColumn = 0;
        if (concepts.isEmpty()) {
            drawString(poseStack, this.font, "No concepts acquired yet", x, y, 0xAAAAAA);
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
                    drawString(poseStack, this.font, "(+" + (concepts.size() - conceptDisplayLimit) + " more...)",
                            column2X, moreY + 5, 0xAAAAAA);
                } else {
                    drawString(poseStack, this.font, "(+" + (concepts.size() - conceptDisplayLimit) + " more...)",
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
        String tipText = "Collect glowing books to learn more concepts!";
        drawWrappedText(poseStack, tipText, x, this.height - 30, width, 0xAAAAAA, false);
    }

    // 修改知识网络可视化以适应面板大小
    private void renderKnowledgeNetwork(PoseStack poseStack, int centerX, int centerY, int radius, int nodeCount) {
        // 确保网络图不会太大也不会太小
        radius = Math.min(radius, 80); // 最大半径
        radius = Math.max(radius, 30); // 最小半径

        if (nodeCount == 0) {
            // 如果没有节点，绘制提示信息
            drawCenteredString(poseStack, this.font, "Knowledge network will appear here", centerX, centerY, 0xAAAAAA);
            return;
        }

        // 绘制标题
        drawCenteredString(poseStack, this.font, "Knowledge Network", centerX, centerY - radius - 15, 0xFFFFAA);

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

    // 绘制带换行功能的文本
    private void drawWrappedText(PoseStack poseStack, String text, int x, int y, int maxWidth, int color, boolean isWhite) {
        List<ColoredText> lines = TextUtils.wrapText(text, maxWidth, isWhite);
        for (int i = 0; i < lines.size(); i++) {
            ColoredText line = lines.get(i);
            drawString(poseStack, this.font, line.text, x, y + i * 12, line.color);
        }
    }

    // 同样为关系视图添加自适应功能
    private void renderRelationshipsView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> relationships = manager.getMasteredRelationships();

        // 标题上移
        drawString(poseStack, this.font, "Mastered Relationship Categories", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        int availableHeight = this.height - y - 40; // 减去底部提示的高度

        if (relationships.isEmpty()) {
            drawString(poseStack, this.font, "No relationship categories mastered yet", x, y, 0xAAAAAA);

            y += 30;
            drawCenteredString(poseStack, this.font, "Example Relationship Structure:", x + width/2, y, 0xFFFFAA);
            renderRelationshipExample(poseStack, x, y + 20, width);

        } else {
            // 计算可以显示的关系数量
            int relationshipLimit = Math.min(relationships.size(), Math.max(1, availableHeight / 40));

            // 分两部分：左侧是关系列表，右侧是关系图
            int listWidth = width / 2 - 20;

            // 绘制关系列表标题
            drawString(poseStack, this.font, "Categories:", x, y, 0xFFFFAA);

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
                    drawString(poseStack, this.font, "(+" + (relationships.size() - relationshipLimit) + " more...)",
                            x + 10, moreY, 0xAAAAAA);
                }
            }

            // 只有当有足够空间时才显示关系图
            int graphHeight = Math.min(200, availableHeight - 40);
            if (graphHeight >= 100) {
                renderRelationshipGraph(poseStack, x + listWidth + 30, y + 20, listWidth, relationships, graphHeight);
            }
        }

        // 底部提示，使用换行处理
        String tipText = "Use the Concept Workbench to build more connections!";
        drawWrappedText(poseStack, tipText, x, this.height - 30, width, 0xAAAAAA, false);
    }


    // 绘制关系图示例
    private void renderRelationshipExample(PoseStack poseStack, int x, int y, int width) {
        int centerX = x + width / 2;

        // 绘制两个示例概念框
        int box1X = centerX - 100;
        int box2X = centerX + 20;
        int boxY = y + 20;
        int boxWidth = 80;
        int boxHeight = 30;

        // 第一个概念框
        fill(poseStack, box1X, boxY, box1X + boxWidth, boxY + boxHeight, 0x6600AA00);
        drawCenteredString(poseStack, this.font, "Concept A", box1X + boxWidth/2, boxY + 10, 0xFFFFFF);

        // 第二个概念框
        fill(poseStack, box2X, boxY, box2X + boxWidth, boxY + boxHeight, 0x6600AA00);
        drawCenteredString(poseStack, this.font, "Concept B", box2X + boxWidth/2, boxY + 10, 0xFFFFFF);

        // 绘制连接线
        drawLine(poseStack, box1X + boxWidth, boxY + boxHeight/2, box2X, boxY + boxHeight/2, 0xFFFFFFFF);

        // 绘制关系标签背景
        int labelX = centerX - 40;
        int labelY = boxY - 15;
        int labelWidth = 80;
        int labelHeight = 20;
        fill(poseStack, labelX, labelY, labelX + labelWidth, labelY + labelHeight, 0x660000AA);

        // 绘制关系标签
        drawCenteredString(poseStack, this.font, "Relates To", centerX, labelY + 6, 0xFFFFFF);

        // 绘制说明文本
        drawCenteredString(poseStack, this.font, "Relationships connect different concepts in meaningful ways",
                centerX, boxY + boxHeight + 30, 0xAAAAAA);
    }

    // 2. 修复关系图绘制函数，移除重复的标题
    private void renderRelationshipGraph(PoseStack poseStack, int x, int y, int width, List<String> relationships, int height) {
        // 确定绘制几个关系示例
        int relationCount = Math.min(relationships.size(), 3); // 最多绘制3个关系

        if (relationCount > 0) {
            // 移除重复的标题，因为已经在主函数中添加了

            int graphY = y;

            // 画一个框表示知识图谱的边界
            fill(poseStack, x, graphY, x + width, graphY + height, 0x22FFFFFF);

            // 绘制中心概念
            int centerX = x + width / 2;
            int centerY = graphY + height / 2;

            // 中心概念圆
            fill(poseStack, centerX - 25, centerY - 12, centerX + 25, centerY + 12, 0x660000AA);
            drawCenteredString(poseStack, this.font, "Agent", centerX, centerY, 0xFFFFFF);

            // 计算适当的半径
            int radius = Math.min(width / 2 - 30, height / 2 - 30);
            radius = Math.max(radius, 40); // 最小半径

            // 绘制放射状的关系和相关概念
            for (int i = 0; i < relationCount; i++) {
                double angle = (Math.PI * 2 * i) / relationCount;

                int relatedX = (int)(centerX + Math.cos(angle) * radius);
                int relatedY = (int)(centerY + Math.sin(angle) * radius);

                // 绘制关系线
                drawLine(poseStack, centerX, centerY, relatedX, relatedY, 0xFFFFFFFF);

                // 绘制关系概念框
                int boxWidth = 60;
                int boxHeight = 20;
                fill(poseStack, relatedX - boxWidth/2, relatedY - boxHeight/2,
                        relatedX + boxWidth/2, relatedY + boxHeight/2, 0x6600AA00);

                // 截短关系名称以适合框
                String relationName = relationships.get(i);
                if (this.font.width(relationName) > boxWidth - 10) {
                    relationName = relationName.substring(0, Math.min(8, relationName.length())) + "...";
                }

                drawCenteredString(poseStack, this.font, relationName,
                        relatedX, relatedY - 3, 0xFFFFFF);

                // 绘制关系类型标签（仅当有足够空间时）
                if (radius > 60) {
                    int midX = (centerX + relatedX) / 2;
                    int midY = (centerY + relatedY) / 2;

                    // 调整标签位置以避免与线重叠
                    double perpAngle = angle + Math.PI/2;
                    int labelOffset = 10;
                    int labelX = (int)(midX + Math.cos(perpAngle) * labelOffset);
                    int labelY = (int)(midY + Math.sin(perpAngle) * labelOffset);

                    fill(poseStack, labelX - 20, labelY - 10, labelX + 20, labelY + 10, 0x88000000);
                    drawCenteredString(poseStack, this.font, "Type " + (i+1), labelX, labelY, 0xFFFFAA);
                }
            }
        }
    }



    // 3. 修复原则视图中状态和内容重叠的问题
    private void renderPrinciplesView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> principles = manager.getValidatedPrinciples();

        // 标题上移
        drawString(poseStack, this.font, "Validated Agent Principles", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        // 计算可用空间
        int availableHeight = this.height - y - 40; // 减去底部提示的空间
        int principleHeight = 80; // 增加每个原则卡片的高度，避免内容重叠
        int displayLimit = Math.max(1, availableHeight / principleHeight);

        if (principles.isEmpty()) {
            drawString(poseStack, this.font, "No principles validated yet", x, y, 0xAAAAAA);
        } else {
            for (int i = 0; i < Math.min(principles.size(), displayLimit); i++) {
                // 为每个原则创建一个卡片样式的展示
                int cardY = y + i * principleHeight;

                // 卡片背景
                fill(poseStack, x, cardY, x + width - 10, cardY + 70, 0x44000000);

                // 原则编号和标题
                drawString(poseStack, this.font, "Principle " + (i+1) + ":", x + 10, cardY + 10, 0xFFFFAA);

                // 截断过长的原则名称
                String principleName = principles.get(i);
                if (this.font.width(principleName) > width - 120) {
                    principleName = principleName.substring(0, Math.min(25, principleName.length())) + "...";
                }
                drawString(poseStack, this.font, principleName, x + 100, cardY + 10, 0xFFFFFF);

                // 分隔线
                fill(poseStack, x + 10, cardY + 25, x + width - 20, cardY + 26, 0x44FFFFFF);

                // 原则描述（减少描述长度，确保不会与状态重叠）
                String description = "This principle defines fundamental behaviors for intelligent agents.";
                drawString(poseStack, this.font, description, x + 10, cardY + 35, 0xCCCCCC);

                // 原则状态 - 移到下一行，避免重叠
                drawString(poseStack, this.font, "Status: ", x + 10, cardY + 55, 0xCCCCCC);
                drawString(poseStack, this.font, "Validated ✓", x + 60, cardY + 55, 0xFF00FF00);
            }

            // 如果原则太多，显示"更多..."
            if (principles.size() > displayLimit) {
                int moreY = y + displayLimit * principleHeight;
                drawString(poseStack, this.font, "(+" + (principles.size() - displayLimit) + " more principles...)",
                        x + 10, moreY + 5, 0xAAAAAA);
            }
        }

        // 底部提示，使用换行处理
        String tipText = "Activate Verification Devices to test your knowledge!";
        drawWrappedText(poseStack, tipText, x, this.height - 30, width, 0xAAAAAA, false);
    }

    // 4. 修复时间线视图中节点标题重叠问题
    private void renderTimelineExample(PoseStack poseStack, int x, int y, int width) {
        // 时间线水平线
        fill(poseStack, x, y + 20, x + width - 10, y + 22, 0xFFFFFFFF);

        // 时间点
        int numPoints = 5;
        String[] years = {"1950", "1970", "1990", "2010", "2023"};
        String[] events = {"Early AI", "Expert Systems", "Neural Networks", "Deep Learning", "Today"};

        for (int i = 0; i < numPoints; i++) {
            int pointX = x + 10 + (width - 30) * i / (numPoints - 1);

            // 时间点标记
            fill(poseStack, pointX - 3, y + 17, pointX + 3, y + 25, 0xFFFF0000);

            // 年份标签
            drawCenteredString(poseStack, this.font, years[i], pointX, y + 35, 0xFFFFFF);

            // 事件标签 - 为了避免重叠，偶数和奇数点位置交错放置
            int eventY = (i % 2 == 0) ? y : y - 15;
            drawCenteredString(poseStack, this.font, events[i], pointX, eventY, 0xFFFFAA);
        }

        // 描述文本 - 放到时间线下方足够远的位置
        drawCenteredString(poseStack, this.font, "Timelines show the historical development of agent technologies",
                x + width/2, y + 60, 0xAAAAAA);
    }

    // 5. 修复时间线可视化中的文本重叠问题
    private void renderTimelinesVisualization(PoseStack poseStack, int x, int y, int width, List<String> timelines) {
        drawString(poseStack, this.font, "Historical Development:", x, y, 0xFFFFAA);

        y += 20;

        // 时间线容器
        fill(poseStack, x, y, x + width - 10, y + 100, 0x22FFFFFF);

        // 根据实际时间线绘制
        for (int i = 0; i < Math.min(timelines.size(), 2); i++) { // 最多显示两条时间线
            int timelineY = y + 10 + i * 40;

            // 时间线名称
            drawString(poseStack, this.font, timelines.get(i) + ":", x, timelineY - 50, 0xFFFFFF);

            // 时间线水平线
            fill(poseStack, x + 10, timelineY, x + width - 20, timelineY + 2, 0xFFFFFFFF);

            // 在时间线上添加6个关键点
            for (int j = 0; j < 6; j++) {
                int pointX = x + 10 + (width - 30) * j / 5;

                // 时间点标记
                int markerColor = (j % 2 == 0) ? 0xFFFF0000 : 0xFF00FFFF;
                fill(poseStack, pointX - 3, timelineY - 3, pointX + 3, timelineY + 5, markerColor);

                // 时间点简短说明 - 交错放置事件标签，避免重叠
                if (j % 2 == 0) { // 偶数事件放在上方
                    String eventText = "Event " + (j+1);
                    drawCenteredString(poseStack, this.font, eventText, pointX, timelineY - 15, 0xFFFFAA);
                } else { // 奇数事件放在下方
                    String eventText = "Event " + (j+1);
                    drawCenteredString(poseStack, this.font, eventText, pointX, timelineY + 15, 0xFFFFAA);
                }
            }
        }
    }

    // 6. 修复时间线视图底部文字没有换行的问题
    private void renderTimelinesView(PoseStack poseStack, int x, int y, int width) {
        KnowledgeGraphManager manager = KnowledgeGraphManager.getInstance();
        List<String> timelines = manager.getMasteredTimelines();

        // 标题上移
        drawString(poseStack, this.font, "Agent Development Timelines", x, y, 0xFFFFAA);
        y += 20;

        // 分隔线
        fill(poseStack, x, y, x + width - 10, y + 1, 0x66FFFFFF);
        y += 10;

        if (timelines.isEmpty()) {
            drawString(poseStack, this.font, "No timelines mastered yet", x, y, 0xAAAAAA);

            // 显示示例时间线 - 上移
            y += 30;
            drawCenteredString(poseStack, this.font, "Example Timeline Structure:", x + width/2, y - 10, 0xFFFFAA);
            renderTimelineExample(poseStack, x, y + 20, width);
        } else {
            // 显示时间线列表
            for (int i = 0; i < timelines.size(); i++) {
                drawString(poseStack, this.font, "• " + timelines.get(i), x, y + i * 25, 0xFFFFFF);
            }

            // 时间线可视化 - 上移
            y += Math.max(60, timelines.size() * 25 + 10);
            renderTimelinesVisualization(poseStack, x, y, width, timelines);
        }

        // 底部提示 - 让提示占据整个宽度
        String tipText = "Visit the Time Corridor to arrange more historical events!";
        fill(poseStack, x, this.height - 30, x + width - 10, this.height - 10, 0x33000000);
        drawString(poseStack, this.font, tipText, x + 10, this.height - 25, 0xAAAAAA);
    }

    // 7. 对所有视图的底部提示使用统一的样式
    private void renderBottomTip(PoseStack poseStack, int x, int y, int width, String text) {
        // 半透明背景
        fill(poseStack, x, y, x + width - 10, y + 20, 0x33000000);
        // 提示文本
        drawString(poseStack, this.font, text, x + 10, y + 5, 0xAAAAAA);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}