package gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import controller.GameController;
import model.NPCModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import prompt.TimeBasedPrompts;

import java.util.*;
import java.util.stream.Collectors;

public class NPCTimeSortedScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");
    private static final ResourceLocation PENDING = new ResourceLocation("npcopenai", "textures/item/to_do.png");

    private TimeBasedPrompts.TimePeriod currentTime = TimeBasedPrompts.TimePeriod.MORNING;
    private static final List<TimeBasedPrompts.TimePeriod> TIME_ORDER = Arrays.asList(
            TimeBasedPrompts.TimePeriod.MORNING,
            TimeBasedPrompts.TimePeriod.NOON,
            TimeBasedPrompts.TimePeriod.AFTERNOON,
            TimeBasedPrompts.TimePeriod.EVENING
    );
    private boolean hasShownCompletionToast = false;
    private Map<String, List<NPCModel>> getSortedNPCsByTime() {
        List<NPCModel> npcs = GameController.getInstance().getNpcs();

        return npcs.stream()
                .sorted(Comparator.comparingInt(npc -> TIME_ORDER.indexOf(TimeBasedPrompts.TimePeriod.valueOf(npc.getTime().toUpperCase()))))
                .collect(Collectors.groupingBy(
                        npc -> TimeBasedPrompts.TimePeriod.valueOf(npc.getTime().toUpperCase()).name(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public NPCTimeSortedScreen() {
        super(new TextComponent("NPC Tasks Sorted by Time"));
    }

    @Override
    protected void init() {
        this.clearWidgets();  // 清除旧的控件
        super.init();
        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        hasShownCompletionToast = false;
        updateCurrentTimePeriod();
        updateGameTime();
        if (currentTime.ordinal() > 0) {
            this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("< Prev"), button -> {
                cycleTime(false);
            }));
        }

        if (currentTime.ordinal() < TIME_ORDER.size() - 1) {
            this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("Next >"), button -> {
                cycleTime(true);
            }));
        }
        // 添加关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));

    }

    private void updateCurrentTimePeriod() {
        List<NPCModel> npcs = GameController.getInstance().getNpcs();
        boolean anyTaskCompletedInCurrentPeriod = npcs.stream()
                .filter(npc -> npc.getTime().equalsIgnoreCase(currentTime.name()))
                .anyMatch(NPCModel::areAllTasksCompleted);

        if (anyTaskCompletedInCurrentPeriod) {
            int currentIndex = TIME_ORDER.indexOf(currentTime);
            if (currentIndex < TIME_ORDER.size() - 1) {
                currentTime = TIME_ORDER.get(currentIndex + 1);
                System.out.println("Switched to: " + currentTime);
                this.init(); // 重新初始化界面以更新 UI 元素
            } else {
                if (!hasShownCompletionToast) {
                    showCompletionToast("恭喜！", "你已经集齐了所有碎片！");
                    hasShownCompletionToast = true; // 确保通知只显示一次
                }
            }
        }
    }

    private void showCompletionToast(String title, String description) {
        Minecraft minecraft = Minecraft.getInstance();
        MutableComponent text = new TextComponent(title);
        MutableComponent desc = new TextComponent(description);
        SystemToast.add(minecraft.getToasts(), SystemToast.SystemToastIds.TUTORIAL_HINT, text, desc);
    }

    private void cycleTime(boolean forward) {
        int currentIndex = currentTime.ordinal();
        if (forward && currentIndex < TIME_ORDER.size() - 1) {
            currentTime = TIME_ORDER.get(currentIndex + 1);
        } else if (!forward && currentIndex > 0) {
            currentTime = TIME_ORDER.get(currentIndex - 1);
        }
        this.init();
        updateGameTime();

    }

    private void updateGameTime() {
        if (this.minecraft.player == null) return;

        switch (currentTime) {
            case MORNING:
                this.minecraft.player.chat("/time set 100");
                break;
            case NOON:
                this.minecraft.player.chat("/time set 6000");
                break;
            case AFTERNOON:
                this.minecraft.player.chat("/time set 9000");
                break;
            case EVENING:
                this.minecraft.player.chat("/time set 15000");
                break;
        }
    }
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        updateCurrentTimePeriod();

        String title = TimeBasedPrompts.getTitle(currentTime);
        String content = TimeBasedPrompts.getContent(currentTime);

        drawCenteredString(poseStack, this.font, title, this.width / 2, 20, 0xFFFFAA00);
        // 使用 TextUtils 处理并分行显示引导语
        List<ColoredText> contentLines = TextUtils.wrapText(content, (int)(this.width / 1.3f), true);

        int contentY = 40;
        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, contentY, line.color);
            contentY += 10; // 每行之间间隔10像素
        }

        List<NPCModel> npcs = getSortedNPCsByTime().getOrDefault(currentTime.name(), new ArrayList<>());
        if (npcs.isEmpty()) {
            drawCenteredString(poseStack, this.font, "No NPCs found for " + currentTime.name(), this.width / 2, 100, 0xFFFFFF);
        }

        int yOffset = contentY + 20;

        for (NPCModel npc : npcs) {
            int npcIndex = GameController.getInstance().getNpcs().indexOf(npc); // 获取原始索引
            drawString(poseStack, this.font, npc.getLocation(), this.width / 2 - 100, yOffset, 0xFFFFFF);
            ResourceLocation icon = npc.areAllTasksCompleted() ? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);
            // 添加一个按钮用于交互
            this.addRenderableWidget(new Button(this.width / 2 + 90, yOffset - 5, 60, 20, new TextComponent("Go"), button -> {
                teleportToNPC(npcIndex);
                this.minecraft.setScreen(new NPCDetailScreen(npc));
            }));

            yOffset += 25;
        }
    }

    private void teleportToNPC(int index) {
        if (this.minecraft.player != null) {
            this.minecraft.player.chat("/findnpc " + index);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}