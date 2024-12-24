package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCoinGuide {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        // 确保逻辑只在服务端执行
        if (!player.level.isClientSide && event.phase == TickEvent.Phase.END) {
            BlockPos playerPos = player.blockPosition();
            BlockPos nearestCoin = GoldCoinTracker.findNearestCoin(playerPos);

//            if (nearestCoin != null) {
//                // 显示最近金币的位置到动作栏
//                player.displayClientMessage(
//                        new TextComponent("Nearest gold coin at: " + nearestCoin.toShortString()), true
//                );
//            } else {
//                player.displayClientMessage(new TextComponent("No gold coins nearby!"), true);
//            }
        }
    }

}