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
import prompt.TaskPrompts;

import java.util.*;
import java.util.stream.Collectors;

public class NPCStageBasedScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");
    private static final ResourceLocation PENDING = new ResourceLocation("npcopenai", "textures/item/to_do.png");

    private TaskPrompts.TaskStage currentStage = TaskPrompts.TaskStage.INTRO;
    private static final List<TaskPrompts.TaskStage> STAGE_ORDER = Arrays.asList(

            TaskPrompts.TaskStage.INTRO,
            TaskPrompts.TaskStage.ADMIN,
            TaskPrompts.TaskStage.TA,
            TaskPrompts.TaskStage.TB,
            TaskPrompts.TaskStage.TC,
            TaskPrompts.TaskStage.TD,
            TaskPrompts.TaskStage.DAOYUAN,
            TaskPrompts.TaskStage.CONFERENCE,
            TaskPrompts.TaskStage.LIBRARY,
            TaskPrompts.TaskStage.GYM,
            TaskPrompts.TaskStage.END
    );
    private boolean hasShownCompletionToast = false;

    private Map<String, List<NPCModel>> getSortedNPCsByStage() {
        List<NPCModel> npcs = GameController.getInstance().getNpcs();

        return npcs.stream()
                .filter(npc -> STAGE_ORDER.contains(TaskPrompts.TaskStage.valueOf(npc.getLocation().toUpperCase()))) // 确保阶段有效
                .sorted(Comparator.comparingInt(npc -> STAGE_ORDER.indexOf(TaskPrompts.TaskStage.valueOf(npc.getLocation().toUpperCase()))))
                .collect(Collectors.groupingBy(
                        npc -> TaskPrompts.TaskStage.valueOf(npc.getLocation().toUpperCase()).name(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public NPCStageBasedScreen() {
        super(new TextComponent("NPC Tasks Sorted by Stage"));
    }

    @Override
    protected void init() {
        this.clearWidgets();  // 清除旧的控件
        super.init();

        // 如果 INTRO 阶段已完成，跳过 INTRO 阶段
        if (TaskPrompts.isIntroCompleted() && currentStage == TaskPrompts.TaskStage.INTRO) {
            currentStage = determineLastCompletedStage();
        }
        System.out.println("CurrentStage:" +  currentStage);

        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        hasShownCompletionToast = false;
        updateCurrentTaskStage();
        if (currentStage.ordinal() > 0) {
            this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("< Prev"), button -> {
                cycleTime(false);
            }));
        }

        if (currentStage.ordinal() < STAGE_ORDER.size() - 1) {
            this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("Next >"), button -> {
                cycleTime(true);
            }));
        }
        // 添加关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> {
            onClose();
        }));

    }

    @Override
    public void onClose() {
        super.onClose();
        TaskPrompts.setIntroCompleted(true);
    }

    /**
     * 确定当前最后一个已完成的阶段。
     * @return 最后一个已完成的阶段。
     */
    private TaskPrompts.TaskStage determineLastCompletedStage() {
        List<NPCModel> npcs = GameController.getInstance().getNpcs();
        TaskPrompts.TaskStage lastCompletedStage = TaskPrompts.TaskStage.INTRO;

        for (TaskPrompts.TaskStage stage : STAGE_ORDER) {
            boolean allTasksCompleted = npcs.stream()
                    .filter(npc -> npc.getLocation().equalsIgnoreCase(stage.name()))
                    .allMatch(NPCModel::areAllTasksCompleted);

            if (allTasksCompleted) {
                lastCompletedStage = stage;
            } else {
                break; // 一旦发现未完成的阶段，停止循环
            }
        }

        return lastCompletedStage;
    }

    /**
     * 更新当前任务阶段。如果当前阶段任务完成，则推进到下一个阶段。
     */
    private void updateCurrentTaskStage() {
        List<NPCModel> npcs = GameController.getInstance().getNpcs();
        boolean anyTaskCompletedInCurrentPeriod = npcs.stream()
                .filter(npc -> npc.getLocation().equalsIgnoreCase(currentStage.name()))
                .anyMatch(NPCModel::areAllTasksCompleted);

        if (anyTaskCompletedInCurrentPeriod) {
            int currentIndex = STAGE_ORDER.indexOf(currentStage);
            if (currentIndex < STAGE_ORDER.size() - 1) {
                System.out.println(currentStage + " 阶段任务已完成！");
                currentStage = STAGE_ORDER.get(currentIndex + 1);
                System.out.println("Switched to: " + currentStage);
                this.init(); // 重新初始化界面以更新 UI 元素
            } else {
                if (!hasShownCompletionToast) {
                    showCompletionToast("恭喜！", "你已经集齐了所有碎片！");
                    hasShownCompletionToast = true;
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
        int currentIndex = currentStage.ordinal();
        if (forward && currentIndex < STAGE_ORDER.size() - 1) {
            currentStage = STAGE_ORDER.get(currentIndex + 1);
        } else if (!forward && currentIndex > 0) {
            currentStage = STAGE_ORDER.get(currentIndex - 1);
        }
        //this.init();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        //updateCurrentTaskStage();

        String title = TaskPrompts.getTitle(currentStage);
        String content = TaskPrompts.getContent(currentStage);

        drawCenteredString(poseStack, this.font, title, this.width / 2, 20, 0xFFFFAA00);

        List<ColoredText> contentLines = TextUtils.wrapText(content, (int)(this.width / 1.3f), true);

        int contentY = 40;
        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, contentY, line.color);
            contentY += 10; // 每行之间间隔10像素
        }

        if (currentStage == TaskPrompts.TaskStage.INTRO || currentStage == TaskPrompts.TaskStage.END) {
            return; // 直接返回，不显示 NPC 列表
        }

        List<NPCModel> npcs = getSortedNPCsByStage().getOrDefault(currentStage.name(), new ArrayList<>());
        if (npcs.isEmpty()) {
            drawCenteredString(poseStack, this.font, "No NPCs found for " + currentStage.name(), this.width / 2, 100, 0xFFFFFF);
        }

        int yOffset = contentY + 20;

        boolean allNPCTasksCompleted = true;

        for (NPCModel npc : npcs) {
            boolean npcTasksCompleted = npc.areAllTasksCompleted();
            int npcIndex = GameController.getInstance().getNpcs().indexOf(npc); // 获取原始索引

            drawString(poseStack, this.font, npc.getLocation(), this.width / 2 - 100, yOffset, 0xFFFFFF);

            ResourceLocation icon = npcTasksCompleted? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);
            // 添加一个按钮用于交互
            this.addRenderableWidget(new Button(this.width / 2 + 90, yOffset - 5, 60, 20, new TextComponent("Go"), button -> {
                teleportToNPC(npcIndex);
                this.minecraft.setScreen(new NPCDetailScreen(npc));
            }));
            yOffset += 25;

            if(!npcTasksCompleted) {
                allNPCTasksCompleted = false;
            }
        }

        if (allNPCTasksCompleted) {
            String outCome = TaskPrompts.getOutCome(currentStage); // 获取当前阶段内容

            // 渲染内容
            List<ColoredText> outComeLines = TextUtils.wrapText(outCome, (int) (this.width / 1.3f), true);
            int contentYOffset = yOffset + 40; // 内容起始位置
            for (ColoredText line : outComeLines) {
                drawString(poseStack, this.font, line.text, this.width / 2 - 120, contentYOffset, line.color);
                contentYOffset += 10; // 每行之间间隔10像素
            }
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