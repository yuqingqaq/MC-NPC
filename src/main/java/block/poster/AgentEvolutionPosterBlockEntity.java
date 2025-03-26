package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentEvolutionPosterBlockEntity extends BasePosterBlockEntity {
    public AgentEvolutionPosterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGENT_EVOLUTION_POSTER_ENTITY.get(), pos, state);
        
        // 设置默认类型标识符
        this.conceptKey = "agent_timeline";
        this.associatedQuestionId = "order_tech_evolution";
    }
}