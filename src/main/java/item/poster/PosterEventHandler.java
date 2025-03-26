package item.poster;

import controller.KnowledgeGraphManager;
import controller.PosterLearningData;
import controller.PosterManager;
import item.poster.client.PosterViewClientHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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

//    // 防止物品展示框被攻击破坏
//    @SubscribeEvent
//    public static void onEntityAttack(AttackEntityEvent event) {
//        Entity target = event.getTarget();
//
//        // 检查目标实体是否是物品展示框
//        if (target instanceof ItemFrame frame) {
//            ItemStack frameItem = frame.getItem();
//
//            // 如果是我们的海报，阻止破坏
//            if (!frameItem.isEmpty() && frameItem.hasTag()) {
//                CompoundTag tag = frameItem.getTag();
//                if (tag != null && tag.contains("PosterData")) {
//                    // 取消攻击事件
//                    event.setCanceled(true);
//                }
//            }
//        }
//    }

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
        if (questionId == null || questionId.isEmpty()) return;

        // 根据海报关联的概念，选择正确的测试题目物品
        ItemStack itemStack = null;

        // 如果是预定义的问题ID，使用对应的预注册物品
        if (questionId.equals("mc_agent_concept")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_CONCEPT.get());
        } else if (questionId.equals("match_learning_methods")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_RELATIONSHIP.get());
        } else if (questionId.equals("tf_learning_principle")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_PRINCIPLE.get());
        } else if (questionId.equals("order_tech_evolution")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_TIMELINE.get());
        }

        // 如果没有匹配的预定义物品，返回
        if (itemStack == null) {
            return;
        }

        // 将物品掉落在地上
            player.drop(itemStack, false);

    }

    // 辅助方法：根据类型获取海报数据
    private static PosterManager.PosterData getPosterDataFromType(String posterType) {
        if (posterType.equals("agent_basic")) {
            return PosterManager.getInstance().getAgentBasicPosterData();
        } else if (posterType.equals("agent_evolution")) {
            return PosterManager.getInstance().getAgentEvolutionPosterData();
        } else if (posterType.equals("agent_learning")) {
            return PosterManager.getInstance().getAgentLearningPosterData();
        } else if (posterType.equals("agent_principles")) {
            return PosterManager.getInstance().getAgentPrinciplesPosterData();
        }
        return null;
    }
}