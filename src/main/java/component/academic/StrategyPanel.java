package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class StrategyPanel extends ScrollPanel {
    private final Font font;
    private int scrollY = 0; // 当前滚动位置
    private List<String> strategyList; // 保存策略的列表
    private final Consumer<String> onStrategyClick; // 点击策略时的回调函数
    private int selectedStrategyIndex = -1; // 当前选中的策略索引

    public StrategyPanel(Minecraft mc, int width, int height, int top, int left, Map<String, List<String>> strategies, Consumer<String> onStrategyClick) {
        super(mc, width, height, top, left, 0, 0, 0, 0, 0x00000000, 0x00000000, 0x00000000);
        this.font = mc.font;
        this.onStrategyClick = onStrategyClick;
        this.strategyList = List.copyOf(strategies.keySet()); // 获取策略的标题
    }

    @Override
    protected int getContentHeight() {
        return strategyList.size() * 20; // 每个策略占用20像素高度
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int mouseX, int mouseY, Tesselator tesselator, int scrollY, int visibleHeight) {
        int yPos = 0;

        for (int i = 0; i < strategyList.size(); i++) {
            String strategy = strategyList.get(i);
            int color = (i == selectedStrategyIndex) ? 0xFFFFFF : 0xAAAAAA;

            // 使用 font.draw 替代 drawString
            font.draw(poseStack, strategy, left + 5, top + yPos + 5, color);
            yPos += 20;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            int yOffset = (int) mouseY - top + scrollY;
            int clickedIndex = yOffset / 20;

            if (clickedIndex >= 0 && clickedIndex < strategyList.size()) {
                selectedStrategyIndex = clickedIndex;
                String clickedStrategy = strategyList.get(clickedIndex);
                onStrategyClick.accept(clickedStrategy); // 触发回调
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // 实现辅助功能描述
    }


} 