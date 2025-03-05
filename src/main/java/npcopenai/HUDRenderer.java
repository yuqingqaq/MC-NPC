package npcopenai;

import component.academic.TaskHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    public static void onMouseClick(PlayerInteractEvent.LeftClickEmpty event) {
        // 获取鼠标位置
        double mouseX = Minecraft.getInstance().mouseHandler.xpos();
        double mouseY = Minecraft.getInstance().mouseHandler.ypos();

        // 检查鼠标点击位置是否在任务标题上
        if (mouseX >= 10 && mouseX <= 100 && mouseY >= 50 && mouseY <= 65) {
            taskHUD.toggleExpand();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        // 检查按键是否为指定的键（例如：G键）
        if (event.getKey() == GLFW.GLFW_KEY_G && event.getAction() == GLFW.GLFW_PRESS) {
            taskHUD.toggleExpand();
        }
    }
}