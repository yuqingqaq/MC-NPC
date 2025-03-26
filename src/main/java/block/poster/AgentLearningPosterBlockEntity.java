package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentLearningPosterBlockEntity extends BasePosterBlockEntity {
    public AgentLearningPosterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGENT_LEARNING_POSTER_ENTITY.get(), pos, state);
    }
}