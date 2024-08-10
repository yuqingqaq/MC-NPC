import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.matrix.MatrixStack;

public class TaskEntry extends ExtendedList.AbstractListEntry<TaskEntry> {
    private final String description;

    public TaskEntry(String description) {
        this.description = description;
    }

    @Override
    public void render(MatrixStack matrixStack, int index, int y, int x, int listWidth, int listHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        // Assuming Minecraft.getInstance().font is the correct way to get the FontRenderer
        Minecraft.getInstance().font.draw(matrixStack, description, x+2, y+1, 0xFFFFFF); // White color for text
    }

    public String getDescription() {
        return description;
    }
}