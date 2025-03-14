package npcopenai;

import component.adaptive.TaskHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "npcopenai")
public class HUDRenderer {
    private static TaskHUD taskHUD = new TaskHUD(Minecraft.getInstance());

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            PoseStack poseStack = event.getMatrixStack();
            taskHUD.render(poseStack);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            taskHUD.tick();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_G && event.getAction() == GLFW.GLFW_PRESS) {
            taskHUD.toggleAllTasks();        }
    }
}