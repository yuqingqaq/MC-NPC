package gui.academic.writing;

import gui.academic.writing.AbstractEditorScreen;
import model.NPCModel;

public class PaperEditorScreen extends AbstractEditorScreen {

    public PaperEditorScreen(NPCModel npc) {
        super(npc, "Paper Editor with " + npc.getNPCName());
    }

    @Override
    protected String getScreenTitle() {
        return "Paper Editor with " + currentNPC.getNPCName();
    }

    @Override
    protected String getTitlePlaceholder() {
        return "Enter Paper Title";
    }

    @Override
    protected String getQuestionPlaceholder() {
        return "Enter Your Question";
    }
}