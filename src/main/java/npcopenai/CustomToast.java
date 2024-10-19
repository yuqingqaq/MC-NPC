package npcopenai;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.TextComponent;

public class CustomToast {
    public static void show(Minecraft mc, String title, String message) {
        TextComponent titleComponent = new TextComponent(title);
        TextComponent messageComponent = new TextComponent(message);
        SystemToast.add(mc.getToasts(), SystemToast.SystemToastIds.TUTORIAL_HINT, titleComponent, messageComponent);
    }
}