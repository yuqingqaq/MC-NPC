package gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import model.NPCModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class NPCDetailScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");

    private NPCModel npc;
    private ResourceLocation npcImage;

    public NPCDetailScreen(NPCModel npc) {
        super(new TextComponent("NPC Details"));
        this.npc = npc;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        //ResourceLocation icon = COMPLETED;
        //RenderSystem.setShaderTexture(0, icon);
        //blit(poseStack, this.width / 2 - 64, 50, 0, 0, 128, 128, 128, 128);

        // Render NPC details
        int leftAlignX = this.width / 2 - 150;
        int yPos = 40;

        // Render NPC details
        drawString(poseStack, this.minecraft.font, "你在这里遇到了  " + npc.getNPCName(), leftAlignX, yPos+=20, 0xFFAAFF);
        drawString(poseStack, this.minecraft.font, "专业：" + npc.getRole(), leftAlignX+10, yPos+=20, 0xFFFFFF);
        drawString(poseStack, this.minecraft.font, "关系：" + npc.getRelationship(), leftAlignX+10, yPos+=20, 0xFFFFFF);
        yPos += 10 ;
        // 添加引导语
        String dialogueIntroduction = "最近他/她常常在讲：";
        drawString(poseStack, this.minecraft.font, dialogueIntroduction, leftAlignX, yPos+=20, 0xFFAAFF);
        yPos += 20;
        for (String dialogue : npc.getDialogues()) {
            String dialogueText = "\"" + dialogue + "\"";
            drawString(poseStack, this.minecraft.font, dialogueText, leftAlignX+10, yPos, 0xFFFFFF);
            yPos += 20;
        }

        // 添加关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}