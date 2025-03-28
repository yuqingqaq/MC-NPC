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
import system.StageManager.StageInfo;
import system.StageManager;
import system.UIScreenManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

public class NPCStageBasedScreen extends Screen {
    private static final ResourceLocation COMPLETED = new ResourceLocation("npcopenai", "textures/item/todo.png");
    private static final ResourceLocation PENDING = new ResourceLocation("npcopenai", "textures/item/to_do.png");

    private final StageManager stageManager; // 使用 StageManager 管理阶段
    private StageInfo currentDisplayStage;

    // 记录玩家已经访问过的NPC
    private static final Map<String, Boolean> VISITED_NPCS = new HashMap<>();

    private int yOffset = 0; // 全局 y 偏移量

    public NPCStageBasedScreen() {
        super(new TextComponent("探索任务"));
        this.stageManager = GameController.getInstance().getStageManager();
        this.currentDisplayStage = stageManager.getCurrentStage();
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.NO_HUD);

        // 跳过已完成的阶段
        if (stageManager.isStageCompleted("INTRO") &&
                currentDisplayStage != null &&
                "INTRO".equals(currentDisplayStage.getId())) {
            currentDisplayStage = determineLastCompletedStage();
        }

        int buttonWidth = 100;
        int buttonHeight = 20;
        int leftButtonX = 20;
        int rightButtonX = this.width - buttonWidth - 20;

        // 获取所有阶段
        List<StageInfo> stages = stageManager.getAllStages();
        int currentIndex = stages.indexOf(currentDisplayStage);

        // 左导航按钮
        if (currentIndex > 0) {
            this.addRenderableWidget(new Button(leftButtonX, this.height - 30, buttonWidth, buttonHeight,
                    new TextComponent("< 上一页"), button -> {
                currentDisplayStage = stages.get(currentIndex - 1);
                this.init();
            }));
        }

        // 右导航按钮
        if (currentIndex < stages.size() - 1) {
            this.addRenderableWidget(new Button(rightButtonX, this.height - 30, buttonWidth, buttonHeight,
                    new TextComponent("下一页 >"), button -> {
                currentDisplayStage = stages.get(currentIndex + 1);
                this.init();
            }));
        }

