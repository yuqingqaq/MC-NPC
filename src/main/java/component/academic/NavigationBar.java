package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

import java.util.List;

public class NavigationBar {
    private final Minecraft minecraft;
    private final List<String> items;
    private int selectedIndex = 0;
    private final Button.OnPress onPress;

    public NavigationBar(Minecraft minecraft, List<String> items, Button.OnPress onPress) {
        this.minecraft = minecraft;
        this.items = items;
        this.onPress = onPress;
    }

    public void render(PoseStack poseStack, int x, int y) {
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            int color = (i == selectedIndex) ? 0xFF0000FF : 0xFFFFFFFF; // 选中项为蓝色
            minecraft.font.draw(poseStack, item, x + i * 60, y, color);
        }
    }

    public void selectItem(int index) {
        if (index >= 0 && index < items.size()) {
            selectedIndex = index;
            onPress.onPress(null);
        }
    }
} 