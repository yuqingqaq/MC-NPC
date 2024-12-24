package gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import model.NPCModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class NPCDetailScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");

    private NPCModel npc;
    private ResourceLocation npcImage;
    private boolean showHint = false;
    public NPCDetailScreen(NPCModel npc) {
        super(new TextComponent(npc.getEvent()));
        this.npc = npc;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonSpacing = 10;
        int centerX = this.width / 2;
        int buttonY = this.height - 50;

        // "跟 xxx 聊聊" 按钮
        this.addRenderableWidget(new Button(centerX - buttonWidth - buttonSpacing, buttonY, buttonWidth, buttonHeight,
                new TextComponent("跟 " + npc.getNPCName() + " 聊聊"), button -> {
            // 打开 NPCInteractionScreen
            this.minecraft.setScreen(new NPCInteractionScreen(npc));
        }
        ));

        // "查看线索" 按钮
        this.addRenderableWidget(new Button(centerX + buttonSpacing, buttonY, buttonWidth, buttonHeight,
                new TextComponent("查看线索"), button -> {
            // 切换显示对话内容
            this.showHint = true;
        }
        ));

        // 关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        // Render NPC details
        int leftAlignX = this.width / 2 - 150;
        int yPos = 40;

        // Render NPC details
        drawString(poseStack, this.minecraft.font, "你在这里遇到了  " + npc.getNPCName(), leftAlignX, yPos+=20, 0xFFAAFF);
        drawString(poseStack, this.minecraft.font, "身份：" + npc.getRole(), leftAlignX+10, yPos+=20, 0xFFFFFF);
        yPos += 10 ;

        // 根据按钮选择渲染对话内容
        String dialogueText = showHint
                ? npc.getDialogues().get(1) // 显示第二条对话内容
                : npc.getDialogues().get(0); // 显示第一条对话内容

        yPos += 20;
        List<ColoredText> contentLines = TextUtils.wrapText(dialogueText, (int) (this.width / 1.3f), true);
        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text,leftAlignX + 10, yPos, line.color);
            yPos += 10;
        }

        // 引导语
        String dialogueIntroduction = "你想要：";
        drawString(poseStack, this.minecraft.font, dialogueIntroduction, leftAlignX, yPos += 40, 0xFFAAFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}