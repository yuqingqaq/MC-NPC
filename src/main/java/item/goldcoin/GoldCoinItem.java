package item.goldcoin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;

public class GoldCoinItem extends Item {
    public GoldCoinItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC)); // 添加到杂项创造模式标签
    }
}