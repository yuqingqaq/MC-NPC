package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentLearningPosterBlock extends BasePosterBlock {
    public AgentLearningPosterBlock() {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AgentLearningPosterBlockEntity(pos, state);
    }
    
    @Override
    protected String getPosterType() {
        return "智能体学习方法";
    }
}