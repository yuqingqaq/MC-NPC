package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import system.StageManager.StageInfo;
import system.StageManager;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCoinGuide {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        // 确保逻辑只在服务端执行
        if (!player.level.isClientSide && event.phase == TickEvent.Phase.END) {
            BlockPos playerPos = player.blockPosition();

            // 获取当前阶段的金币
            StageManager stageManager = GameController.getInstance().getStageManager();
            StageInfo currentStage = stageManager.getCurrentStage();

            if (currentStage != null) {
                BlockPos nearestCoin = GoldCoinTracker.findNearestCoinForStage(playerPos, currentStage.getId());

                // 此处注释掉的代码保留但不启用，以保持与原代码一致
                /*
                if (nearestCoin != null) {
                    // 显示最近金币的位置到动作栏
                    player.displayClientMessage(
                            new TextComponent("最近的金币位置: " + nearestCoin.toShortString()), true
                    );
                } else {
                    player.displayClientMessage(new TextComponent("当前阶段没有金币!"), true);
                }
                */
            }
        }
    }
}