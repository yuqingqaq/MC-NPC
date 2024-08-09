package api.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import metadata.NPCMessage;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Static method to convert from NPCMessage to Message
    public static Message fromNPCMessage(NPCMessage npcMessage) {
        return new Message(npcMessage.getSender(), npcMessage.getContent());
    }
}