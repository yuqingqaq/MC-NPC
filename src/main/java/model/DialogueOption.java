package model;

public class DialogueOption {
    private String text;
    private String triggerTaskId;

    public DialogueOption(String text, String triggerTaskId) {
        this.text = text;
        this.triggerTaskId = triggerTaskId;
    }

    public String getText() {
        return text;
    }

    public String getTriggerTaskId() {
        return triggerTaskId;
    }
}
