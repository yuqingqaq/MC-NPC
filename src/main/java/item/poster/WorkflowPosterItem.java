package item.poster;

public class WorkflowPosterItem extends BasePosterItem {
    @Override
    protected String getPosterType() {
        return "workflow";
    }

    @Override
    protected String getPosterDisplayName() {
        return "智能体工作流海报";
    }
}