package entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;


public class ProfessorNPCEntityRenderer<T extends Mob> extends MobRenderer<T, VillagerModel<T>> {
    private static final ResourceLocation PROFESSOR_TEXTURE = new ResourceLocation("npcopenai", "textures/entity/villager/profession/engineer.png");

    public ProfessorNPCEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return PROFESSOR_TEXTURE;
    }
}