package util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TooltipUtil {

    // 添加金币的 Tooltip
    public static void addGoldCoinTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip) {
        tooltip.add(new TextComponent("§6金币：用于完成任务"));
        tooltip.add(new TextComponent("§8拾取后完成当前任务，生成下一个金币"));
    }

    // 添加知识日记的 Tooltip
    public static void addKnowledgeJournalTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip) {
        tooltip.add(new TextComponent("§6知识日记：打开知识图谱"));
        tooltip.add(new TextComponent("§8右键地面使用以查看知识图谱"));
    }

    // 添加题目卡的 Tooltip
    public static void addQuestionTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, String questionId) {
        tooltip.add(new TextComponent("§6问题卡：用于解答题目"));
        tooltip.add(new TextComponent("§7题目 ID: " + questionId));
        tooltip.add(new TextComponent("§8右键地面查看和解答问题"));
    }

    // 添加论文的 Tooltip
    public static void addPaperTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, String title) {
        tooltip.add(new TextComponent("§6论文：" + title));
        tooltip.add(new TextComponent("§8右键地面查看详细内容"));
    }

    // 添加自定义任务的 Tooltip
    public static void addCustomTaskTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip) {
        tooltip.add(new TextComponent("§6任务查看器：查看任务概览"));
        tooltip.add(new TextComponent("§8右键地面打开任务总览"));
    }

    // 添加时间排序任务的 Tooltip
    public static void addTimedTaskTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip) {
        tooltip.add(new TextComponent("§6校园导览任务：查看校园导览任务"));
        tooltip.add(new TextComponent("§8右键地面打开校园导览"));
    }

    // 添加 NPC列表界面 的 Tooltip
    public static void addCustomItemTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip) {
        tooltip.add(new TextComponent("§6NPC查看器：查看NPC总览"));
        tooltip.add(new TextComponent("§7右键使用以打开NPC列表界面"));
    }
}