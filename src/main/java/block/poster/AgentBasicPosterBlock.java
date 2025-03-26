package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentBasicPosterBlock extends BasePosterBlock {
    public AgentBasicPosterBlock() {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AgentBasicPosterBlockEntity(pos, state);
    }
    
    @Override
    protected String getPosterType() {
        return "智能体基础概念";
    }
}