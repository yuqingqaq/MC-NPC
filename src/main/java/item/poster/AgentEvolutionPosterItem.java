package item.poster;

import block.poster.AgentEvolutionPosterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentEvolutionPosterItem extends BasePosterItem {

    public AgentEvolutionPosterItem() {
        super(BlockRegistry.AGENT_EVOLUTION_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        // 不需要额外处理，使用时会从PosterManager获取数据
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体技术演化";
    }
}