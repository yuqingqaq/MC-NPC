package entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import entity.LibrarianNPCEntity;
import entity.layers.LibPlainLayer;
import entity.layers.LibrarianLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static net.minecraft.client.gui.GuiComponent.fill;

public class LibrarianNPCEntityRenderer extends MobRenderer<LibrarianNPCEntity, VillagerModel<LibrarianNPCEntity>> {
    private static final ResourceLocation CUSTOM_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/villager.png");

    public LibrarianNPCEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
        this.addLayer(new LibPlainLayer(this));
        this.addLayer(new LibrarianLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(LibrarianNPCEntity entity) {
        return CUSTOM_TEXTURE;
    }

    @Override
    public void render(LibrarianNPCEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        if (entity.isTalking()) {
            renderSpeechBubble(entity, poseStack, bufferSource, packedLight);
        }
    }

    private void renderSpeechBubble(LibrarianNPCEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() + 0.75, 0.0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        String text = entity.getCurrentSpeechText();
        int opacity = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        int width = this.getFont().width(text) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = poseStack.last().pose();
        fill(poseStack, -width - 1, -1, width + 1, 10, 0x20FFFFFF | opacity);
        RenderSystem.enableTexture();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        this.getFont().draw(poseStack, text, -width, 1, 0xFFFFFF | opacity);
        poseStack.popPose();
    }
}