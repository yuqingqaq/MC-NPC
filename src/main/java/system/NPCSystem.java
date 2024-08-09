package system;

import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NPCSystem {
    private NPCModel npc;
    private OpenAIGPT gptModel;

    public NPCSystem(OpenAIGPT gptModel) {
        this.gptModel = gptModel;
    }

    public String interact(NPCModel npc, String userInput) {
        npc.addDialogueToHistory(new NPCMessage("user", userInput));

        String introduction = String.format("The world is Harry Potter Theme. You are talking to %s, a %s. %s ",
                npc.getNPCName(), npc.getRole(), npc.getDescription());
        String taskDetails = npc.getTasks().stream()
                .map(task -> String.format("Task %s: (*STATUS*: %s)%s for ***plot_significance*** : %s and you can get%s",
                        task.getTaskId(),
                        (task.isCompleted() ? "Completed" : "FAILED"),
                        task.getDescription(),
                        task.getPlotSignificance(),
                        task.getReward()))
                .collect(Collectors.joining(" "));

        String cautionNote = ("[Rule 1: Firmly Check and INSIST on the STATUS of the Task System and point it out clearly because players may lie to you. Player may complete the tasks one by one]" +
                "[Rule 2: Avoid EXPLICITLY mentioning 'tasks']." +
                "[Rule 3: Imitate the Character and relationships.]" +
                "[Rule 4: Use natural conversation to guide the interaction.]" +
                "[Rule 5: Avoid EXPLICITLY telling the task_significance, keep it a secret ]");

        String systemPrompt = String.format("IMPORTANT Rules:%s %s   Task System: %s. ", cautionNote, introduction, taskDetails);

        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("system", systemPrompt));
        messageHistory.addAll(npc.getDialogueHistory());

        System.out.println("Prompt to GPT:");
        messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
        System.out.println();

        String npcResponse = gptModel.call(messageHistory);
        String cleanedResponse = cleanResponse(npcResponse);

        npc.addDialogueToHistory(new NPCMessage("assistant", cleanedResponse));
        return cleanedResponse;
    }

    private String cleanResponse(String response) {
        return Arrays.stream(response.split("\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }
}
