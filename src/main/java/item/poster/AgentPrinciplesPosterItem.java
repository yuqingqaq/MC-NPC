package item.poster;

import block.poster.AgentPrinciplesPosterBlockEntity;
import controller.PosterManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentPrinciplesPosterItem extends BasePosterItem {

    public AgentPrinciplesPosterItem() {
        super(BlockRegistry.AGENT_PRINCIPLES_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        if (blockEntity instanceof AgentPrinciplesPosterBlockEntity entity) {
            PosterManager.getInstance().setupAgentPrinciplesPosterBlockEntity(entity);
        }
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体设计原则";
    }
}