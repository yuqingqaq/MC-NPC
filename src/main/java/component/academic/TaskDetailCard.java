package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

public class TaskDetailCard {
    // 为每种卡片类型定义独立的图标资源
    private static final ResourceLocation ICON_TARGET = new ResourceLocation("npcopenai", "textures/gui/task.png");
    private static final ResourceLocation ICON_LOCATION = new ResourceLocation("npcopenai", "textures/gui/location.png");
    private static final ResourceLocation ICON_TIME = new ResourceLocation("npcopenai", "textures/gui/time.png");
    private static final ResourceLocation ICON_RESOURCE = new ResourceLocation("npcopenai", "textures/gui/resource.png");
    private static final ResourceLocation ICON_STRATEGY = new ResourceLocation("npcopenai", "textures/gui/strategy.png");
    private static final ResourceLocation ICON_SUMMARY = new ResourceLocation("npcopenai", "textures/gui/summary.png");
    
    // 提供公共访问方法
    public static ResourceLocation getTargetIcon() { return ICON_TARGET; }
    public static ResourceLocation getLocationIcon() { return ICON_LOCATION; }
    public static ResourceLocation getTimeIcon() { return ICON_TIME; }
    public static ResourceLocation getResourceIcon() { return ICON_RESOURCE; }
    public static ResourceLocation getStrategyIcon() { return ICON_STRATEGY; }
    public static ResourceLocation getSummaryIcon() { return ICON_SUMMARY; }
    
    private static final int ICON_SIZE = 16;
    private final Minecraft minecraft;
    
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 20;
    
    public TaskDetailCard(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
    
    public void renderNormalCard(PoseStack poseStack, int x, int y, int width, String title, 
            String content, ResourceLocation icon, boolean showButton, Button.OnPress onPress) {
        // 渲染 10% 透明度的黑色背景
        Screen.fill(poseStack, x, y, x + width, y + 50, 0x1A000000);
        
        RenderSystem.setShaderTexture(0, icon);
        RenderSystem.enableBlend();
        Screen.blit(poseStack, x + 10, y + 10, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        
        minecraft.font.draw(poseStack, title, x + 32, y + 12, 0xFFFFFF);
        minecraft.font.draw(poseStack, content, x + 10, y + 32, 0xAAAAAA);
        
        // 根据 showButton 参数决定是否渲染按钮
        if (showButton) {
            Button viewButton = new Button(x + width - BUTTON_WIDTH - 10, y + 10, BUTTON_WIDTH, BUTTON_HEIGHT, 
                                           new TextComponent("查看"), onPress);
            viewButton.render(poseStack, 0, 0, 0);
        }
    }
    
    public void renderWideCard(PoseStack poseStack, int x, int y, int width, String title, 
            List<String> contents, ResourceLocation icon, boolean showButton, Button.OnPress onPress) {
        int height = 35 + contents.size() * 15;
        // 渲染 10% 透明度的黑色背景
        Screen.fill(poseStack, x, y, x + width, y + height, 0x1A000000);
        
        RenderSystem.setShaderTexture(0, icon);
        RenderSystem.enableBlend();
        Screen.blit(poseStack, x + 10, y + 10, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        
        minecraft.font.draw(poseStack, title, x + 32, y + 12, 0xFFFFFF);
        int contentY = y + 32;
        for (String content : contents) {
            minecraft.font.draw(poseStack, "• " + content, x + 10, contentY, 0xAAAAAA);
            contentY += 15;
        }
        
        // 根据 showButton 参数决定是否渲染按钮
        if (showButton) {
            Button viewButton = new Button(x + width - BUTTON_WIDTH - 10, y + 10, BUTTON_WIDTH, BUTTON_HEIGHT, 
                                           new TextComponent("查看"), onPress);
            viewButton.render(poseStack, 0, 0, 0);
        }
    }

    public boolean isMouseOverButton(int mouseX, int mouseY, int x, int y, int width) {
        int buttonX = x + width - BUTTON_WIDTH - 10;
        int buttonY = y + 10;
        return mouseX >= buttonX && mouseX <= buttonX + BUTTON_WIDTH && mouseY >= buttonY && mouseY <= buttonY + BUTTON_HEIGHT;
    }
} 