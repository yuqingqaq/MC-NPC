package item.poster;

import block.poster.AgentEvolutionPosterBlockEntity;
import controller.PosterManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentEvolutionPosterItem extends BasePosterItem {

    public AgentEvolutionPosterItem() {
        super(BlockRegistry.AGENT_EVOLUTION_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        if (blockEntity instanceof AgentEvolutionPosterBlockEntity entity) {
            PosterManager.getInstance().setupAgentEvolutionPosterBlockEntity(entity);
        }
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体技术演化";
    }
}