package com.freedomai.projectpn.metadata;

public class NPCMessage {
    private String sender;
    private String content;

    public NPCMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}