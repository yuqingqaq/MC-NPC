package item.knowledge;

import item.knowledge.data.MultipleChoiceData;
import item.knowledge.data.QuestionData;
import item.knowledge.data.QuestionRepository;

import model.NPCModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import npcopenai.NPCOpenAI;

public class QuestionItem extends Item {
    private final String questionId;  // 题目的唯一ID

    public QuestionItem(String questionId) {
        super(new Properties().tab(CreativeModeTab.TAB_MISC));
        this.questionId = questionId;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        NPCOpenAI.getLogger().info("QuestionItem used with ID: " + questionId);

        if (world.isClientSide && context.getPlayer() != null) {
            // 获取题目数据
            QuestionData questionData = QuestionRepository.getQuestionById(questionId);
            if (questionData == null) {
                NPCOpenAI.getLogger().error("Question data not found for ID: " + questionId);
                return InteractionResult.FAIL;
            }

            // 创建NPC模型
            NPCModel npcModel = new NPCModel(questionData.getExpertType());

            // 打开题目界面
            QuestionClientHandler.openQuestionScreen(questionData, npcModel);
        }

        return InteractionResult.SUCCESS;
    }
}