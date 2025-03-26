package item.paper;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import util.TooltipUtil;
import java.util.List;

import javax.annotation.Nullable;

import npcopenai.NPCOpenAI;
import model.NPCModel;

import java.util.List;

/**
 * 论文物品基类，用于表示可阅读的科研论文
 */
public abstract class BasePaperItem extends Item {
    private final String subTaskTitle;    // 子任务标题
    private final List<String> paperContent; // 论文内容
    private final String paperTitle;      // 论文标题
    private final String paperAuthors;    // 论文作者
    private final String npcExpertType;   // 对应的NPC专家类型

    public BasePaperItem(String subTaskTitle, String paperTitle, String paperAuthors, 
                         List<String> paperContent, String npcExpertType) {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
        this.subTaskTitle = subTaskTitle;
        this.paperTitle = paperTitle;
        this.paperAuthors = paperAuthors;
        this.paperContent = paperContent;
        this.npcExpertType = npcExpertType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("BasePaperItem used: " + getPaperTitle());

        if (world.isClientSide) { // 客户端逻辑
            if (context.getPlayer() != null) {
                NPCModel npcModel = createNPCModel();

                // 打开论文阅读界面
                PaperItemClientHandler.openPaperReviewScreen(
                    getSubTaskTitle(), 
                    getPaperContent(), 
                    npcModel,
                    getPaperTitle(),
                    getPaperAuthors()
                );
            } else {
                NPCOpenAI.getLogger().info("No player found");
            }
        }
        return InteractionResult.SUCCESS;
    }

    // 初始化 NPCModel
    protected NPCModel createNPCModel() {
        return new NPCModel(npcExpertType);
    }

    // Getters
    public String getSubTaskTitle() {
        return subTaskTitle;
    }

    public String getPaperTitle() {
        return paperTitle;
    }

    public String getPaperAuthors() {
        return paperAuthors;
    }

    public List<String> getPaperContent() {
        return paperContent;
    }

    public String getNpcExpertType() {
        return npcExpertType;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加 Tooltip 信息
        tooltip.add(new TextComponent("§6论文标题: " + paperTitle));
        tooltip.add(new TextComponent("§7作者: " + paperAuthors));
        tooltip.add(new TextComponent("§8右键使用以查看详细内容"));
    }
}