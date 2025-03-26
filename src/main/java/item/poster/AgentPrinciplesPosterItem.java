package item.poster;

import block.poster.AcademicPosterBlockEntity;
import controller.PosterManager;

public class AgentPrinciplesPosterItem extends BasePosterItem {
    @Override
    protected void applyPosterData(AcademicPosterBlockEntity entity) {
        PosterManager.getInstance().applyAgentPrinciplesPosterToEntity(entity);
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体设计原则";
    }
}