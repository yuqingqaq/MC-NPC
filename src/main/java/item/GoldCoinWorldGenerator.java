package item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GoldCoinWorldGenerator {

    private static boolean hasGenerated = false; // 防止多次生成

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 确保当前是游戏运行状态（不是菜单界面等）
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.level.isClientSide && !hasGenerated) {
                hasGenerated = true; // 确保只生成一次
                System.out.println("GoldCoinWorldGenerator: Generating gold coins...");
                // 在 (0, 64, 0) 附近生成金币
                BlockPos center = new BlockPos(0, 32, 0);
                GoldCoinSpawner.spawnGoldCoins((ClientLevel) mc.level, center, 50, 20); // 半径50，生成20个金币
            }
        }
    }
}