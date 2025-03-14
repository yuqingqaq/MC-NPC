package component.adaptive;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class NoteModule {
    private final EditBox editBox;
    private final Minecraft minecraft;

    public NoteModule(Minecraft minecraft, int x, int y, int width, int height) {
        this.minecraft = minecraft;
        this.editBox = new EditBox(minecraft.font, x, y, width, height, new TextComponent("笔记"));
        this.editBox.setMaxLength(Integer.MAX_VALUE); // 设置无限输入长度
        this.editBox.setBordered(true); // 添加边框
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染标题
        minecraft.font.draw(poseStack, "笔记", editBox.x, editBox.y - 12, 0xFFFFFF);
        // 渲染笔记内容
        editBox.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public void setText(String text) {
        editBox.setValue(text);
    }

    public String getText() {
        return editBox.getValue();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return editBox.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return editBox.charTyped(codePoint, modifiers);
    }
}