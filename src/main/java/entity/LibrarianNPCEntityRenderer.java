package entity;

import entity.layers.LibPlainLayer;
import entity.layers.LibrarianLayer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

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
}