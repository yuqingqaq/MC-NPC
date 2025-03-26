package item.poster;

import block.poster.AgentLearningPosterBlockEntity;
import controller.PosterManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentLearningPosterItem extends BasePosterItem {

    public AgentLearningPosterItem() {
        super(BlockRegistry.AGENT_LEARNING_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        if (blockEntity instanceof AgentLearningPosterBlockEntity entity) {
            PosterManager.getInstance().setupAgentLearningPosterBlockEntity(entity);
        }
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体学习方法";
    }
}