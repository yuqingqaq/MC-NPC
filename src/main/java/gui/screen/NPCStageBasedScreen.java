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

    private int yOffset = 0; // 全局 y 偏移量

    public NPCStageBasedScreen() {
        super(new TextComponent("NPC Tasks Sorted by Stage"));
    }

    @Override
    protected void init() {
        this.clearWidgets(); // 清除旧的控件
        super.init();

        // 如果 INTRO 阶段已完成，跳过 INTRO 阶段
        if (TaskPrompts.isIntroCompleted() && currentStage == TaskPrompts.TaskStage.INTRO) {
            currentStage = determineLastCompletedStage();
        }
        System.out.println("CurrentStage: " + currentStage);

        // 按钮宽高和位置设置
        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        // 添加左导航按钮（如果当前阶段不是第一个）
        if (currentStage.ordinal() > 0) {
            this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight, new TextComponent("< Prev"), button -> {
                cycleTime(false);
            }));
        }

        // 添加右导航按钮（如果当前阶段不是最后一个）
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染背景
        renderBackground(poseStack);

        // 渲染页面内容
        yOffset = 20; // 初始化 y 偏移量
        renderTitle(poseStack);
        renderStageContent(poseStack);
        renderNPCList(poseStack);

        // 调用父类方法以渲染按钮等控件
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderTitle(PoseStack poseStack) {
        String title = TaskPrompts.getTitle(currentStage);
        drawCenteredString(poseStack, this.font, title, this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 20; // 标题占用高度
    }

    private void renderStageContent(PoseStack poseStack) {
        String content = TaskPrompts.getContent(currentStage);
        List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);

        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10; // 每行之间间隔10像素
        }

        yOffset += 10; // 内容与 NPC 列表之间的间距
    }

    private void renderNPCList(PoseStack poseStack) {
        // 如果是 INTRO 或 END 阶段，直接返回，不渲染 NPC 列表
        if (currentStage == TaskPrompts.TaskStage.INTRO || currentStage == TaskPrompts.TaskStage.END) {
            return;
        }

        // 获取当前阶段的 NPC 列表
        List<NPCModel> npcs = getSortedNPCsByStage().getOrDefault(currentStage.name(), new ArrayList<>());
        if (npcs.isEmpty()) {
            drawCenteredString(poseStack, this.font, "No NPCs found for " + currentStage.name(), this.width / 2, yOffset, 0xFFFFFF);
            yOffset += 20; // 空提示占用高度
            return;
        }

        boolean allNPCTasksCompleted = true; // 检查是否所有 NPC 任务完成

        for (NPCModel npc : npcs) {
            boolean npcTasksCompleted = npc.areAllTasksCompleted();
            int npcIndex = GameController.getInstance().getNpcs().indexOf(npc); // 获取 NPC 的索引

            // 渲染 NPC 的位置
            drawString(poseStack, this.font, npc.getLocation(), this.width / 2 - 100, yOffset, 0xFFFFFF);

            // 渲染任务状态图标（已完成或未完成）
            ResourceLocation icon = npcTasksCompleted ? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);

            // 添加按钮，用于传送到 NPC 并打开详情页面
            this.addRenderableWidget(new Button(this.width / 2 + 90, yOffset - 5, 60, 20, new TextComponent("Go"), button -> {
                teleportToNPC(npcIndex);
                this.minecraft.setScreen(new NPCDetailScreen(npc));
            }));

            yOffset += 25; // 每个 NPC 控件之间的间隔
            if (!npcTasksCompleted) {
                allNPCTasksCompleted = false; // 如果有未完成的任务，则标记为 false
            }
        }

        // 如果所有 NPC 的任务都完成，则渲染阶段完成内容
        if (allNPCTasksCompleted) {
            renderCompletionContent(poseStack);
        }
    }

    private void renderCompletionContent(PoseStack poseStack) {
        String outCome = TaskPrompts.getOutCome(currentStage);

        yOffset += 10;
        // 渲染阶段完成的描述内容
        List<ColoredText> outComeLines = TextUtils.wrapText(outCome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outComeLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10; // 每行之间间隔10像素
        }

        yOffset += 10; // 阶段完成内容与下方的间距
    }

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

    private void cycleTime(boolean forward) {
        int currentIndex = currentStage.ordinal();
        if (forward && currentIndex < STAGE_ORDER.size() - 1) {
            currentStage = STAGE_ORDER.get(currentIndex + 1);
        } else if (!forward && currentIndex > 0) {
            currentStage = STAGE_ORDER.get(currentIndex - 1);
        }
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
}