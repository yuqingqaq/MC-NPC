package entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import entity.LibrarianNPCEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LibrarianLayer extends RenderLayer<LibrarianNPCEntity, VillagerModel<LibrarianNPCEntity>> {
    public final static ResourceLocation LIBRARIAN_TEXTURE = new ResourceLocation("minecraft", "textures/entity/villager/profession/librarian.png");

    public LibrarianLayer(RenderLayerParent<LibrarianNPCEntity, VillagerModel<LibrarianNPCEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, LibrarianNPCEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            renderColoredCutoutModel(this.getParentModel(), LIBRARIAN_TEXTURE, matrixStack, buffer, packedLight, entity, 1.0F, 1.0F, 1.0F);
        }
    }
}
