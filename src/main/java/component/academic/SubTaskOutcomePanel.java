package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import model.AdaptiveSubTaskModel;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.List;

public class SubTaskOutcomePanel extends AbstractWidget {
    private final Minecraft mc;
    private final List<AdaptiveSubTaskModel> subTasks;

    public SubTaskOutcomePanel(Minecraft mc, int width, int height, int top, int left, int border, int scrollBarWidth, List<AdaptiveSubTaskModel> subTasks) {
        super(left, top, width, height, null);
        this.mc = mc;
        this.subTasks = subTasks;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int y = this.y + 10;
        for (AdaptiveSubTaskModel subTask : subTasks) {
            drawString(poseStack, this.mc.font, "Title: " + subTask.getTitle(), this.x + 10, y, 0xFFFFFF);
            drawString(poseStack, this.mc.font, "Outcome: " + subTask.getOutcome(), this.x + 10, y + 10, 0xAAAAAA);
            y += 30;
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}