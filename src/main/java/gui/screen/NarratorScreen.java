package gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import controller.GameController;
import model.NPCModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
// import prompt.TaskPrompts;
import prompt.SRLTaskPrompt;
import system.TaskSystem;
import prompt.NarratorPrompts;

import java.util.List;

public class NarratorScreen extends Screen {
    private final String DisplayContent;
    public NarratorScreen(String TaskContent){
        super(new TextComponent("Narrator Screen"));
        this.DisplayContent = TaskContent;
    }
    
    @Override
    protected void init() {

        super.init();

        int centerY = this.height / 2;
        int centerX = this.width  / 2;
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> onClose()));


    }

    private int yOffset = 0; // 全局 y 偏移量

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        // Render NPC details
        int yOffset = 60;
        renderTaskContent(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public void renderTaskContent(PoseStack poseStack){
        String content = this.DisplayContent;
//        if ("AANarrator".equals(content)) {
//            content = NarratorPrompts.getContent("AANarrator");
//        }
        int yOffset = 80;
        drawString(poseStack, this.minecraft.font, content, this.width / 2 - 120, yOffset, 0xFFFFFF);

        // System.out.println(content);
        // List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.5f), true);
        // for (ColoredText line : contentLines) {
        //     drawString(poseStack, this.minecraft.font, line.text, this.width / 2 - 120, yOffset, 0xFFFFFF);
        //     // System.out.println(line.text);
        //     yOffset += 10;
        // }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
