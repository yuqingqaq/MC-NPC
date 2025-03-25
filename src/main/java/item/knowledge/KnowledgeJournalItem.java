package item.knowledge;

import gui.academic.knowledge.KnowledgeGraphScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import npcopenai.NPCOpenAI;

public class KnowledgeJournalItem extends Item {

    public KnowledgeJournalItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("item.KnowledgeJournalItem used on: " + context.getClickedPos());

        if (world.isClientSide) { // 客户端逻辑
            if (context.getPlayer() != null) {
                // 打开知识图谱界面
                KnowledgeJournalClientHandler.openKnowledgeGraphScreen();
            }
        }
        return InteractionResult.SUCCESS;
    }
}

