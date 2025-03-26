package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentLearningPosterBlockEntity extends BasePosterBlockEntity {
    public AgentLearningPosterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGENT_LEARNING_POSTER_ENTITY.get(), pos, state);
        
        // 设置默认类型标识符
        this.conceptKey = "agent_relationship";
        this.associatedQuestionId = "match_learning_methods";
    }
}