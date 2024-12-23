package gui.map;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyMappingHandler {
    public static final String CATEGORY = "key.categories.npcopenai";
    public static final String TOGGLE_MAP = "key.npcopenai.toggle_map";

    public static KeyMapping toggleMapKey;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 创建按键映射实例
        toggleMapKey = new KeyMapping(
                TOGGLE_MAP, // 按键描述
                InputConstants.Type.KEYSYM, // 按键类型
                GLFW.GLFW_KEY_M, // 默认绑定 M 键
                CATEGORY // 按键分类
        );

        // 注册按键映射
        ClientRegistry.registerKeyBinding(toggleMapKey);
    }
}