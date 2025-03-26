package item.poster;

import block.poster.AcademicPosterBlockEntity;
import controller.PosterManager;

public class AgentBasicPosterItem extends BasePosterItem {
    @Override
    protected void applyPosterData(AcademicPosterBlockEntity entity) {
        PosterManager.getInstance().applyAgentBasicPosterToEntity(entity);
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体基础概念";
    }
}