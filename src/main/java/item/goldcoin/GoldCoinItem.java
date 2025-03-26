package item.goldcoin;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import util.TooltipUtil;


public class GoldCoinItem extends Item {
    public GoldCoinItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC)); // 添加到杂项创造模式标签
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipUtil.addGoldCoinTooltip(stack, level, tooltip);
    }
}