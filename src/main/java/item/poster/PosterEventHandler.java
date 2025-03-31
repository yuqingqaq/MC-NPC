package item.poster;

import controller.KnowledgeGraphManager;
import controller.PosterLearningData;
import controller.PosterManager;
import item.poster.client.PosterViewClientHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import npcopenai.NPCOpenAI;
import registry.ItemRegistry;

import java.util.logging.Logger;

@Mod.EventBusSubscriber(modid = NPCOpenAI.MODID)
public class PosterEventHandler {
    private static final Logger LOGGER = Logger.getLogger("PosterEventHandler");

    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getPlayer();

        // 检查是否点击的是物品框架
        if (target instanceof ItemFrame frame) {
            ItemStack frameItem = frame.getItem();

            // 检查是否有NBT标签
            if (!frameItem.isEmpty() && frameItem.hasTag()) {
                CompoundTag tag = frameItem.getTag();

                // 检查是否包含海报数据
                if (tag != null && tag.contains("PosterData")) {
                    CompoundTag posterData = tag.getCompound("PosterData");
                    String posterType = posterData.getString("PosterType");

                    // 确保有有效的海报类型
                    if (!posterType.isEmpty()) {
                        //LOGGER.info("Player " + player.getName().getString() + " interacted with poster: " + posterType);

                        // 取消默认交互（防止取下展示框）
                        event.setCanceled(true);

                        // 处理海报交互
                        handlePosterInteraction(player, posterType);
                    }
                }
            }
        }
    }

    // 可选：添加方块破坏事件处理，虽然不直接影响ItemFrame
    @SubscribeEvent
    public static void onBlockBreak(PlayerEvent.BreakSpeed event) {
        BlockState state = event.getState();
        // 这里我们可以检查被破坏的是否是物品展示框所在的方块
        // 但一般情况下这不是必要的，因为ItemFrame是实体不是方块
    }

    // 处理玩家与海报的交互
    private static void handlePosterInteraction(Player player, String posterType) {
        // 获取海报数据
        PosterManager.PosterData data = getPosterDataFromType(posterType);
        if (data == null) {
            LOGGER.warning("Failed to get poster data for type: " + posterType);
            return;
        }

        // 客户端：显示海报内容
        if (player.level.isClientSide) {
            PosterViewClientHandler.openPosterScreen(
                    data.getTitle(),
                    data.getContent(),
                    data.getImagePath(),
                    data.getExpertType()
            );
        }
        // 服务端：记录学习进度
        else if (player instanceof ServerPlayer) {
            recordPosterLearning(player, posterType, data);
        }
    }

    // 记录玩家学习海报
    private static void recordPosterLearning(Player player, String posterType, PosterManager.PosterData data) {
        // 检查玩家是否已学习
        if (!PosterLearningData.hasLearned(player, posterType)) {
            // 记录学习状态
            PosterLearningData.markAsLearned(player, posterType);

            // 添加概念
            String conceptKey = data.getConceptKey();
            if (!conceptKey.isEmpty()) {
                KnowledgeGraphManager.getInstance().addConcept(conceptKey);
                player.sendMessage(new TextComponent("你学习了新概念: " + data.getTitle()), player.getUUID());

                // 生成题目
                spawnTestQuestion(player, data.getQuestionId());

                LOGGER.info("Player " + player.getName().getString() + " learned new concept: " + conceptKey);
            }
        }
    }

    // 生成测试题目物品
    private static void spawnTestQuestion(Player player, String questionId) {
        LOGGER.info("Attempting to spawn test question entity, questionId: " + questionId);

        if (questionId == null || questionId.isEmpty()) {
            LOGGER.warning("QuestionId is null or empty, cannot spawn test question");
            return;
        }

        // 创建物品
        Item questionItem = null;

        if (questionId.equals("mc_agent_definition")) {
            questionItem = ItemRegistry.AGENT_DEFINITION.get();
        } else if (questionId.equals("match_llm_concepts")) {
            questionItem = ItemRegistry.AGENT_LLMS.get();
        } else if (questionId.equals("tf_tools_concept")) {
            questionItem = ItemRegistry.AGENT_TOOLS.get();
        } else if (questionId.equals("order_workflow_steps")) {
            questionItem = ItemRegistry.AGENT_WORKFLOW.get();
        }

        if (questionItem == null) {
            LOGGER.warning("Failed to get item for questionId: " + questionId);
            return;
        }

        // 创建物品堆栈
        ItemStack itemStack = new ItemStack(questionItem);

        // 客户端代码不处理实体的创建，只在服务端处理
        Level level = player.level;
        if (!level.isClientSide) {
            try {
                // 计算玩家前方的位置（距离玩家约1.5个方块）
                double offsetDistance = 1.5;
                Vec3 lookVec = player.getLookAngle().normalize();

                double spawnX = player.getX() + (lookVec.x * offsetDistance);
                double spawnY = player.getY() + player.getEyeHeight() - 0.3; // 稍微低于眼睛高度
                double spawnZ = player.getZ() + (lookVec.z * offsetDistance);

                // 创建物品实体
                net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                        level,
                        spawnX,
                        spawnY,
                        spawnZ,
                        itemStack
                );

                // 设置物品属性
                itemEntity.setPickUpDelay(10); // 短暂的拾取延迟，防止立即被捡起
                itemEntity.lifespan = 6000; // 设置较长的生存时间（5分钟）

                // 使物品保持相对静止状态（减少随机移动）
                itemEntity.setDeltaMovement(Vec3.ZERO);

                // 添加到世界
                boolean success = level.addFreshEntity(itemEntity);

                if (success) {
                    LOGGER.info("Successfully spawned item entity in front of player at: " +
                            spawnX + ", " + spawnY + ", " + spawnZ);

                    // 通知玩家
                    player.sendMessage(new TextComponent("你获得了一个测试题目！请拾取它。"), player.getUUID());

                    // 播放物品生成音效
                    level.playSound(null, spawnX, spawnY, spawnZ,
                            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.75F, 1.0F);

                    // 在物品位置添加粒子效果
                    if (level instanceof ServerLevel serverLevel) {
                        // 创建强烈的粒子效果
                        serverLevel.sendParticles(
                                ParticleTypes.TOTEM_OF_UNDYING,  // 图腾粒子非常明显
                                spawnX,
                                spawnY + 0.3, // 稍微在物品上方
                                spawnZ,
                                20,  // 较多的粒子数量
                                0.2, 0.3, 0.2,  // 垂直方向扩散多一点
                                0.1  // 较慢速度让粒子持续更久
                        );

                        // 再添加一层环形粒子
                        for (int i = 0; i < 8; i++) {
                            double angle = i * Math.PI / 4; // 均匀分布在圆周上
                            double offsetX = Math.cos(angle) * 0.3;
                            double offsetZ = Math.sin(angle) * 0.3;

                            serverLevel.sendParticles(
                                    ParticleTypes.END_ROD,  // 末地烛粒子有光柱效果
                                    spawnX + offsetX,
                                    spawnY,
                                    spawnZ + offsetZ,
                                    1,  // 每个位置只需要一个粒子
                                    0.0, 0.05, 0.0,  // 只在垂直方向有少量扩散
                                    0.02  // 非常慢的速度
                            );
                        }
                    }
                } else {
                    LOGGER.warning("Failed to spawn item entity in the world");
                }
            } catch (Exception e) {
                LOGGER.severe("Error spawning item entity: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 辅助方法：根据类型获取海报数据
    private static PosterManager.PosterData getPosterDataFromType(String posterType) {
        switch (posterType) {
            case "agent_definition":
                return PosterManager.getInstance().getAgentDefinitionPosterData();
            case "llms":
                return PosterManager.getInstance().getLLMsPosterData();
            case "tools":
                return PosterManager.getInstance().getToolsPosterData();
            case "workflow":
                return PosterManager.getInstance().getWorkflowPosterData();
            default:
                return null;
        }
    }
}