package gui.map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiComponent;
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

    private boolean isLoading = true; // 是否正在加载地图数据
    private Map<BlockPos, Integer> currentMapData = new HashMap<>(); // 当前帧的地图数据

    private int centerBlockX; // 地图中心点 X（玩家打开地图时的位置）
    private int centerBlockZ; // 地图中心点 Z（玩家打开地图时的位置）

    private Button refreshButton;

    public GlobalMapScreen() {
        super(new TextComponent("Global Map"));
    }

    @Override
    protected void init() {
        super.init();

        // 刷新按钮（放在屏幕左下角）
        int buttonWidth = 80;
        int buttonHeight = 20;
        refreshButton = new Button(10, this.height - buttonHeight - 10, buttonWidth, buttonHeight, new TextComponent("Refresh"), button -> {
            // 重新加载地图数据
            isLoading = true;
            loadMapDataAsync();
        });
        this.addRenderableWidget(refreshButton);

        // 打开地图时立即加载数据
        loadMapDataAsync();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        // 绘制背景
        this.renderBackground(poseStack);

        // 绘制标题
        drawCenteredString(poseStack, this.font, "全局地图", this.width / 2 -100, 20, 0xFFFFFF);

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
        Player player = minecraft.player;

        // 遍历当前帧的地图数据
        for (Map.Entry<BlockPos, Integer> entry : currentMapData.entrySet()) {
            BlockPos pos = entry.getKey();
            int color = entry.getValue();

            // 计算相对于地图中心 (centerBlockX, centerBlockZ) 的局部坐标
            int relativeX = (pos.getX() - centerBlockX) / SAMPLE_INTERVAL_X;
            int relativeZ = (pos.getZ() - centerBlockZ) / SAMPLE_INTERVAL_Z;

            // 将局部坐标映射到屏幕坐标，以屏幕中心为地图中心
            int screenX = centerX + relativeX * (MAP_WIDTH / (2 * RADIUS)); // 横向比例
            int screenY = centerY + relativeZ * (MAP_HEIGHT / (2 * RADIUS)); // 纵向比例

            // 绘制像素点
            GuiComponent.fill(poseStack, screenX, screenY, screenX + 1, screenY + 1, color);
        }

        GuiComponent.fill(poseStack, centerX - 2, centerY - 2, centerX + 1, centerY + 1, 0xFF00FF00); // 橙色

        float yaw = player.getYRot(); // 获取玩家的旋转角度（朝向）
        double arrowAngle = Math.toRadians(yaw + 180); // 将角度转为弧度，并反转方向以匹配屏幕坐标系

        int arrowSize = 6; // 箭头大小可根据全局地图调整

        int arrowX = (int) (centerX + Math.sin(arrowAngle) * arrowSize); // 前进方向 X 偏移
        int arrowY = (int) (centerY - Math.cos(arrowAngle) * arrowSize); // 前进方向 Y 偏移

        GuiComponent.fill(poseStack, arrowX - 2, arrowY - 2, arrowX + 2, arrowY + 2, 0xFF00FF00); // 绿色箭头头部

        int tailX = (int) (centerX - Math.sin(arrowAngle) * (arrowSize / 2)); // 尾部 X 偏移
        int tailY = (int) (centerY + Math.cos(arrowAngle) * (arrowSize / 2)); // 尾部 Y 偏移

        GuiComponent.fill(poseStack, tailX - 1, tailY - 1, tailX + 1, tailY + 1, 0xFF00FF00); // 绿色箭头尾部
    }

    private void loadMapDataAsync() {
        Player player = minecraft.player;
        Level level = player.level;

        // 设置地图中心点为玩家打开地图时的位置
        if (centerBlockX == 0 && centerBlockZ == 0) { // 仅初始化一次
            centerBlockX = (int) player.getX();
            centerBlockZ = (int) player.getZ();
        }

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
                            tempMapData.put(new BlockPos(centerBlockX + x, y, centerBlockZ + z), color); // 缓存方块颜色
                            foundBlock = true;
                            break; // 停止向下搜索
                        }
                    }

                    // 如果没有找到任何非空气方块，设置默认颜色（透明）
                    // 有缓存的话可以加载
                    if (!foundBlock) {
                        tempMapData.put(new BlockPos(centerBlockX + x, minY, centerBlockZ + z), 0x00B0B0B0);
                    }
                }
            }

            // 数据加载完成后更新到主线程
            minecraft.execute(() -> {
                currentMapData = tempMapData; // 更新当前帧的地图数据
                isLoading = false; // 标记加载完成
            });
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
}