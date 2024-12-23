package gui.map;

import gui.map.GlobalMapScreen;
import gui.map.KeyMappingHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "npcopenai", value = Dist.CLIENT)
public class MapKeyInputHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();

            // 检测按键是否被按下
            if (KeyMappingHandler.toggleMapKey.consumeClick()) {
                System.out.println("Pressed to toggle map key");
                mc.setScreen(new GlobalMapScreen()); // 打开全局地图界面
            }
        }
    }
}