package item.poster;

import controller.PosterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Logger;

public abstract class BasePosterItem extends Item {
    private static final Logger LOGGER = Logger.getLogger("BasePosterItem");

    public BasePosterItem() {
        super(new Properties().tab(CreativeModeTab.TAB_DECORATIONS).stacksTo(16));
    }

    // 子类必须实现的方法：获取海报类型
    protected abstract String getPosterType();

    // 子类可选重写：获取海报显示名称
    protected String getPosterDisplayName() {
        return getPosterType();
    }

    // 添加提示信息
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        PosterManager.PosterData data = getPosterDataFromType(getPosterType());
        if (data != null) {
            tooltip.add(new TextComponent("§6" + data.getTitle()));
            tooltip.add(new TextComponent("§7由 " + data.getExpertType() + " 撰写"));
            tooltip.add(new TextComponent("§8右键墙面放置，再次右键查看"));
        } else {
            tooltip.add(new TextComponent("§6" + getPosterDisplayName()));
            tooltip.add(new TextComponent("§8右键墙面放置，再次右键查看"));
        }
    }

    // 右键使用，放置在墙上
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        // 检查点击的是墙面（不是地板或天花板）
        if (face == Direction.UP || face == Direction.DOWN) {
            return InteractionResult.PASS;
        }

        // 计算相框放置位置
        BlockPos framePos = clickedPos.relative(face);

        // 检查该位置是否已有方块
        if (!level.getBlockState(framePos).isAir()) {
            return InteractionResult.PASS;
        }

        // 只在服务端处理实体创建
        if (!level.isClientSide) {
            // 创建普通物品展示框
            ItemFrame frame = new ItemFrame(level, framePos, face);

            // 设置海报类型 NBT（用于后续识别）
            CompoundTag posterTag = new CompoundTag();
            posterTag.putString("PosterType", this.getPosterType());

            // 复制物品并设置 NBT
            ItemStack framePosterItem = itemStack.copy();
            framePosterItem.setCount(1);

            CompoundTag itemTag = framePosterItem.getOrCreateTag();
            itemTag.put("PosterData", posterTag);

            // 将海报物品放入框架
            frame.setItem(framePosterItem, false);

            // 设置框架为不可见（只显示内容）
            frame.setInvisible(true);

            // 添加到世界
            boolean success = level.addFreshEntity(frame);

            if (success) {
                // 如果成功添加，减少玩家手中的物品数量
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                // 播放放置音效
                level.playSound(null, framePos, SoundEvents.ITEM_FRAME_ADD_ITEM,
                        SoundSource.BLOCKS, 1.0F, 1.0F);

                LOGGER.info("Successfully placed poster frame of type: " + getPosterType() + " at " + framePos);
                return InteractionResult.SUCCESS;
            } else {
                LOGGER.warning("Failed to place poster frame: " + getPosterType());
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 辅助方法：根据类型获取海报数据
    protected PosterManager.PosterData getPosterDataFromType(String posterType) {
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