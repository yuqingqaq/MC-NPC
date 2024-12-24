package item.goldcoin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = "npcopenai", bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class HUDPathRenderer {

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        // 获取当前目标位置
        BlockPos targetPos = PlayerPathGuide.getCurrentTarget();
        if (targetPos == null) return;

        // 计算玩家位置和目标位置
        Vec3 playerPos = player.position();
        Vec3 targetVec = new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        // 计算目标方向
        Vec3 direction = targetVec.subtract(playerPos).normalize();
        double angle = Math.atan2(direction.z, direction.x) - Math.toRadians(player.getYRot());
        angle = Math.toDegrees(angle);
        if (angle < 0) angle += 360;

        // 计算距离
        double distance = playerPos.distanceTo(targetVec);

        // 渲染箭头
        renderArrow(event.getMatrixStack(), angle, distance);
    }

    private static void renderArrow(PoseStack poseStack, double angle, double distance) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int arrowX = screenWidth / 2 + 150;
        int arrowY = screenHeight / 2; // 箭头在屏幕中心上方

        // 使用 PoseStack 管理矩阵变换
        poseStack.pushPose();
        poseStack.translate(arrowX, arrowY, 0); // 平移到箭头绘制位置
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees((float) angle));
        poseStack.scale(1.0f, 1.0f, 1.0f); // 放大箭头

        // 渲染箭头纹理
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new net.minecraft.resources.ResourceLocation("npcopenai", "textures/gui/arrow.png"));
        GuiComponent.blit(poseStack, -8, -8, 0, 0, 16, 16, 16, 16);

        poseStack.popPose(); // 恢复矩阵状态

        // 渲染距离
        //String distanceText = String.format("Distance: %.1f blocks", distance);
        //mc.font.draw(poseStack, distanceText, screenWidth / 2 - mc.font.width(distanceText) / 2, screenHeight / 2 + 30, 0xFFFFFF);
    }
}