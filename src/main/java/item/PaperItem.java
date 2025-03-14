package item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import npcopenai.NPCOpenAI;
import model.NPCModel;

import java.util.List;

public class PaperItem extends Item {
    private final String subTaskTitle;             // subTask
    private final List<String> paperContent; // 论文内容

    public PaperItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
        this.subTaskTitle = "文献搜集";
        this.paperContent = List.of(
                "Language Models as Zero-Shot Planners: Extracting Actionable Knowledge for Embodied Agents",
                "Dynamic llm-agent network: An llm-agent collaboration framework with agent team optimization",
                "Multi-agent collaboration: Harnessing the power of intelligent llm agents",
                "Llm-powered hierarchical language agent for real-time human-ai coordination"
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("item.PaperItem used on: " + context.getClickedPos());

        if (world.isClientSide) { // 客户端逻辑
            NPCOpenAI.getLogger().info("Executing on client side");
            if (context.getPlayer() != null) {
                String title = this.subTaskTitle;
                List<String> content = this.paperContent;
                NPCModel npcModel = createNPCModel();

                // 安全调用 PaperItemClientHandler
                PaperItemClientHandler.openPaperReviewScreen(title, content, npcModel);
            } else {
                NPCOpenAI.getLogger().info("No player found");
            }
        } else {
            NPCOpenAI.getLogger().info("This action is server-side, passing");
        }
        return InteractionResult.SUCCESS;
    }

    // 初始化 NPCModel
    private NPCModel createNPCModel() {
        NPCModel npcModel = new NPCModel("文献阅读Agent");
        return npcModel;
    }
}