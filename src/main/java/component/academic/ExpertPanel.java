package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ExpertPanel extends AbstractWidget implements Widget {
    private final List<Button> actionButtons; // 按钮列表
    private final int panelWidth;
    private final int panelHeight;

    public ExpertPanel(int x, int y, int width, int height) {
        super(x, y, width, height, new TextComponent("Expert Panel"));
        this.actionButtons = new ArrayList<>();
        this.panelWidth = width;
        this.panelHeight = height;
        init();
    }

    // 初始化按钮
    private void init() {
        int buttonHeight = 20; // 按钮高度
        int buttonSpacing = 10; // 按钮间距
        int startY = this.y + 20; // 第一个按钮的起始 Y 坐标

        actionButtons.clear(); // 清空按钮列表

        // 创建“邮件写作”按钮
        Button emailWritingButton = new Button(
                this.x + 10, // 按钮 x 坐标
                startY,      // 按钮 y 坐标
                this.panelWidth - 20, // 按钮宽度
                buttonHeight,         // 按钮高度
                new TextComponent("邮件写作"), // 按钮文本
                btn -> teleportToNPC(12) // 点击事件
        );

        // 创建“论文写作”按钮
        Button essayWritingButton = new Button(
                this.x + 10, // 第二个按钮的 x 坐标
                startY + buttonHeight + buttonSpacing, // 第二个按钮的 y 坐标
                this.panelWidth - 20, // 按钮宽度
                buttonHeight,         // 按钮高度
                new TextComponent("论文写作"), // 按钮文本
                btn -> teleportToNPC(13) // 点击事件
        );

        // 将按钮添加到列表
        actionButtons.add(emailWritingButton);
        actionButtons.add(essayWritingButton);
    }

    // 执行跳转到 NPC 的逻辑
    private void teleportToNPC(int index) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.chat("/findnpc " + index);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染背景面板
        fill(poseStack, this.x, this.y, this.x + this.panelWidth, this.y + this.panelHeight, 0x33000000); // 半透明背景

        // 渲染标题
        Minecraft.getInstance().font.draw(
                poseStack,
                "选择一个选项",
                this.x + 10,
                this.y + 5,
                0xFFFFFF // 白色标题文字
        );

        // 渲染所有按钮
        for (Button button : actionButtons) {
            button.render(poseStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否有按钮被点击并处理点击事件
        for (Button actionButton : actionButtons) {
            if (actionButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        // 暂时不需要实现旁白支持
    }

}