package gui.map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class MiniMapRenderer {
    private static boolean needsUpdate = true; // 是否需要更新小地图数据
    private static int[][] cachedMiniMapData; // 缓存小地图的颜色数据
    private static int mapSize =60; // 小地图大小
    private static int radius = 30; // 小地图显示范围
    private static int heightRange = 5; // 显示的高度范围
    private static final Map<String, Integer> blockColorMap = new HashMap<>(){{
        // 常见方块的颜色定义
        // 常见方块的颜色定义
        put("minecraft:air", 0x00000000); // 空气：透明
        put("minecraft:smooth_stone_slab", 0xFFA9A9A9); // 平滑石台阶：浅灰色
        put("minecraft:white_concrete", 0xFFFFFFFF); // 白色混凝土：白色
        put("minecraft:light_gray_wool", 0xFFD3D3D3); // 浅灰色羊毛：浅灰色
        put("minecraft:anvil", 0xFF696969); // 铁砧：深灰色
        put("minecraft:birch_leaves", 0xFF80A755); // 桦树叶：浅绿色
        put("minecraft:stone_bricks", 0xFF808080); // 石砖：灰色
        put("minecraft:jungle_leaves", 0xFF77AB2F); // 丛林树叶：亮绿色
        put("minecraft:smooth_stone", 0xFFA9A9A9); // 平滑石头：浅灰色
        put("minecraft:spruce_leaves", 0xFF619961); // 云杉叶：深绿色
        put("minecraft:brown_wool", 0xFF8B4513); // 棕色羊毛：棕色
        put("minecraft:oak_wood", 0xFF8B6B44); // 橡木：浅棕色
        put("minecraft:spruce_planks", 0xFF915B38); // 云杉木板：深棕色
        put("minecraft:green_wool", 0xFF556B2F); // 绿色羊毛：橄榄绿
        put("minecraft:bricks", 0xFFC1440E); // 砖块：砖红色
        put("minecraft:brick_slab", 0xFFC1440E); // 砖台阶：砖红色
        put("minecraft:oak_log", 0xFF8B6B44); // 橡木原木：浅棕色
        put("minecraft:gray_wool", 0xFF808080); // 灰色羊毛：灰色
        put("minecraft:diorite", 0xFFFFFAF0); // 闪长岩：白色
        put("minecraft:white_wool", 0xFFFFFFFF); // 白色羊毛：白色
        put("minecraft:light_gray_concrete", 0xFFD3D3D3); // 浅灰色混凝土：浅灰色
        put("minecraft:beacon", 0xFF55FFAA); // 信标：亮青色
        put("minecraft:acacia_planks", 0xFFFFA07A); // 金合欢木板：橙红色
        put("minecraft:cyan_terracotta", 0xFF5B6E6E); // 青色陶瓦：灰青色
        put("minecraft:polished_diorite", 0xFFF5F5F5); // 抛光闪长岩：白色
        put("minecraft:red_mushroom_block", 0xFFF5DEB3); // 红色蘑菇块：米黄色（小麦色）
        put("minecraft:bone_block", 0xFFFFFFE0); // 骨块：奶白色
        put("minecraft:jungle_planks", 0xFFAE8652); // 丛林木板：浅棕色
        put("minecraft:spruce_trapdoor", 0xFF6B4226); // 云杉活板门：深棕色
        put("minecraft:azure_bluet", 0xFFFFFFFF); // 茶色小花：白色
        put("minecraft:blue_orchid", 0xFF42A4F4); // 蓝色兰花：浅蓝色
        put("minecraft:green_stained_glass", 0xFF557755); // 绿色染色玻璃：深绿
        put("minecraft:quartz_block", 0xFFFFFFF0); // 石英块：白色
        put("minecraft:lime_wool", 0xFF32CD32); // 黄绿色羊毛：亮绿
        put("minecraft:glass", 0x80FFFFFF); // 玻璃：半透明
        put("minecraft:rose_bush", 0xFFFF0000); // 玫瑰花：红色
        put("minecraft:iron_bars", 0xFFB0C4DE); // 铁栏杆：浅灰蓝色
        put("minecraft:magenta_concrete", 0xFFFF00FF); // 品红色混凝土：品红色
        put("minecraft:magenta_wool", 0xFFFF00FF); // 品红色羊毛：品红色
        put("minecraft:white_terracotta", 0xFFF5E6D7); // 白色陶瓦：米白色
        put("minecraft:oak_slab", 0xFF8B6B44); // 橡木台阶：浅棕色
        put("minecraft:bookshelf", 0xFFC19A6B); // 书架：浅棕色
        put("minecraft:stripped_birch_wood", 0xFFEADAA9); // 去皮桦木：浅黄色
        put("minecraft:smooth_quartz", 0xFFFFFFF0); // 平滑石英：白色
        put("minecraft:yellow_wool", 0xFFFFFF00); // 黄色羊毛：亮黄
        put("minecraft:iron_block", 0xFFC1C1C1); // 铁块：浅灰色
        put("minecraft:lily_pad", 0xFF208030); // 荷叶：深绿色
        put("minecraft:stone_brick_slab", 0xFF808080); // 石砖台阶：灰色
        put("minecraft:composter", 0xFF5F4326); // 堆肥桶：深棕色
        put("minecraft:cobblestone", 0xFF808080); // 圆石：灰色
        put("minecraft:oak_planks", 0xFF8B6B44); // 橡木木板：浅棕色
        put("minecraft:polished_andesite", 0xFFA9A9A9); // 抛光安山岩：浅灰色
        put("minecraft:polished_andesite_slab", 0xFFA9A9A9); // 抛光安山岩台阶：浅灰色
        put("minecraft:water", 0xFF3F76E4); // 水：默认蓝色
        put("minecraft:flowing_water", 0xFF3F76E4); // 流动的水：默认蓝色
        put("minecraft:nether_bricks", 0xFF301B1B); // 下界砖：深红棕色
        put("minecraft:red_sand", 0xFFDB7F48); // 红沙：橙红色
        put("minecraft:blue_wool", 0xFF3C44AA); // 蓝色羊毛：深蓝色
        put("minecraft:green_concrete", 0xFF5E7C16); // 绿色混凝土：深绿色
        put("minecraft:light_blue_concrete", 0xFF3AB3DA); // 浅蓝色混凝土：亮蓝色
        put("minecraft:magenta_terracotta", 0xFF95576C); // 品红色陶瓦：暗紫红色
        put("minecraft:netherite_block", 0xFF3C3C3C); // 下界合金块：深灰色
        put("minecraft:crying_obsidian", 0xFF463E6E); // 哭泣的黑曜石：紫灰色
        put("minecraft:warped_planks", 0xFF2A6A66); // 扭曲木板：青绿色
        put("minecraft:warped_nylium", 0xFF3F5E59); // 扭曲菌岩：青蓝色
        put("minecraft:crimson_planks", 0xFF7D332D); // 绯红木板：暗红色
        put("minecraft:crimson_nylium", 0xFF6A2C27); // 绯红菌岩：深红色
        put("minecraft:basalt", 0xFF3E3E3E); // 玄武岩：深灰色
        put("minecraft:polished_basalt", 0xFF4C4C4C); // 抛光玄武岩：浅灰色
        put("minecraft:blackstone", 0xFF1A1A1A); // 黑石：深黑色
        put("minecraft:soul_soil", 0xFF74543C); // 灵魂土：棕色
        put("minecraft:ancient_debris", 0xFF4C3A32); // 遗迹残骸：深棕色
        put("minecraft:coral_block", 0xFFFF7F50); // 珊瑚块：珊瑚粉色
        put("minecraft:prismarine", 0xFF8AB3A5); // 海晶石：浅青绿色
        put("minecraft:prismarine_bricks", 0xFF80C4B9); // 海晶石砖：青绿色
        put("minecraft:dark_prismarine", 0xFF456E67); // 暗海晶石：深青绿色
        put("minecraft:kelp", 0xFF4C9A2A); // 海带：深绿色
        put("minecraft:honey_block", 0xFFFFA631); // 蜂蜜块：亮橙色
        put("minecraft:slime_block", 0xFF82C873); // 粘液块：亮绿色
        put("minecraft:nether_wart_block", 0xFF7F2B2B); // 地狱疣块：深红色
        put("minecraft:shroomlight", 0xFFFFA740); // 菌光体：亮橙色
        put("minecraft:obsidian", 0xFF271A34); // 黑曜石：深紫色
        put("minecraft:dark_oak_planks", 0xFF4C3A2E); // 深橡木木板：深棕色
        put("minecraft:ice", 0x80D4F4FF); // 冰：半透明冰蓝色
        put("minecraft:packed_ice", 0xFFD4F4FF); // 浓缩冰：冰蓝色
        put("minecraft:blue_ice", 0xFF8CBED6); // 蓝冰：浅蓝色
        put("minecraft:bedrock", 0xFF303030); // 基岩：深灰色
        put("minecraft:gravel", 0xFFA8A8A8); // 沙砾：浅灰色
        put("minecraft:sand", 0xFFFFDAB9); // 沙子：沙色
        put("minecraft:deepslate", 0xFF4A4A4A); // 深板岩：深灰色
        put("minecraft:cobbled_deepslate", 0xFF505050); // 深板岩圆石：深灰色
        put("minecraft:deepslate_bricks", 0xFF404040); // 深板岩砖：深灰色
        put("minecraft:deepslate_tiles", 0xFF383838); // 深板岩瓦：深灰色
        put("minecraft:amethyst_block", 0xFFD3B2E4); // 紫水晶块：淡紫色
        put("minecraft:calcite", 0xFFFFF5EE); // 方解石：象牙白
        put("minecraft:tuff", 0xFF7F7F7F); // 凝灰岩：浅灰色
        put("minecraft:glow_lichen", 0xFF9DC1A4); // 发光地衣：淡青色
        put("minecraft:dripstone_block", 0xFF9A8678); // 滴水石块：棕色
        put("minecraft:pointed_dripstone", 0xFF9A8678); // 尖滴水石：棕色
        put("minecraft:copper_block", 0xFFDA8A67); // 铜块：浅橙色
        put("minecraft:oxidized_copper", 0xFF3A6E75); // 氧化铜：青绿色
        put("minecraft:cut_copper", 0xFFDC9A7B); // 切制铜块：浅橙色
        put("minecraft:waxed_copper_block", 0xFFD08A67); // 打蜡铜块：橙色
        put("minecraft:raw_copper_block", 0xFFC27754); // 粗铜块：棕橙色
        put("minecraft:raw_iron_block", 0xFFC7AF8F); // 粗铁块：灰棕色
        put("minecraft:raw_gold_block", 0xFFFCD34D); // 粗金块：金黄色
        put("minecraft:dripstone", 0xFF8F7F6F); // 滴水石：棕灰色
        put("minecraft:mangrove_planks", 0xFF703424); // 红树木板：红棕色
        put("minecraft:mangrove_log", 0xFF704034); // 红树原木：红棕色
        put("minecraft:green_carpet", 0xFF556B2F); // 绿色地毯：橄榄绿
        put("minecraft:cauldron", 0xFF808080); // 炼药锅：灰色
        put("minecraft:polished_granite", 0xFFE9967A); // 抛光花岗岩：浅橙色
        put("minecraft:cyan_wool", 0xFF00FFFF); // 青色羊毛：青色
        put("minecraft:polished_blackstone_bricks", 0xFF4B484C); // 抛光黑石砖：深灰色
        put("minecraft:birch_log", 0xFFEADAA9); // 桦木原木：浅黄色
        put("minecraft:cyan_stained_glass", 0x8000FFFF); // 青色染色玻璃：半透明青
        put("minecraft:dark_oak_fence", 0xFF4B3621); // 深橡木栅栏：深棕色
        put("minecraft:light_blue_stained_glass_pane", 0x80428BFF); // 浅蓝染色玻璃板：半透明浅蓝
        put("minecraft:red_stained_glass_pane", 0x80FF0000); // 红色染色玻璃板：半透明红色
        put("minecraft:birch_planks", 0xFFEADAA9); // 桦木木板：浅黄色
        put("minecraft:stripped_jungle_wood", 0xFFAE8652); // 去皮丛林木：浅棕色
        put("minecraft:jungle_slab", 0xFFAE8652); // 丛林台阶：浅棕色
        put("minecraft:oak_stairs", 0xFFC19A6B); // 橡木楼梯：浅棕色
        put("minecraft:dispenser", 0xFF4F4F4F); // 发射器：深灰色
        put("minecraft:spruce_slab", 0xFF915B38); // 云杉台阶：深棕色
        put("minecraft:brown_mushroom_block", 0xFF8B4513); // 棕色蘑菇块：棕色
        put("minecraft:dark_oak_stairs", 0xFF4B3621); // 深橡木楼梯：深棕色
        put("minecraft:black_wool", 0xFF000000); // 黑色羊毛：黑色
        put("minecraft:iron_door", 0xFFB0C4DE); // 铁门：浅蓝灰色
        put("minecraft:smooth_quartz_slab", 0xFFF5F5F5); // 平滑石英台阶：白色
        put("minecraft:jungle_fence", 0xFFAE8652); // 丛林栅栏：浅棕色
        put("minecraft:pink_terracotta", 0xFFDB7093); // 粉色陶瓦：粉红色
        put("minecraft:spruce_door", 0xFF915B38); // 云杉门：深棕色
        put("minecraft:spruce_stairs", 0xFF915B38); // 云杉楼梯：深棕色
        put("minecraft:potted_jungle_sapling", 0xFFAE8652); // 丛林树苗盆栽：浅棕色
        put("minecraft:birch_slab", 0xFFEADAA9); // 桦木台阶：浅黄色
        put("minecraft:red_sandstone_wall", 0xFFCD853F); // 红砂岩墙：橙棕色
        put("minecraft:lime_terracotta", 0xFF32CD32); // 黄绿色陶瓦：亮绿
        put("minecraft:yellow_concrete", 0xFFFFFF00); // 黄色混凝土：亮黄
        put("minecraft:beehive", 0xFFC19A6B); // 蜂巢：浅棕色
        put("minecraft:dropper", 0xFF4F4F4F); // 投掷器：深灰色
        put("minecraft:observer", 0xFF2D2D2D); // 侦测器：深灰色
        put("minecraft:blue_concrete", 0xFF0000FF); // 蓝色混凝土：蓝色
        put("minecraft:gray_wall_banner", 0xFF808080); // 灰色墙旗：灰色
        put("minecraft:lodestone", 0xFF808080); // 磁石：灰色
        put("minecraft:light_gray_wall_banner", 0xFFD3D3D3); // 浅灰色墙旗：浅灰色
        put("minecraft:loom", 0xFFC19A6B); // 织布机：浅棕色
        put("minecraft:iron_trapdoor", 0xFFB0C4DE); // 铁活板门：浅蓝灰色
        put("minecraft:barrier", 0xFF000000); // 屏障方块：黑色
        put("minecraft:warped_wall_sign", 0xFF009688); // 扭曲墙牌：青绿色
        put("minecraft:brown_terracotta", 0xFF8B4513); // 棕色陶瓦：棕色
        put("minecraft:chain", 0xFF808080); // 链条：灰色
        put("minecraft:sea_lantern", 0xFFADD8E6); // 海晶灯：淡蓝色
        put("minecraft:yellow_terracotta", 0xFFD3A625); // 黄色陶瓦：金黄
        put("minecraft:red_terracotta", 0xFF8B0000); // 红色陶瓦：深红色
        put("minecraft:red_wool", 0xFFFF0000); // 红色羊毛：红色
        put("minecraft:quartz_stairs", 0xFFF5F5F5); // 石英楼梯：白色
        put("minecraft:oak_fence", 0xFFC19A6B); // 橡木栅栏：浅棕色
        put("minecraft:dark_oak_button", 0xFF4B3621); // 深橡木按钮：深棕色
        put("minecraft:note_block", 0xFF8B4513); // 音符盒：棕色
        put("minecraft:glass_pane", 0x80FFFFFF); // 玻璃板：半透明白
        put("minecraft:jungle_stairs", 0xFFAE8652); // 丛林楼梯：浅棕色
        put("minecraft:acacia_stairs", 0xFFFFA07A); // 金合欢楼梯：橙红色
        put("minecraft:acacia_fence", 0xFFFFA07A); // 金合欢栅栏：橙红色
        put("minecraft:acacia_slab", 0xFFFFA07A); // 金合欢台阶：橙红色
        put("minecraft:birch_stairs", 0xFFEADAA9); // 桦木楼梯：浅黄色
        put("minecraft:black_carpet", 0xFF000000); // 黑色地毯：黑色
        put("minecraft:potted_birch_sapling", 0xFFEADAA9); // 桦木树苗盆栽：浅黄色
        put("minecraft:quartz_slab", 0xFFF5F5F5); // 石英台阶：白色
        put("minecraft:purple_wool", 0xFF800080); // 紫色羊毛：紫色
        put("minecraft:dirt", 0xFF8B4513); // 泥土：棕色
        // 添加其他方块
    }};

    // 方块颜色缓存
    private static final Map<BlockPos, Integer> blockColorCache = new HashMap<>();

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        int updateCounter = 0;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        // 每帧绘制小地图
        renderMiniMap(event.getMatrixStack(), player);

        // 每 5 帧更新一次小地图数据
        if (updateCounter % 5 == 0) {
            updateMiniMapData(player);
            needsUpdate = false; // 数据更新完成
        }
        updateCounter++;
    }

    private static void renderMiniMap(PoseStack poseStack, Player player) {
        Minecraft mc = Minecraft.getInstance();

        int centerX = mc.getWindow().getGuiScaledWidth() - mapSize - 10; // 右上角位置
        int centerY = 10;

        // 绘制小地图背景
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f); // 半透明背景
        GuiComponent.fill(poseStack, centerX, centerY, centerX + mapSize, centerY + mapSize, 0x80000000);

        // 绘制缓存的小地图数据
        if (cachedMiniMapData != null) {
            int samplingFactor = 2; // 与 updateMiniMapData 中的采样频率保持一致
            for (int x = 0; x < cachedMiniMapData.length; x++) {
                for (int z = 0; z < cachedMiniMapData[x].length; z++) {
                    int color = cachedMiniMapData[x][z];
                    if (color != 0) { // 只绘制非空数据
                        // 调整绘制比例，根据采样频率计算像素位置
                        int pixelX = centerX + x * (mapSize / (2 * radius)) * samplingFactor;
                        int pixelY = centerY + z * (mapSize / (2 * radius)) * samplingFactor;
                        GuiComponent.fill(poseStack, pixelX, pixelY, pixelX + 2, pixelY + 2, color);
                    }
                }
            }
        }

        // 绘制玩家位置（用橙色点表示）
        int playerPixelX = centerX + mapSize / 2;
        int playerPixelY = centerY + mapSize / 2;
        GuiComponent.fill(poseStack, playerPixelX - 2, playerPixelY - 2, playerPixelX + 1, playerPixelY + 1, 0xFF00FF00); // 绿色

        // 根据玩家的朝向绘制箭头
        float yaw = player.getYRot(); // 玩家朝向
        double arrowAngle = Math.toRadians(yaw);

        // 箭头大小
        int arrowSize = 5;

        // 计算箭头头部的位置（反转方向偏移）
        int arrowX = (int) (playerPixelX - Math.sin(arrowAngle) * arrowSize);
        int arrowY = (int) (playerPixelY + Math.cos(arrowAngle) * arrowSize);

        // 绘制箭头头部（绿色点）
        GuiComponent.fill(poseStack, arrowX - 2, arrowY - 2, arrowX + 2, arrowY + 2, 0xFF00FF00); // 绿色箭头头部

        // 计算箭头尾部的位置
        int tailX = (int) (playerPixelX + Math.sin(arrowAngle) * (arrowSize / 2));
        int tailY = (int) (playerPixelY - Math.cos(arrowAngle) * (arrowSize / 2));

        // 绘制箭头尾部（线条）
        GuiComponent.fill(poseStack, tailX - 1, tailY - 1, tailX + 1, tailY + 1, 0xFF00FF00); // 绿色箭头尾部

        renderEntitiesOnMiniMap(poseStack, player, centerX, centerY);

    }

    private static void renderEntitiesOnMiniMap(PoseStack poseStack, Player player, int centerX, int centerY) {
        Minecraft mc = Minecraft.getInstance();
        Level level = player.level;

        // 小地图中心位置
        int mapCenterX = centerX + mapSize / 2;
        int mapCenterY = centerY + mapSize / 2;

        // 获取所有附近的实体
        level.getEntities(player, player.getBoundingBox().inflate(radius), entity -> true).forEach(entity -> {

            // 获取实体的相对位置
            double relativeX = entity.getX() - player.getX();
            double relativeZ = entity.getZ() - player.getZ();
            double distance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);

            // 如果实体超出小地图范围，则跳过
            if (distance > radius) return;

            // 将实体位置映射到小地图的像素位置
            int entityPixelX = (int) (mapCenterX + (relativeX / radius) * (mapSize / 2));
            int entityPixelY = (int) (mapCenterY + (relativeZ / radius) * (mapSize / 2));

            // 绘制实体
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity) {
                GuiComponent.fill(poseStack, entityPixelX - 1, entityPixelY - 1, entityPixelX + 1, entityPixelY + 1, 0xFFFFFF00); // 黄色点（物品实体）
            } else {
                GuiComponent.fill(poseStack, entityPixelX - 2, entityPixelY - 2, entityPixelX + 2, entityPixelY + 2, 0xFF0000FF); // 蓝色点（普通实体）
            }
        });
    }

    private static void updateMiniMapData(Player player) {
        Minecraft mc = Minecraft.getInstance();
        Level level = player.level;

        Vec3 playerPos = player.position();
        int playerX = (int) playerPos.x;
        int playerZ = (int) playerPos.z;

        // 初始化缓存数组
        cachedMiniMapData = new int[radius * 2 + 1][radius * 2 + 1];

        // 用于统计方块种类
        Map<String, Integer> blockTypeCount = new HashMap<>();

        int samplingFactor = 2; // 采样频率倍数（1 = 全采样，2 = 每隔1个方块采样，3 = 每隔2个方块采样）

        // 遍历小地图范围内的方块
        for (int x = -radius; x <= radius; x += samplingFactor) { // 使用采样频率
            for (int z = -radius; z <= radius; z += samplingFactor) {
                boolean blockFound = false; // 标记是否找到第一个非空气方块

                // 从高到低遍历高度范围
                for (int dy = heightRange; dy >= -heightRange; dy--) {
                    BlockPos blockPos = new BlockPos(playerX + x, player.blockPosition().getY() + dy, playerZ + z);

                    // 获取方块状态
                    BlockState blockState = level.getBlockState(blockPos);
                    String blockName = blockState.getBlock().getRegistryName().toString();

                    // 判断是否为非空气方块
                    if (!blockState.isAir()) {
                        // 如果方块类型未统计过，则添加到 blockColorMap
                        if (!blockColorMap.containsKey(blockName)) {
                            blockColorMap.put(blockName, 0xFF808080); // 默认颜色（可以替换为实际颜色）
                        }
                        // 获取方块颜色并缓存到小地图数据
                        cachedMiniMapData[(x + radius) / samplingFactor][(z + radius) / samplingFactor] = blockColorMap.get(blockName);

                        // 统计方块类型数量
                        blockTypeCount.put(blockName, blockTypeCount.getOrDefault(blockName, 0) + 1);

                        blockFound = true; // 标记找到方块
                        break; // 跳出高度遍历，停止向下搜索
                    }
                }

                // 如果没有找到任何非空气方块，设置默认颜色（如透明）
                if (!blockFound) {
                    cachedMiniMapData[(x + radius) / samplingFactor][(z + radius) / samplingFactor] = 0x00000000; // 透明
                }
            }
        }
    }

    private static int getBlockColorWithCache(Level level, BlockPos blockPos) {
        // 检查缓存中是否已有颜色
        if (blockColorCache.containsKey(blockPos)) {
            return blockColorCache.get(blockPos);
        }

        // 如果缓存中没有，计算颜色并存入缓存
        int color = getBlockColor(level, blockPos);
        blockColorCache.put(blockPos, color);
        return color;
    }

    static int getBlockColor(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        String blockName = blockState.getBlock().getRegistryName().toString();

        // 优先从硬编码颜色映射表中获取颜色
        if (blockColorMap.containsKey(blockName)) {
            return blockColorMap.get(blockName);
        }

        // 如果映射中没有，尝试动态计算颜色
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int color = blockColors.getColor(blockState, level, blockPos, 0);
        if (color != -1) {
            return 0xFF000000  ; // 确保颜色值不透明
        }

        // 如果动态颜色不可用，返回默认颜色
        return getDefaultBlockColor(blockState);
    }

    private static int getDefaultBlockColor(BlockState blockState) {
        if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
            return 0xFF8B8B59; // 草方块：绿色
        } else if (blockState.getBlock() == Blocks.OAK_LEAVES) {
            return 0xFF228B22; // 橡树叶：深绿色
        } else if (blockState.getBlock() == Blocks.WATER) {
            return 0xFF3F76E4; // 水：蓝色
        } else if (blockState.getBlock() == Blocks.LAVA) {
            return 0xFFFF4500; // 岩浆：橙红色
        } else if (blockState.getBlock() == Blocks.VOID_AIR) {
            return 0x00000000; // 虚空空气：透明
        } else if (blockState.getMaterial().isSolid()) {
            return 0xFF808080; // 实体方块：灰色
        }
        return 0xFFF0F0F0; // 其他未知方块：深灰色
    }
    // 调整颜色亮度的方法
    private static int adjustColorBrightness(int color, int heightDifference) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        int brightnessAdjustment = heightDifference * 15; // 每层调整 15 的亮度
        red = clampColorValue(red + brightnessAdjustment);
        green = clampColorValue(green + brightnessAdjustment);
        blue = clampColorValue(blue + brightnessAdjustment);

        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    private static int clampColorValue(int value) {
        return Math.max(0, Math.min(255, value));
    }
}