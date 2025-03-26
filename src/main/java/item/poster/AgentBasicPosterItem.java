package item.poster;

import block.poster.AgentBasicPosterBlockEntity;
import controller.PosterManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentBasicPosterItem extends BasePosterItem {

    public AgentBasicPosterItem() {
        super(BlockRegistry.AGENT_BASIC_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        if (blockEntity instanceof AgentBasicPosterBlockEntity entity) {
            PosterManager.getInstance().setupAgentBasicPosterBlockEntity(entity);
        }
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体基础概念";
    }
}