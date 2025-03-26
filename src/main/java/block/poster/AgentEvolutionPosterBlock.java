package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

public class AgentEvolutionPosterBlock extends BasePosterBlock {
    public AgentEvolutionPosterBlock() {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AgentEvolutionPosterBlockEntity(pos, state);
    }

    @Override
    protected String getPosterType() {
        return "智能体技术演化";
    }
}