        // 添加寻找金币按钮，但仅在非INTRO和非END阶段，并且玩家已经访问过该阶段的NPC时显示
        if (currentDisplayStage != null &&
                !"INTRO".equals(currentDisplayStage.getId()) &&
                !"END".equals(currentDisplayStage.getId()) &&
                Boolean.TRUE.equals(VISITED_NPCS.get(currentDisplayStage.getId())) &&
                currentDisplayStage.getCoinLocation() != null &&
                !currentDisplayStage.getCoinLocation().isEmpty()) {

            this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 30, 100, buttonHeight,
                    new TextComponent("找碎片"), button -> {
                teleportToCoin(currentDisplayStage.getCoinLocation());
            }));
        }

        // 关闭按钮
        this.addRenderableWidget(new Button(this.width - 30, 15, 20, 20,
                new TextComponent("X"), button -> onClose()));
    }

    private void teleportToCoin(String locationStr) {
        if (this.minecraft.player != null) {
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
                            new TextComponent("已传送到金币位置"),
                            false
                    );
                } catch (NumberFormatException e) {
                    this.minecraft.player.displayClientMessage(
                            new TextComponent("坐标格式错误: " + locationStr),
                            false
                    );
                }
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);

        yOffset = 20; // 初始化 y 偏移量

        if (currentDisplayStage == null) {
            drawCenteredString(poseStack, this.font, "当前没有阶段信息",
                    this.width / 2, yOffset, 0xFFFF0000);
            super.render(poseStack, mouseX, mouseY, partialTicks);
            return;
        }

        // 渲染标题
        drawCenteredString(poseStack, this.font, currentDisplayStage.getTitle(),
                this.width / 2, yOffset, 0xFFFFAA00);
        yOffset += 20;

        // 渲染内容
        String content = currentDisplayStage.getContent();
        List<ColoredText> contentLines = TextUtils.wrapText(content, (int) (this.width / 1.3f), true);

        for (ColoredText line : contentLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }

        yOffset += 10;

        // 如果当前阶段不是INTRO或END，渲染NPC信息
        if (!"INTRO".equals(currentDisplayStage.getId()) && !"END".equals(currentDisplayStage.getId())) {
            renderNPCInfo(poseStack);
        }

        // 如果当前阶段已完成，显示完成内容
        if (stageManager.isStageCompleted(currentDisplayStage.getId()) &&
                currentDisplayStage.getOutcome() != null &&
                !currentDisplayStage.getOutcome().isEmpty()) {

            renderCompletionContent(poseStack);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderNPCInfo(PoseStack poseStack) {
        // 获取当前阶段关联的NPC位置列表
        String npcLocationsStr = currentDisplayStage.getNpcLocation();
        if (npcLocationsStr == null || npcLocationsStr.isEmpty()) {
            return;
        }

        // 分割多个NPC位置（如果有的话）
        List<String> npcLocations = Arrays.asList(npcLocationsStr.split(";"));
        boolean allTasksCompleted = true;

        // 获取所有NPC
        List<NPCModel> allNpcs = GameController.getInstance().getNpcs();

        // 遍历当前阶段的所有NPC位置
        for (String location : npcLocations) {
            // 查找该位置的NPC
            NPCModel npc = null;
            int npcIndex = -1;

            for (int i = 0; i < allNpcs.size(); i++) {
                if (allNpcs.get(i).getLocation().equalsIgnoreCase(location.trim())) {
                    npc = allNpcs.get(i);
                    npcIndex = i;
                    break;
                }
            }

            if (npc == null) {
                continue;  // 如果找不到对应的NPC，跳过
            }

            boolean npcTasksCompleted = npc.areAllTasksCompleted();

            drawString(poseStack, this.font, npc.getLocation(), this.width / 2 - 100, yOffset, 0xFFFFFF);

            ResourceLocation icon = npcTasksCompleted ? COMPLETED : PENDING;
            RenderSystem.setShaderTexture(0, icon);
            blit(poseStack, this.width / 2 - 120, yOffset - 4, 0, 0, 16, 16, 16, 16);

            final int finalNpcIndex = npcIndex;
            this.addRenderableWidget(new Button(this.width / 2 + 90, yOffset - 5, 60, 20, new TextComponent("Go"), button -> {
                teleportToNPC(finalNpcIndex);
                // 标记该阶段的NPC已访问
                VISITED_NPCS.put(currentDisplayStage.getId(), true);
            }));

            yOffset += 25;
            if (!npcTasksCompleted) {
                allTasksCompleted = false;
            }
        }
    }

    private void renderCompletionContent(PoseStack poseStack) {
        yOffset += 10;

        String outCome = currentDisplayStage.getOutcome();
        List<ColoredText> outComeLines = TextUtils.wrapText(outCome, (int) (this.width / 1.3f), true);
        for (ColoredText line : outComeLines) {
            drawString(poseStack, this.font, line.text, this.width / 2 - 120, yOffset, line.color);
            yOffset += 10;
        }
        yOffset += 10;
    }

    private void teleportToNPC(int npcIndex) {
        if (this.minecraft.player != null) {
            this.minecraft.player.chat("/findnpc " + npcIndex);
            onClose();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        // 标记INTRO阶段为已完成
        stageManager.markStageCompleted("INTRO");
        UIScreenManager.getInstance().setCurrentScreenState(UIScreenManager.ScreenState.DEFAULT);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private StageInfo determineLastCompletedStage() {
        List<StageInfo> stages = stageManager.getAllStages();
        for (StageInfo stage : stages) {
            if (!stageManager.isStageCompleted(stage.getId())) {
                return stage;
            }
        }
        // 如果所有阶段都已完成，返回END阶段
        return stageManager.getStageById("END");
    }

    // 重置所有NPC访问状态（可用于测试）
    public static void resetAllVisitedStatus() {
        VISITED_NPCS.clear();
    }

    // 标记指定阶段的NPC已访问（可供外部调用）
    public static void markStageVisited(String stageId) {
        VISITED_NPCS.put(stageId, true);
    }
}