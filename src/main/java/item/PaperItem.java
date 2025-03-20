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
        this.subTaskTitle = "文献搜集-ExpeL";
        this.paperContent = List.of(
                "ExpeL: LLM Agents Are Experiential Learners\n",
                "The recent surge in research interest in applying large language models (LLMs) to decision-making tasks has flourished by leveraging the extensive world knowledge embedded in LLMs. While there is a growing demand to tailor LLMs for custom decision-making tasks, finetuning them for specific tasks is resource-intensive and may diminish the model's generalization capabilities. Moreover, state-of-the-art language models like GPT-4 and Claude are primarily accessible through API calls, with their parametric weights remaining proprietary and unavailable to the public. This scenario emphasizes the growing need for new methodologies that allow learning from agent experiences without requiring parametric updates. To address these problems, we introduce the Experiential Learning (ExpeL) agent. Our agent autonomously gathers experiences and extracts knowledge using natural language from a collection of training tasks. At inference, the agent recalls its extracted insights and past experiences to make informed decisions. Our empirical results highlight the robust learning efficacy of the ExpeL agent, indicating a consistent enhancement in its performance as it accumulates experiences. We further explore the emerging capabilities and transfer learning potential of the ExpeL agent through qualitative observations and additional experiments."
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