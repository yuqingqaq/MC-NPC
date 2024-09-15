package com.freedomai.projectpn.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.freedomai.projectpn.entity.ProfessorNPCEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class ProfessorLayer extends RenderLayer<ProfessorNPCEntity, VillagerModel<ProfessorNPCEntity>> {
    public final static ResourceLocation PROF_TEXTURE = new ResourceLocation("projectpn", "textures/entity/villager/profession/engineer.png");

    public ProfessorLayer(RenderLayerParent<ProfessorNPCEntity, VillagerModel<ProfessorNPCEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, ProfessorNPCEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            renderColoredCutoutModel(this.getParentModel(), PROF_TEXTURE, matrixStack, buffer, packedLight, entity, 1.0F, 1.0F, 1.0F);
        }
    }
}
