package npcopenai;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PersistentToast implements Toast {
    private final TutorialToast.Icons icon;
    private final TextComponent title;
    private final TextComponent message;
    private Toast.Visibility visibility;
    private float progress;
    private final boolean progressable;

    public PersistentToast(TutorialToast.Icons icon, String title, String message, boolean progressable) {
        this.icon = icon;
        this.title = new TextComponent(title);
        this.message =new TextComponent(message);
        this.visibility = Visibility.SHOW;
        this.progressable = progressable;
        this.progress = 0.0F;
    }

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent toastGui, long delta) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        toastGui.blit(poseStack, 0, 0, 0, 96, this.width(), this.height());
        icon.render(poseStack, toastGui, 6, 6);

        if (message == null) {
            toastGui.getMinecraft().font.draw(poseStack, title, 30.0F, 12.0F, -11534256);
        } else {
            toastGui.getMinecraft().font.draw(poseStack, title, 30.0F, 7.0F, -11534256);
            toastGui.getMinecraft().font.draw(poseStack, message, 30.0F, 18.0F, -16777216);
        }

        if (progressable) {
            GuiComponent.fill(poseStack, 3, 28, 157, 29, -1);
            int color = progress >= 1.0F ? -16755456 : -11206656;
            GuiComponent.fill(poseStack, 3, 28, (int)(3.0F + 154.0F * progress), 29, color);
        }

        return visibility;
    }

    public void updateProgress(float newProgress) {
        this.progress = newProgress;
        if (newProgress >= 1.0F) {
            this.visibility = Visibility.HIDE;
        }
    }

    public void hide() {
        this.visibility = Visibility.HIDE;
    }

}