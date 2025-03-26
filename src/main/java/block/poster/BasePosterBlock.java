package block.poster;

import block.poster.client.PosterViewClientHandler;
import controller.KnowledgeGraphManager;
import controller.PosterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import registry.ItemRegistry;

import javax.annotation.Nullable;
import java.util.logging.Logger;

public abstract class BasePosterBlock extends Block implements EntityBlock {
    private static final Logger LOGGER = Logger.getLogger("BasePosterBlock");

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // 定义不同方向的碰撞箱
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_WEST = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 2, 16, 16);

    public BasePosterBlock() {
        super(Properties.of(Material.WOOD)
                .strength(2.0f)
                .sound(SoundType.WOOD)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // 获取海报类型，用于查找海报数据
    protected abstract String getPosterType();

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            // 客户端：直接根据海报类型获取数据并打开UI
            openPosterViewScreen(player, getPosterType());
            return InteractionResult.SUCCESS;
        } else {
            // 服务器端：记录玩家已读并生成题目
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BasePosterBlockEntity posterEntity) {
                // 检查玩家是否已经学习过这个海报
                if (!posterEntity.hasBeenReadBy(player.getUUID())) {
                    // 标记为已读
                    posterEntity.markAsRead(player.getUUID());

                    // 获取对应的海报数据
                    PosterManager.PosterData data = getPosterDataFromPosterType(getPosterType());
                    if (data != null) {
                        // 添加相应的知识概念
                        String conceptKey = data.getConceptKey();
                        if (!conceptKey.isEmpty()) {
                            KnowledgeGraphManager.getInstance().addConcept(conceptKey);
                            player.sendMessage(new TextComponent("你学习了新概念: " + data.getTitle()), player.getUUID());

                            // 生成对应的测试题目物品
                            spawnTestQuestion(level, pos, data.getQuestionId());
                        }
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
    }

    // 根据海报类型获取海报数据
    private PosterManager.PosterData getPosterDataFromPosterType(String posterType) {
        if (posterType.equals("智能体基础概念")) {
            return PosterManager.getInstance().getAgentBasicPosterData();
        } else if (posterType.equals("智能体技术演化")) {
            return PosterManager.getInstance().getAgentEvolutionPosterData();
        } else if (posterType.equals("智能体学习方法")) {
            return PosterManager.getInstance().getAgentLearningPosterData();
        } else if (posterType.equals("智能体设计原则")) {
            return PosterManager.getInstance().getAgentPrinciplesPosterData();
        }
        return null;
    }

    // 生成测试题目物品
    private void spawnTestQuestion(Level level, BlockPos pos, String questionId) {
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
            LOGGER.warning("未找到匹配的测试题目物品: " + questionId);
            return;
        }

        LOGGER.info("生成测试题目物品: " + questionId);

        // 在海报前方生成物品实体
        BlockState state = level.getBlockState(pos);
        Direction facing = state.getValue(FACING);
        BlockPos spawnPos = pos.relative(facing);

        // 调整物品生成位置，使其出现在海报前方
        double x = spawnPos.getX() + 0.5;
        double y = spawnPos.getY() + 0.5;
        double z = spawnPos.getZ() + 0.5;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        // 设置物品不会消失
        itemEntity.setUnlimitedLifetime();
        // 添加特效
        itemEntity.setGlowingTag(true);

        level.addFreshEntity(itemEntity);
    }

    // 打开海报查看界面 - 直接从PosterManager获取最新数据
    private void openPosterViewScreen(Player player, String posterType) {
        PosterManager.PosterData data = getPosterDataFromPosterType(posterType);

        if (data != null) {
            // 直接从PosterManager获取的最新数据打开屏幕
            PosterViewClientHandler.openPosterScreen(
                    data.getTitle(),
                    data.getContent(),
                    data.getImagePath(),
                    data.getExpertType()
            );
        } else {
            // 找不到数据，显示错误信息
            LOGGER.warning("无法找到海报数据：" + posterType);
            PosterViewClientHandler.openErrorScreen(posterType);
        }
    }

    // 旋转方块相关方法
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}