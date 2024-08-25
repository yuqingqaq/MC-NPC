package entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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

//public class NPCEntityRenderer<T extends Mob> extends MobRenderer<T, VillagerModel<T>> {
//    private static final ResourceLocation LIBRARIAN_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/profession/librarian.png");
//
//    public NPCEntityRenderer(EntityRendererProvider.Context context) {
//        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);    }
//
//    @Override
//    public ResourceLocation getTextureLocation(T entity) {
//        return LIBRARIAN_TEXTURE;
//    }
//}
public class NPCEntityRenderer extends VillagerRenderer {
    private static final ResourceLocation CUSTOM_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/profession/librarian.png");

    public NPCEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Villager entity) {
        // 你可以根据实体的状态或属性返回不同的纹理
        return CUSTOM_TEXTURE;
    }
}