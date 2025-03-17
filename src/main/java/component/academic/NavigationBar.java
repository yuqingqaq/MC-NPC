package component.adaptive;

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
            int color = (i == selectedIndex) ? 0xFFFFFF00 : 0xFFFFFFFF; // 选中项为亮黄色，未选中项为白色
            minecraft.font.draw(poseStack, item, x + i * 80, y, color);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 10;
        int startY = 40;

        for (int i = 0; i < items.size(); i++) {
            int itemX = startX + i * 80;
            int itemWidth = 80;

            if (mouseX >= itemX && mouseX <= itemX + itemWidth && mouseY >= startY && mouseY <= startY + 10) {
                selectItem(i);
                return true;
            }
        }
        return false;
    }

    public void selectItem(int index) {
        if (index >= 0 && index < items.size()) {
            selectedIndex = index;
            onPress.onPress(null);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}