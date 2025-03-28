package gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import component.ColoredText;
import component.TextUtils;
import controller.GameController;
import item.goldcoin.GoldCoinTracker;
import model.NPCModel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import prompt.TaskPrompts;
import system.TaskSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 记录玩家已经访问过的NPC
    private static final Map<TaskPrompts.TaskStage, Boolean> VISITED_NPCS = new HashMap<>();

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

        // 添加寻找金币按钮，但仅在非INTRO和非END阶段，并且玩家已经访问过该阶段的NPC时显示
        if (currentStage != TaskPrompts.TaskStage.INTRO && currentStage != TaskPrompts.TaskStage.END
                && Boolean.TRUE.equals(VISITED_NPCS.get(currentStage))) {
            this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 30, 100, buttonHeight, new TextComponent("Find Gold Coin"), button -> {
                teleportToStageGoldCoin(currentStage);
            }));
        }

        // 关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20, new TextComponent("X"), button -> onClose()));
    }

    private void teleportToStageGoldCoin(TaskPrompts.TaskStage stage) {
        if (this.minecraft.player != null) {
            // 从TaskSystem获取该阶段的金币位置
            String locationStr = GameController.getInstance().getTaskSystem().getStageCoinLocation(stage);
            if (locationStr != null) {
                String[] coords = locationStr.split(",");
                if (coords.length == 3) {
                    try {
                        int x = Integer.parseInt(coords[0].trim());
                        int y = Integer.parseInt(coords[1].trim());
                        int z = Integer.parseInt(coords[2].trim());

                        // 使用命令传送玩家到该阶段的金币位置
                        this.minecraft.player.chat("/tp @s " + x + " " + y + " " + z);
                        onClose(); // 关闭界面

                        // 显示提示信息
                        this.minecraft.player.displayClientMessage(
                                new TextComponent("Teleported to gold coin location for " + stage.name() + " stage."),
                                false
                        );
                    } catch (NumberFormatException e) {
                        this.minecraft.player.displayClientMessage(
                                new TextComponent("Error parsing coordinates for " + stage.name() + " stage."),
                                false
                        );
                    }
                }
            } else {
                this.minecraft.player.displayClientMessage(
                        new TextComponent("No gold coin location defined for " + stage.name() + " stage."),
                        false
                );
            }
        }
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

        // 获取当前阶段对应的NPC列表
        List<NPCModel> npcs = taskSystem.getNPCsByStage(currentStage.name());

        // 如果当前阶段没有找到NPC(可能是因为阶段名与位置名不一致)，尝试通过映射查找
        if (npcs.isEmpty()) {
            // 尝试查找与该阶段相关的位置名称
            for (Map.Entry<String, List<NPCModel>> entry : taskSystem.getNpcByStage().entrySet()) {
                if (taskSystem.getStageFromLocation(entry.getKey()) == currentStage) {
                    npcs = entry.getValue();
                    break;
                }
            }
        }

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
                // 标记该阶段的NPC已访问
                VISITED_NPCS.put(currentStage, true);
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

    // 重置所有NPC访问状态（可用于测试）
    public static void resetAllVisitedStatus() {
        VISITED_NPCS.clear();
    }

    // 标记指定阶段的NPC已访问（可供外部调用）
    public static void markStageVisited(TaskPrompts.TaskStage stage) {
        VISITED_NPCS.put(stage, true);
    }
}