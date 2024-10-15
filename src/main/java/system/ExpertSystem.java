package system;

import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExpertSystem {
    //private NPCModel npc;
    private OpenAIGPT gptModel;

    public ExpertSystem(OpenAIGPT gptModel) {
        this.gptModel = gptModel;
    }

    public String interact(NPCModel npc, String userInput) {


        String systemPrompt = String.format("You are an third-persepective expert in mental health, you need to give professional advices to player to continue the conversation, with no more than 50 words. ");

        //npc.addDialogueToHistory(new NPCMessage("user", userInput)); // Not displayed in GUI
        //npc.addDialogueTochatHistory(new NPCMessage("user", userInput));

        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("system", systemPrompt));
        //messageHistory.addAll(npc.getDialogueHistory());

        messageHistory.add(new NPCMessage("user", npc.getChatHistoryAsString() + userInput));

        System.out.println("Prompt to ExpertGPT:");
        messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
        System.out.println();
        String npcResponse = gptModel.call(messageHistory);
        String cleanedResponse = cleanResponse(npcResponse);

        //npc.addDialogueToHistory(new NPCMessage("assistant", cleanedResponse));
        //npc.addDialogueTochatHistory(new NPCMessage("assistant", cleanedResponse));
        return cleanedResponse;
    }

    private String cleanResponse(String response) {
        return Arrays.stream(response.split("\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }
}
