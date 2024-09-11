package entity;

import entity.layers.ProfPlainLayer;
import entity.layers.ProfessorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;


public class ProfessorNPCEntityRenderer extends MobRenderer<ProfessorNPCEntity, VillagerModel<ProfessorNPCEntity>> {
    private static final ResourceLocation PROFESSOR_TEXTURE = new ResourceLocation("npcopenai", "textures/entity/villager/villager.png");

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