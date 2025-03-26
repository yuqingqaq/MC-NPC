package item.poster;

import block.poster.AcademicPosterBlockEntity;
import controller.PosterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

import java.util.logging.Logger;

public abstract class BasePosterItem extends BlockItem {
    protected static final Logger LOGGER = Logger.getLogger("PosterItem");

    public BasePosterItem() {
        super(BlockRegistry.ACADEMIC_POSTER.get(), new Properties().tab(CreativeModeTab.TAB_DECORATIONS));
    }

    // 应用海报数据的抽象方法，由子类实现
    protected abstract void applyPosterData(AcademicPosterBlockEntity entity);

    // 获取海报类型名称（用于日志）
    protected abstract String getPosterTypeName();

    @Override
    public InteractionResult useOn(UseOnContext context) {
        LOGGER.info("正在放置" + getPosterTypeName() + "海报");

        // 检查是否可以放置在目标位置
        BlockPlaceContext blockPlaceContext = new BlockPlaceContext(context);
        if (blockPlaceContext.getClickedFace() != Direction.UP &&
                blockPlaceContext.getClickedFace() != Direction.DOWN) {

            // 调用原BlockItem的放置逻辑
            InteractionResult result = super.useOn(context);

            if (result.consumesAction()) {
                // 放置成功，设置海报数据
                Level level = context.getLevel();
                BlockPos pos = blockPlaceContext.getClickedPos();
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof AcademicPosterBlockEntity posterEntity) {
                    // 应用海报数据 - 调用子类实现的方法
                    LOGGER.info("正在应用" + getPosterTypeName() + "海报数据到位置: " + pos);
                    applyPosterData(posterEntity);

                    // 如果是玩家放置的，减少物品数量
                    Player player = context.getPlayer();
                    if (player != null && !player.getAbilities().instabuild) {
                        ItemStack itemStack = context.getItemInHand();
                        itemStack.shrink(1);
                    }
                } else {
                    LOGGER.severe("放置海报失败: 在位置 " + pos + " 没有找到海报方块实体");
                }
            }
            return result;
        }

        return InteractionResult.PASS; // 不能放在顶部或底部
    }
}