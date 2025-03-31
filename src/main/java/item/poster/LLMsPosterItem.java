package item.poster;

public class LLMsPosterItem extends BasePosterItem {
    @Override
    protected String getPosterType() {
        return "llms";
    }

    @Override
    protected String getPosterDisplayName() {
        return "大型语言模型海报";
    }
}