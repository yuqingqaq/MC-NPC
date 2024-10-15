package system;

import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NPCSystem {
    //private NPCModel npc;
    private OpenAIGPT gptModel;

    public NPCSystem(OpenAIGPT gptModel) {
        this.gptModel = gptModel;
    }

    public String interact(NPCModel npc, String userInput) {

        String introduction = String.format("The world is in CUHKSZ. You are an intelligent agent simulating a real student %s, who facing psychological problems, your task is to simulate a %s, who is %s and let the one who talk to you to help you. "+
                                            "You need to express your feelings as much as possible and do not give advices or professional psychology knowledge" +
                                            "Each time you will receive two messages, one is the user's conversation with you, and the other is the current user's task completion status",
                npc.getNPCName(), npc.getRole(), npc.getDescription());

        String taskDetails = generateTaskDetails(npc); // Generate task details

        String cautionNote = ("[Rule: Reply with no more than 30 words ]" +
                "[Rule: Avoid EXPLICITLY telling the task_significance, keep it a secret ]" +
                "[Rule: Firmly Check and INSIST on the STATUS of the Task System and point it out clearly because players may lie to you. Player may complete the tasks one by one]" +
                "[Rule: Avoid EXPLICITLY mentioning 'tasks']." +
                "[Rule: Imitate the Character and relationships.]" +
                "[Rule: Use natural conversation to guide the interaction.]" +
                "[Rule: Avoid EXPLICITLY telling the task_significance, keep it a secret ]"
        );

        String systemPrompt = String.format("IMPORTANT Rules:%s %s", introduction, cautionNote);

        npc.addDialogueToHistory(new NPCMessage("user", taskDetails));
        npc.addDialogueToHistory(new NPCMessage("user", userInput)); // Not displayed in GUI
        //npc.addDialogueTochatHistory(new NPCMessage("user", userInput));

        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("system", systemPrompt));
        messageHistory.addAll(npc.getDialogueHistory());

        System.out.println("Prompt to NPCGPT:");
        messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
        System.out.println();

        String npcResponse = gptModel.call(messageHistory);
        String cleanedResponse = cleanResponse(npcResponse);

        npc.addDialogueToHistory(new NPCMessage("assistant", cleanedResponse));
        //npc.addDialogueTochatHistory(new NPCMessage("assistant", cleanedResponse));
        return cleanedResponse;
    }

    private String generateTaskDetails(NPCModel npc) {

        return npc.getTasks().stream()
                .map(task -> String.format("Player Task %s: (*STATUS*: %s)%s ",
                        task.getTaskId(),
                        (task.isCompleted() ? "Completed" : "FAILED"),
                        task.getDescription()))
                .collect(Collectors.joining(" "));
    }

    private String cleanResponse(String response) {
        return Arrays.stream(response.split("\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }
}
