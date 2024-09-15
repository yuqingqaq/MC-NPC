package com.freedomai.projectpn.renderer;

import com.freedomai.projectpn.renderer.layers.ProfPlainLayer;
import com.freedomai.projectpn.renderer.layers.ProfessorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.freedomai.projectpn.entity.ProfessorNPCEntity;

public class ProfessorNPCEntityRenderer extends MobRenderer<ProfessorNPCEntity, VillagerModel<ProfessorNPCEntity>> {
    private static final ResourceLocation PROFESSOR_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/villager.png");

    public ProfessorNPCEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
        this.addLayer(new ProfPlainLayer(this));
        this.addLayer(new ProfessorLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ProfessorNPCEntity entity) {
        // 你可以根据实体的状态或属性返回不同的纹理
        return PROFESSOR_TEXTURE;
    }
}