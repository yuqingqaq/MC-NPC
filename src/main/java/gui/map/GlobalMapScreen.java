package gui.map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GlobalMapScreen extends Screen {
    private static int MAP_WIDTH = 512;  // 地图宽度（像素）
    private static int MAP_HEIGHT = 512; // 地图高度（像素）
    private static int RADIUS = 256;     // 地图半径（以方块为单位）
    private static int SAMPLE_INTERVAL_X = 2; // 横向采样间隔
    private static int SAMPLE_INTERVAL_Z = 2; // 纵向采样间隔
    private static int RENDER_OFFSET_X = -115; // 渲染偏移（X）
    private static int RENDER_OFFSET_Y = 0;    // 渲染偏移（Y）

    private static Map<BlockPos, Integer> globalMapDataCache = new HashMap<>(); // 缓存的全局地图数据
    private boolean isLoading = true; // 是否正在加载地图数据

    private EditBox widthBox, heightBox, radiusBox, sampleXBox, sampleZBox, offsetXBox, offsetYBox;
    private Button applyButton;

    public GlobalMapScreen() {
        super(new TextComponent("Global Map"));
    }

    @Override
    protected void init() {
        super.init();

        int inputX = 10; // 输入框的起始 X 坐标
        int inputY = 10; // 输入框的起始 Y 坐标
        int inputWidth = 50; // 输入框的宽度
        int inputHeight = 20; // 输入框的高度
        int spacing = 40; // 每个输入框间隔的高度（包括说明文字）

        // 宽度输入框
        widthBox = new EditBox(this.font, inputX, inputY, inputWidth, inputHeight, new TextComponent("Width"));
        widthBox.setValue(String.valueOf(MAP_WIDTH));
        //this.addRenderableWidget(widthBox);

        // 高度输入框
        heightBox = new EditBox(this.font, inputX, inputY + spacing, inputWidth, inputHeight, new TextComponent("Height"));
        heightBox.setValue(String.valueOf(MAP_HEIGHT));
        //this.addRenderableWidget(heightBox);

        // 半径输入框
        radiusBox = new EditBox(this.font, inputX, inputY + spacing * 2, inputWidth, inputHeight, new TextComponent("Radius"));
        radiusBox.setValue(String.valueOf(RADIUS));
        //this.addRenderableWidget(radiusBox);

        // 横向采样间隔输入框
        sampleXBox = new EditBox(this.font, inputX, inputY + spacing * 3, inputWidth, inputHeight, new TextComponent("Sample X"));
        sampleXBox.setValue(String.valueOf(SAMPLE_INTERVAL_X));
        //this.addRenderableWidget(sampleXBox);

        // 纵向采样间隔输入框
        sampleZBox = new EditBox(this.font, inputX, inputY + spacing * 4, inputWidth, inputHeight, new TextComponent("Sample Z"));
        sampleZBox.setValue(String.valueOf(SAMPLE_INTERVAL_Z));
        //this.addRenderableWidget(sampleZBox);

        // 渲染偏移 X 输入框
        offsetXBox = new EditBox(this.font, inputX, inputY + spacing * 5, inputWidth, inputHeight, new TextComponent("Offset X"));
        offsetXBox.setValue(String.valueOf(RENDER_OFFSET_X));
        this.addRenderableWidget(offsetXBox);

        // 渲染偏移 Y 输入框
        offsetYBox = new EditBox(this.font, inputX, inputY + spacing * 6, inputWidth, inputHeight, new TextComponent("Offset Y"));
        offsetYBox.setValue(String.valueOf(RENDER_OFFSET_Y));
        this.addRenderableWidget(offsetYBox);

        // 应用按钮（放在屏幕左下角）
        int buttonWidth = 80;
        int buttonHeight = 20;
        applyButton = new Button(10, this.height - buttonHeight - 10, buttonWidth, buttonHeight, new TextComponent("Apply"), button -> {
            // 更新参数
            try {
                MAP_WIDTH = Integer.parseInt(widthBox.getValue());
                MAP_HEIGHT = Integer.parseInt(heightBox.getValue());
                RADIUS = Integer.parseInt(radiusBox.getValue());
                SAMPLE_INTERVAL_X = Integer.parseInt(sampleXBox.getValue());
                SAMPLE_INTERVAL_Z = Integer.parseInt(sampleZBox.getValue());
                RENDER_OFFSET_X = Integer.parseInt(offsetXBox.getValue());
                RENDER_OFFSET_Y = Integer.parseInt(offsetYBox.getValue());
            } catch (NumberFormatException e) {
                // 如果输入无效，则忽略
            }

            // 重新加载地图数据
            isLoading = true;
            loadMapDataAsync();
        });
        this.addRenderableWidget(applyButton);

        // 如果已有缓存数据，直接使用
        if (!globalMapDataCache.isEmpty()) {
            isLoading = false; // 标记加载完成
        } else {
            // 否则异步加载地图数据
            loadMapDataAsync();
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        // 绘制背景
        this.renderBackground(poseStack);

        // 绘制标题
        drawCenteredString(poseStack, this.font, "全局地图", this.width / 2, 40, 0xFFFFFF);

        // 绘制输入框下方的说明文字
        int labelX = 70; // 说明文字的起始 X 坐标
        int labelY = 15; // 说明文字的起始 Y 坐标
        int spacing = 40; // 每个输入框间隔的高度（包括说明文字）
        drawString(poseStack, this.font, "地图宽度", labelX, labelY, 0xFFFFFF);
        drawString(poseStack, this.font, "地图高度", labelX, labelY + spacing, 0xFFFFFF);
        drawString(poseStack, this.font, "地图半径", labelX, labelY + spacing * 2, 0xFFFFFF);
        drawString(poseStack, this.font, "横向采样率", labelX, labelY + spacing * 3, 0xFFFFFF);
        drawString(poseStack, this.font, "纵向采样率", labelX, labelY + spacing * 4, 0xFFFFFF);
        drawString(poseStack, this.font, "渲染偏移 X", labelX, labelY + spacing * 5, 0xFFFFFF);
        drawString(poseStack, this.font, "渲染偏移 Y", labelX, labelY + spacing * 6, 0xFFFFFF);

        if (isLoading) {
            // 如果地图正在加载中，显示“加载中”提示
            drawCenteredString(poseStack, this.font, "加载中...", this.width / 2, this.height / 2, 0xAAAAAA);
        } else {
            // 地图加载完成后，将地图绘制在屏幕中央
            drawGlobalMap(poseStack, this.width / 2 + RENDER_OFFSET_X, this.height / 2 + RENDER_OFFSET_Y);
        }

        // 绘制关闭按钮提示
        drawCenteredString(poseStack, this.font, "按 ESC 关闭", this.width / 2, this.height - 20, 0xAAAAAA);

        super.render(poseStack, mouseX, mouseY, delta);
    }

    private void drawGlobalMap(PoseStack poseStack, int centerX, int centerY) {
        if (globalMapDataCache != null) {
            Player player = minecraft.player;

            // 玩家当前位置的世界坐标
            int playerX = (int) player.getX();
            int playerZ = (int) player.getZ();

            // 遍历缓存的地图数据
            for (Map.Entry<BlockPos, Integer> entry : globalMapDataCache.entrySet()) {
                BlockPos pos = entry.getKey();
                int color = entry.getValue();

                // 计算相对于玩家的局部坐标
                int relativeX = (pos.getX() - playerX) / SAMPLE_INTERVAL_X;
                int relativeZ = (pos.getZ() - playerZ) / SAMPLE_INTERVAL_Z;

                // 将局部坐标映射到屏幕坐标，以屏幕中心为起点
                int screenX = centerX + relativeX * (MAP_WIDTH / (2 * RADIUS)); // 横向比例
                int screenY = centerY + relativeZ * (MAP_HEIGHT / (2 * RADIUS)); // 纵向比例

                // 绘制像素点
                GuiComponent.fill(poseStack, screenX, screenY, screenX + 1, screenY + 1, color);
            }
        }
    }


    private void loadMapDataAsync() {
        Player player = minecraft.player;
        Level level = player.level;

        // 异步加载地图数据
        CompletableFuture.runAsync(() -> {
            Map<BlockPos, Integer> tempMapData = new HashMap<>();

            int centerX = (int) player.getX();
            int centerZ = (int) player.getZ();

            // 限制高度范围（例如玩家所在高度的上下 10 层）
            int minY = Math.max(level.getMinBuildHeight(), player.blockPosition().getY() - 10);
            int maxY = Math.min(level.getMaxBuildHeight(), player.blockPosition().getY() + 10);

            // 使用采样步长
            for (int x = -RADIUS; x <= RADIUS; x += SAMPLE_INTERVAL_X/2) {
                for (int z = -RADIUS; z <= RADIUS; z += SAMPLE_INTERVAL_Z) {
                    boolean foundBlock = false;

                    // 从上往下遍历高度范围，找到第一个非空气方块
                    for (int y = maxY; y >= minY; y--) {
                        BlockPos blockPos = new BlockPos(centerX + x, y, centerZ + z);
                        int color = MiniMapRenderer.getBlockColor(level, blockPos); // 获取方块颜色

                        if (color != 0x00000000) { // 非空气方块
                            tempMapData.put(new BlockPos(x, y, z), color); // 缓存方块颜色
                            foundBlock = true;
                            break; // 停止向下搜索
                        }
                    }

                    // 如果没有找到任何非空气方块，设置一个默认颜色（透明）
                    if (!foundBlock) {
                        tempMapData.put(new BlockPos(x, minY, z), 0x00000000);
                    }
                }
            }

            // 数据加载完成后更新到主线程
            minecraft.execute(() -> {
                globalMapDataCache = tempMapData; // 更新缓存
                isLoading = false; // 标记加载完成
            });
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
}