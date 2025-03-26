package item.poster;

import block.poster.AcademicPosterBlockEntity;
import controller.PosterManager;

public class AgentLearningPosterItem extends BasePosterItem {
    @Override
    protected void applyPosterData(AcademicPosterBlockEntity entity) {
        PosterManager.getInstance().applyAgentLearningPosterToEntity(entity);
    }

    @Override
    protected String getPosterTypeName() {
        return "智能体学习方法";
    }
}