package item.poster;

import block.poster.AgentPrinciplesPosterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentPrinciplesPosterItem extends BasePosterItem {

    public AgentPrinciplesPosterItem() {
        super(BlockRegistry.AGENT_PRINCIPLES_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        // 不需要额外处理，使用时会从PosterManager获取数据
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体设计原则";
    }
}