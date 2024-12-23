package item;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import registry.ItemRegistry;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinPickupHandler {

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof Player player) {
            System.out.println("EntityItemPickupEvent triggered!");
            // 物品拾取逻辑在服务端处理
            if (!player.level.isClientSide()) {
                System.out.println("EntityItemPickupEvent ServerSide");
                // 获取拾取的物品实体
                ItemStack pickedUpItem = event.getItem().getItem(); // 获取物品堆栈
                System.out.println("ItemRegistry.GOLD_COIN.get()"  + ItemRegistry.GOLD_COIN.get());

                // 检查是否为自定义金币
                if (pickedUpItem.getItem() == ItemRegistry.GOLD_COIN.get()) {
                    int goldCoinCount = pickedUpItem.getCount(); // 获取金币数量
                    // 显示提示信息到客户端
                    player.displayClientMessage(new TextComponent("You picked up a gold coin! Total: " + goldCoinCount), false);
                }
            }
        }
    }
}