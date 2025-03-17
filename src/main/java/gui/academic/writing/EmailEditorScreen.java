package gui.academic.writing;

import gui.academic.writing.AbstractEditorScreen;
import model.NPCModel;

public class EmailEditorScreen extends AbstractEditorScreen {

    public EmailEditorScreen(NPCModel npc) {
        super(npc, "Email Editor with " + npc.getNPCName());
    }

    @Override
    protected String getScreenTitle() {
        return "Email Editor with " + currentNPC.getNPCName();
    }

    @Override
    protected String getTitlePlaceholder() {
        return "Enter Email Title";
    }

    @Override
    protected String getQuestionPlaceholder() {
        return "Enter Your Question";
    }
}