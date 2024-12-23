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
    private static int RENDER_OFFSET_X = 64; // 渲染偏移（X）
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

        // 刷新按钮（放在屏幕左下角）
        int buttonWidth = 80;
        int buttonHeight = 20;
        applyButton = new Button(10, this.height - buttonHeight - 10, buttonWidth, buttonHeight, new TextComponent("Refresh"), button -> {

            // 重新加载地图数据
            isLoading = true;
            loadMapDataAsync();
        });
        this.addRenderableWidget(applyButton);

        // 每次打开地图时刷新数据
        loadMapDataAsync();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        // 绘制背景
        this.renderBackground(poseStack);

        // 绘制标题
        drawCenteredString(poseStack, this.font, "全局地图", this.width / 2 - 120, 20, 0xFFFFFF);

        if (isLoading) {
            // 如果地图正在加载中，显示“加载中”提示
            drawCenteredString(poseStack, this.font, "加载中...", this.width / 2, this.height / 2, 0xAAAAAA);
        } else {
            // 地图加载完成后，将地图绘制在屏幕中央
            drawGlobalMap(poseStack, this.width / 2, this.height / 2);
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

                // 计算相对于地图中心的局部坐标
                int relativeX = (pos.getX() - centerBlockX) / SAMPLE_INTERVAL_X;
                int relativeZ = (pos.getZ() - centerBlockZ) / SAMPLE_INTERVAL_Z;

                // 将局部坐标映射到屏幕坐标，以屏幕中心为地图中心
                int screenX = centerX - RENDER_OFFSET_X  + relativeX * (MAP_WIDTH / (2 * RADIUS)); // 横向比例
                int screenY = centerY + relativeZ * (MAP_HEIGHT / (2 * RADIUS)); // 纵向比例

                // 绘制像素点
                GuiComponent.fill(poseStack, screenX, screenY, screenX + 1, screenY + 1, color);
            }

            // 绘制玩家位置（固定在屏幕中心）
            GuiComponent.fill(poseStack, centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFFFFA500); // 橙色
        }
    }

    private int centerBlockX; // 地图固定中心点 X
    private int centerBlockZ; // 地图固定中心点 Z

    private void loadMapDataAsync() {
        Player player = minecraft.player;
        Level level = player.level;

        // 设置地图中心点为玩家打开地图时的位置
        centerBlockX = (int) player.getX();
        centerBlockZ = (int) player.getZ();

        isLoading = true; // 标记为加载中

        // 异步加载地图数据
        CompletableFuture.runAsync(() -> {
            Map<BlockPos, Integer> tempMapData = new HashMap<>();

            // 限制高度范围（例如玩家所在高度的上下 10 层）
            int minY = Math.max(level.getMinBuildHeight(), player.blockPosition().getY() - 10);
            int maxY = Math.min(level.getMaxBuildHeight(), player.blockPosition().getY() + 10);

            // 遍历小地图范围
            for (int x = -RADIUS; x <= RADIUS; x += SAMPLE_INTERVAL_X) {
                for (int z = -RADIUS; z <= RADIUS; z += SAMPLE_INTERVAL_Z) {
                    boolean foundBlock = false;

                    // 从高到低遍历高度范围，找到第一个非空气方块
                    for (int y = maxY; y >= minY; y--) {
                        BlockPos blockPos = new BlockPos(centerBlockX + x, y, centerBlockZ + z);
                        int color = MiniMapRenderer.getBlockColor(level, blockPos); // 获取方块颜色

                        if (color != 0x00000000) { // 找到非空气方块
                            tempMapData.put(new BlockPos(x, y, z), color); // 缓存方块颜色
                            foundBlock = true;
                            break; // 停止向下搜索
                        }
                    }

                    // 如果没有找到任何非空气方块，设置默认颜色（透明）
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