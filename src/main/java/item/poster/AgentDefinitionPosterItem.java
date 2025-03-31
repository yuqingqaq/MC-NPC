package item.poster;

public class AgentDefinitionPosterItem extends BasePosterItem {
    @Override
    protected String getPosterType() {
        return "agent_definition";
    }

    @Override
    protected String getPosterDisplayName() {
        return "智能体定义海报";
    }
}