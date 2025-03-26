package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentBasicPosterBlockEntity extends BasePosterBlockEntity {
    public AgentBasicPosterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGENT_BASIC_POSTER_ENTITY.get(), pos, state);
        
        // 设置默认类型标识符
        this.conceptKey = "agent_concept";
        this.associatedQuestionId = "mc_agent_concept";
    }
}