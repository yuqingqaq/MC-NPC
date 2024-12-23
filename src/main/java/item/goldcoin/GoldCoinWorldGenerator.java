package item.goldcoin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static item.goldcoin.GoldCoinSpawner.spawnGoldCoins;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldCoinWorldGenerator {

    private static boolean hasGenerated = false; // 防止重复生成

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        // 确保只在服务端执行
        if (event.getWorld() instanceof ServerLevel serverLevel && !hasGenerated) {
            hasGenerated = true; // 确保只生成一次
            System.out.println("GoldCoinWorldGenerator: Generating gold coins on the server...");

            // 定义生成的中心点
            BlockPos center = new BlockPos(0, 30, 0);
            spawnGoldCoins(serverLevel, center, 10, 5); // 半径50，生成20个金币
        }
    }


}