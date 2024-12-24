package gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import controller.GameController;
import model.NPCModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import prompt.TaskPrompts;
import system.TaskSystem;

import java.util.List;

public class NPCStageBasedScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");
    private static final ResourceLocation PENDING = new ResourceLocation("npcopenai", "textures/item/to_do.png");

    private final TaskSystem taskSystem; // 使用 TaskSystem 管理任务和 NPC
    private TaskPrompts.TaskStage currentStage = TaskPrompts.TaskStage.INTRO;
    private static final List<TaskPrompts.TaskStage> STAGE_ORDER = List.of(
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

    private int yOffset = 0; // 全局 y 偏移量

    public NPCStageBasedScreen() {
        super(new TextComponent("NPC Tasks Sorted by Stage"));
        this.taskSystem = GameController.getInstance().getTaskSystem();
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();

        // 跳过已完成的阶段
        if (TaskPrompts.isIntroCompleted() && currentStage == TaskPrompts.TaskStage.INTRO) {
            currentStage = determineLastCompletedStage();
        }

        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        // 左导航按钮
        if (currentStage.ordinal() > 0) {
            this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("< Prev"), button -> {
                cycleStage(false);
            }));
        }

        // 右导航按钮
        if (currentStage.ordinal() < STAGE_ORDER.size() - 1) {
            this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("Next >"), button -> {
                cycleStage(true);
            }));
        }

        // 关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> onClose()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);

        yOffset = 20; // 初始化 y 偏移量
        renderTitle(poseStack);
        renderStageContent(poseStack);
        renderNPCList(poseStack);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderTitle(PoseStack poseStack) {
        String title = TaskPrompts.getTitle(currentStage);
        drawCenteredString(poseStack, this.font, title, this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 20;
    }

    private void renderStageContent(PoseStack poseStack) {
        String content = TaskPrompts.getContent(currentStage);
        List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);

        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }

        yOffset += 10;
    }

    private void renderNPCList(PoseStack poseStack) {
        if (currentStage == TaskPrompts.TaskStage.INTRO || currentStage == TaskPrompts.TaskStage.END) {
            return;
        }

        List<NPCModel> npcs = taskSystem.getNPCsByStage(currentStage.name());
        if (npcs.isEmpty()) {
            drawCenteredString(poseStack, this.font, "No NPCs for this stage", this.width / 2, yOffset, 0xFFFFFF);
            yOffset += 20;
            return;
        }

        boolean allTasksCompleted = true;
        for (NPCModel npc : npcs) {
            boolean npcTasksCompleted = npc.areAllTasksCompleted();
            int npcIndex = GameController.getInstance().getNpcs().indexOf(npc);

            drawString(poseStack, this.font, npc.getLocation(), this.width / 2 - 100, yOffset, 0xFFFFFF);

            ResourceLocation icon = npcTasksCompleted ? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);

            this.addRenderableWidget(new Button(this.width / 2 + 90, yOffset - 5, 60, 20, new TextComponent("Go"), button -> {
                teleportToNPC(npcIndex);
            }));

            yOffset += 25;
            if (!npcTasksCompleted) {
                allTasksCompleted = false;
            }
        }

        if (allTasksCompleted) {
            renderCompletionContent(poseStack);
        }
    }

    private void renderCompletionContent(PoseStack poseStack) {

        yOffset += 10;

        String outCome = TaskPrompts.getOutCome(currentStage);
        List<ColoredText> outComeLines = TextUtils.wrapText(outCome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outComeLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }
        yOffset += 10;
    }

    private void cycleStage(boolean forward) {
        int currentIndex = currentStage.ordinal();
        if (forward && currentIndex < STAGE_ORDER.size() - 1) {
            currentStage = STAGE_ORDER.get(currentIndex + 1);
        } else if (!forward && currentIndex > 0) {
            currentStage = STAGE_ORDER.get(currentIndex - 1);
        }
        this.init(); // 刷新界面
    }

    private void teleportToNPC(int index) {
        if (this.minecraft.player != null) {
            this.minecraft.player.chat("/findnpc " + index);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        TaskPrompts.setIntroCompleted(true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private TaskPrompts.TaskStage determineLastCompletedStage() {
        for (TaskPrompts.TaskStage stage : STAGE_ORDER) {
            if (!taskSystem.areAllTasksCompletedInStage(stage.name())) {
                return stage;
            }
        }
        return TaskPrompts.TaskStage.END;
    }
}