package block.poster;

import block.poster.client.PosterViewClientHandler;
import controller.KnowledgeGraphManager;
import item.knowledge.QuestionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import registry.ItemRegistry;

import java.util.logging.Logger;

public class AcademicPosterBlock extends Block implements EntityBlock {
    private static final Logger LOGGER = Logger.getLogger("AcademicPosterBlock");

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // 定义不同方向的碰撞箱
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_WEST = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 2, 16, 16);

    public AcademicPosterBlock() {
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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AcademicPosterBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AcademicPosterBlockEntity posterEntity) {
                // 调试输出
                LOGGER.info("客户端：点击海报 '" + posterEntity.getTitle() +
                        "' 位于 " + pos);

                // 打开海报内容查看界面
                openPosterViewScreen(player, posterEntity);
                return InteractionResult.SUCCESS;
            }
        } else {
            // 服务器端逻辑
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AcademicPosterBlockEntity posterEntity) {
                // 调试输出
                LOGGER.info("服务器端：点击海报 '" + posterEntity.getTitle() +
                        "' 位于 " + pos +
                        ", 概念: " + posterEntity.getConceptKey() +
                        ", 问题ID: " + posterEntity.getAssociatedQuestionId());

                // 检查玩家是否已经学习过这个海报
                if (!posterEntity.hasBeenReadBy(player.getUUID())) {
                    // 标记为已读
                    posterEntity.markAsRead(player.getUUID());

                    // 添加相应的知识概念
                    String conceptKey = posterEntity.getConceptKey();
                    if (!conceptKey.isEmpty()) {
                        KnowledgeGraphManager.getInstance().addConcept(conceptKey);
                        player.sendMessage(new TextComponent("你学习了新概念: " + posterEntity.getTitle()), player.getUUID());

                        // 生成对应的测试题目物品
                        spawnTestQuestion(level, pos, posterEntity.getAssociatedQuestionId());
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    // 生成测试题目物品
    private void spawnTestQuestion(Level level, BlockPos pos, String questionId) {
        if (questionId == null || questionId.isEmpty()) return;

        // 根据海报关联的概念，选择正确的测试题目物品
        ItemStack itemStack = null;

        // 如果是预定义的问题ID，使用对应的预注册物品
        if (questionId.equals("q_agent_basic")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_CONCEPT.get());
        } else if (questionId.equals("q_agent_types")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_RELATIONSHIP.get());
        } else if (questionId.equals("q_agent_applications")) {
            itemStack = new ItemStack(ItemRegistry.AGENT_PRINCIPLE.get());
        } else if (questionId.equals("q_agent_learning")) {
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

    // 打开海报查看界面
    private void openPosterViewScreen(Player player, AcademicPosterBlockEntity entity) {
        // 客户端代码，通过NetworkHandler或直接打开屏幕
        PosterViewClientHandler.openPosterScreen(entity.getTitle(), entity.getContent(),
                entity.getImagePath(), entity.getExpertType());
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