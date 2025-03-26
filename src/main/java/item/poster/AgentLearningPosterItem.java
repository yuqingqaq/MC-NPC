package item.poster;

import block.poster.AgentLearningPosterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentLearningPosterItem extends BasePosterItem {

    public AgentLearningPosterItem() {
        super(BlockRegistry.AGENT_LEARNING_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        // 不需要额外处理，使用时会从PosterManager获取数据
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体学习方法";
    }
}