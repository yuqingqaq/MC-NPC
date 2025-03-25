package component.academic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.ArrayList;
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

    // 记录可点击的NPC区域
    private List<NpcLink> npcLinks = new ArrayList<>();

    // NPC数据定义
    public static class NpcLink {
        public int x, y, width, height;
        public int npcId;
        public String npcName;

        public NpcLink(int x, int y, int width, int height, int npcId, String npcName) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.npcId = npcId;
            this.npcName = npcName;
        }

        public boolean contains(int mouseX, int mouseY) {
            // 扩大点击区域，更容易点击
            int padding = 8; // 增加点击区域的大小
            return mouseX >= (x - padding) && mouseX <= (x + width + padding) &&
                    mouseY >= (y - padding) && mouseY <= (y + height + padding);
        }
    }

    // 预定义的NPC数据
    private static final String[][] NPC_DATA = {
            {"邮件写作大师", "12"},
            {"论文写作专家", "13"},
            {"领域专家", "14"}
    };

    public TaskDetailCard(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    // 清除NPC链接
    public void clearNpcLinks() {
        npcLinks.clear();
    }

    // 获取NPC链接列表
    public List<NpcLink> getNpcLinks() {
        return npcLinks;
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

    // 自适应高度的资源卡片渲染
    public int renderResourceCard(PoseStack poseStack, int x, int y, int width, String title,
                                  List<String> resources, ResourceLocation icon, boolean showButton, Button.OnPress onPress) {
        // 清除旧的NPC链接
        clearNpcLinks();

        // 计算卡片总高度：标题(32) + 资源列表(每行15像素) + 底部填充(15)
        int contentHeight = resources.size() * 15;
        int cardHeight = 32 + contentHeight + 15;

        // 渲染背景
        Screen.fill(poseStack, x, y, x + width, y + cardHeight, 0x1A000000);

        // 渲染图标
        RenderSystem.setShaderTexture(0, icon);
        RenderSystem.enableBlend();
        Screen.blit(poseStack, x + 10, y + 10, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        // 渲染标题
        minecraft.font.draw(poseStack, title, x + 32, y + 12, 0xFFFFFF);

        // 渲染资源列表
        int contentY = y + 32;
        for (String resource : resources) {
            renderResourceLine(poseStack, resource, x + 10, contentY, width - 20, 0xAAAAAA);
            contentY += 15;
        }

        // 渲染按钮
        if (showButton) {
            Button viewButton = new Button(x + width - BUTTON_WIDTH - 10, y + 10, BUTTON_WIDTH, BUTTON_HEIGHT,
                    new TextComponent("查看"), onPress);
            viewButton.render(poseStack, 0, 0, 0);
        }

        // 返回卡片总高度
        return cardHeight;
    }

    // 渲染一行资源文本，检测并高亮NPC名称
    private void renderResourceLine(PoseStack poseStack, String resource, int x, int y, int maxWidth, int defaultColor) {
        // 先渲染基本的项目符号
        String bulletPoint = "• ";
        minecraft.font.draw(poseStack, bulletPoint, x, y, defaultColor);
        x += minecraft.font.width(bulletPoint);

        // 渲染资源文本，检测NPC名称
        String content = resource;
        int currentX = x;
        boolean foundNpc = false;

        // 检查文本中是否包含预定义的NPC名称
        for (String[] npcInfo : NPC_DATA) {
            String npcName = npcInfo[0];
            int npcId = Integer.parseInt(npcInfo[1]);

            int index = content.indexOf(npcName);
            if (index != -1) {
                foundNpc = true;

                // 渲染NPC名称前的文本
                String beforeNpc = content.substring(0, index);
                minecraft.font.draw(poseStack, beforeNpc, currentX, y, defaultColor);
                currentX += minecraft.font.width(beforeNpc);

                // 渲染NPC名称为高亮蓝色
                int linkWidth = minecraft.font.width(npcName);
                minecraft.font.draw(poseStack, npcName, currentX, y, 0x55AAFF); // 蓝色高亮

                // 添加下划线
                Screen.fill(poseStack, currentX, y + 9, currentX + linkWidth, y + 10, 0x55AAFF);

                // 记录这个NPC链接的位置
                npcLinks.add(new NpcLink(currentX, y, linkWidth, 10, npcId, npcName));

                // 更新当前X位置和剩余内容
                currentX += linkWidth;
                content = content.substring(index + npcName.length());
            }
        }

        // 渲染剩余文本
        if (!content.isEmpty()) {
            minecraft.font.draw(poseStack, content, currentX, y, defaultColor);
        }

        // 如果没有找到NPC，渲染整个文本
        if (!foundNpc) {
            minecraft.font.draw(poseStack, resource, x, y, defaultColor);
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

    // 检查鼠标是否悬停在NPC链接上
    public int checkNpcLinkHover(int mouseX, int mouseY) {
        for (NpcLink link : npcLinks) {
            if (link.contains(mouseX, mouseY)) {
                System.out.println("Found match! NPC: " + link.npcId + " - " + link.npcName);
                return link.npcId;
            }
        }
        return -1; // 返回-1表示鼠标不在任何NPC链接上
    }


}