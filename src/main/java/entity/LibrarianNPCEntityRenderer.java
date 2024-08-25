package entity;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class LibrarianNPCEntityRenderer<T extends Mob> extends MobRenderer<T, VillagerModel<T>> {
    private static final ResourceLocation CUSTOM_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/profession/librarian.png");

    public LibrarianNPCEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return CUSTOM_TEXTURE;
    }
}