package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
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

                    // 获取金币的位置
                    BlockPos coinPos = event.getItem().blockPosition();
                    System.out.println("Trying to remove coin at position: " + coinPos);

                    // 列出所有追踪的位置
                    System.out.println("Tracked positions before removal: " + GoldCoinTracker.getCoinPositions());

                    // 从追踪器中移除位置
                    boolean removed = GoldCoinTracker.removeCoinPosition(coinPos);

                    if (removed) {
                        System.out.println("Successfully removed coin at position: " + coinPos);
                    } else {
                        System.out.println("Failed to remove coin at position: " + coinPos);
                    }

                    // 获取剩余金币数量
                    int remainingCoins = GoldCoinTracker.getCoinPositions().size();
                    System.out.println("Tracked positions after removal: " + GoldCoinTracker.getCoinPositions());
                    System.out.println("Remaining gold coins: " + remainingCoins);

                    // 显示提示信息到客户端
                    player.displayClientMessage(
                            new TextComponent("You picked up a gold coin! Left Total: " + remainingCoins), false
                    );

                    System.out.println("Remaining gold coins: " + remainingCoins);

                }

            }
        }
    }
}