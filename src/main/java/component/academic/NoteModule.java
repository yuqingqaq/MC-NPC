package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class NoteModule {
    private final EditBox editBox;

    public NoteModule(Minecraft minecraft, int x, int y, int width, int height) {
        this.editBox = new EditBox(minecraft.font, x, y, width, height, new TextComponent("笔记"));
    }

    public void render(PoseStack poseStack) {
        editBox.render(poseStack, 0, 0, 0);
    }

    public void setText(String text) {
        editBox.setValue(text);
    }

    public String getText() {
        return editBox.getValue();
    }
} 