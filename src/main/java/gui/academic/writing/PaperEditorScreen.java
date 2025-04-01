package gui.academic.writing;

import gui.academic.writing.AbstractEditorScreen;
import model.NPCModel;
import system.TaskManager;

public class PaperEditorScreen extends AbstractEditorScreen {
    private static final String FIND_ADVISOR_TASK_TITLE = "找到论文写作辅导员";
    private static final String OUTLINE_TASK_TITLE = "报告大纲撰写";

    public PaperEditorScreen(NPCModel npc) {
        // 设置要保存内容的任务为"报告大纲撰写"
        super(npc, "学习报告大纲写作", OUTLINE_TASK_TITLE);

        // 打开界面时，自动完成"找到论文写作辅导员"任务，并显示通知
        TaskManager.getInstance().completeSubTaskByTitle(FIND_ADVISOR_TASK_TITLE, true);

        // TaskManager会自动启动下一个子任务（报告大纲撰写）
    }

    @Override
    protected String getScreenTitle() {
        return "学习报告大纲写作";
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