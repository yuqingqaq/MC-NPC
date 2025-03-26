package item.poster;

import block.poster.AcademicPosterBlockEntity;
import controller.PosterManager;

public class AgentEvolutionPosterItem extends BasePosterItem {
    @Override
    protected void applyPosterData(AcademicPosterBlockEntity entity) {
        PosterManager.getInstance().applyAgentEvolutionPosterToEntity(entity);
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体技术演化";
    }
}