package npcopenai;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

public class TimedToast implements Toast {
    private final TextComponent title;
    private final TextComponent message;
    private final long displayTime;  // Toast display duration in milliseconds
    private long firstDrawTime = 0;  // Time when the toast was first drawn

    public TimedToast(String title, String message, long displayTime) {
        this.title = new TextComponent(title);
        this.message = new TextComponent(message);
        this.displayTime = displayTime;
    }

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent toastGui, long delta) {
        if (this.firstDrawTime == 0) {
            this.firstDrawTime = delta;
        }

        Minecraft.getInstance().getTextureManager().bindForSetup(TEXTURE);
        GuiComponent.blit(poseStack, 0, 0, 0, 0, this.width(), this.height(), this.width(), this.height());
        toastGui.getMinecraft().font.draw(poseStack, this.title, 18, 7, 0xFFFFFFFF);
        toastGui.getMinecraft().font.draw(poseStack, this.message, 18, 18, 0xFFFF55FF);

        return delta - this.firstDrawTime >= displayTime ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int width() {
        return 200; // Adjust width as necessary
    }

    @Override
    public int height() {
        return 150; // Adjust height as necessary
    }
}