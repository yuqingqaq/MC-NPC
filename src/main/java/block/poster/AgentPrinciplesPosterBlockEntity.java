package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentPrinciplesPosterBlockEntity extends BasePosterBlockEntity {
    public AgentPrinciplesPosterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGENT_PRINCIPLES_POSTER_ENTITY.get(), pos, state);
        
        // 设置默认类型标识符
        this.conceptKey = "agent_principle";
        this.associatedQuestionId = "tf_learning_principle";
    }
}