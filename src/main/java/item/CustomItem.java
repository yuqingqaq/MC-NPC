package item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import controller.GameController;
import model.NPCModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import util.TooltipUtil;
import java.util.List;

import javax.annotation.Nullable;

import npcopenai.NPCOpenAI;

public class CustomItem extends Item {
    public CustomItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("item.CustomItem used on: " + context.getClickedPos());

        if (world.isClientSide) {
            NPCOpenAI.getLogger().info("Executing on client side");
            if (context.getPlayer() != null) {
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CustomItemClientHandler::openNPCTaskScreen);            } else {
                NPCOpenAI.getLogger().info("No player found");
            }
        } else {
            NPCOpenAI.getLogger().info("This action is server-side, passing");
        }
        return InteractionResult.PASS;
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 使用 TooltipUtil 添加 Tooltip
        TooltipUtil.addCustomItemTooltip(stack, level, tooltip);
    }

}