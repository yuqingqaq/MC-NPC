package entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import entity.LibrarianNPCEntity;
import entity.ProfessorNPCEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class ProfPlainLayer extends RenderLayer<ProfessorNPCEntity, VillagerModel<ProfessorNPCEntity>> {
    public final static ResourceLocation PLAIN_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/type/plains.png");

    public ProfPlainLayer(RenderLayerParent<ProfessorNPCEntity, VillagerModel<ProfessorNPCEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, ProfessorNPCEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            renderColoredCutoutModel(this.getParentModel(), PLAIN_TEXTURE, matrixStack, buffer, packedLight, entity, 1.0F, 1.0F, 1.0F);
        }
    }
}
