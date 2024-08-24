//import com.mojang.blaze3d.matrix.MatrixStack;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.widget.button.Button;
//import net.minecraft.util.text.StringTextComponent;
//
//public class ExampleScreen extends Screen {
//
//    public ExampleScreen() {
//        super(new StringTextComponent("NPC-OpenAI GUI"));
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//        int buttonWidth = 200;
//        int buttonHeight = 20;
//        int buttonX = (this.width - buttonWidth) / 2;
//        int buttonY = (this.height - buttonHeight) / 2;
//
//        this.addButton(new Button(buttonX, buttonY, buttonWidth, buttonHeight, new StringTextComponent("Click me"), button -> {
//            assert this.minecraft != null;
//            assert this.minecraft.player != null;
//            this.minecraft.player.sendMessage(new StringTextComponent("Button clicked!"), this.minecraft.player.getUUID());
//        }));
//    }
//
//    @Override
//    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(matrixStack);
//        super.render(matrixStack, mouseX, mouseY, partialTicks);
//        drawCenteredString(matrixStack, this.font, "This is an NPC-OpenAI GUI!", this.width / 2, 10, 0xFFFFFF);
//    }
//
//    @Override
//    public boolean isPauseScreen() {
//        return false;
//    }
//}