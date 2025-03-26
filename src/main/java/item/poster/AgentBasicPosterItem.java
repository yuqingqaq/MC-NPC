package item.poster;

import block.poster.AgentBasicPosterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import registry.BlockRegistry;

public class AgentBasicPosterItem extends BasePosterItem {

    public AgentBasicPosterItem() {
        super(BlockRegistry.AGENT_BASIC_POSTER.get());
    }

    @Override
    protected void applyPosterData(BlockEntity blockEntity) {
        // 不需要额外处理，使用时会从PosterManager获取数据
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体基础概念";
    }
}