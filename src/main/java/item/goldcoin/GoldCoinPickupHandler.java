package item.goldcoin;

import controller.GameController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import registry.ItemRegistry;
import system.StageManager.StageInfo;
import system.StageManager;
import system.TaskManager;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinPickupHandler {

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level.isClientSide()) {
            return; // 只在服务端处理玩家拾取事件
        }

        // 获取拾取的物品
        ItemStack pickedUpItem = event.getItem().getItem();

        // 检查是否为自定义金币
        if (pickedUpItem.getItem() != ItemRegistry.GOLD_COIN.get()) {
            return; // 不是金币，忽略
        }

        // 获取金币位置并移除
        BlockPos coinPos = event.getItem().blockPosition();
        GoldCoinTracker.removeCoinPosition(coinPos);

        // 获取阶段管理器
        StageManager stageManager = GameController.getInstance().getStageManager();
        StageInfo currentStage = stageManager.getCurrentStage();


        // 进入下一阶段
        stageManager.advanceToNextStage();
        StageInfo nextStage = stageManager.getCurrentStage();

        // 如果有下一阶段且有金币位置，生成新金币
        if (nextStage != null && nextStage.getCoinLocation() != null && !nextStage.getCoinLocation().isEmpty()) {
            try {
                String[] coords = nextStage.getCoinLocation().split(",");
                if (coords.length == 3) {
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());
                    int z = Integer.parseInt(coords[2].trim());

                    // 生成新金币
                    GoldCoinSpawner.spawnGoldCoin(
                            (ServerLevel) player.level,
                            new BlockPos(x, y, z),
                            nextStage.getId()
                    );

                }
            } catch (Exception e) {
                System.out.println("Error spawning coin for stage " + nextStage.getId() + ": " + e.getMessage());
            }
        } else {
            // 任务完成
            player.displayClientMessage(
                    new TextComponent("恭喜你完成了收集到了所有校徽碎片!"),
                    false
            );

            TaskManager.getInstance().completeSubTaskByTitle("收集校徽碎片", true);
            player.displayClientMessage(
                    new TextComponent("Subtask '收集校徽碎片' completed!"),
                    false
            );        }
    }
}