package system;

import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NPCSystem {
    private OpenAIGPT gptModel;

    public NPCSystem(OpenAIGPT gptModel) {
        this.gptModel = gptModel;
    }

    public String interact(NPCModel npc, String userInput, String language) {
        String introduction, cautionNote;

        if ("zh".equals(language)) {
            introduction = NPCPromptConfig.generateIntroductionInZh(npc.getNPCName(), npc.getRole(), npc.getDescription());
            cautionNote = NPCPromptConfig.generateCautionNoteInZh();
        } else {
            introduction = NPCPromptConfig.generateIntroduction(npc.getNPCName(), npc.getRole(), npc.getDescription());
            cautionNote = NPCPromptConfig.generateCautionNote();
        }

        String systemPrompt = String.format("IMPORTANT Rules:%s %s", introduction, cautionNote);

        String taskDetails = generateTaskDetails(npc); // This method can stay here as it is specific to NPC

        npc.addDialogueToHistory(new NPCMessage("user", taskDetails));
        npc.addDialogueToHistory(new NPCMessage("user", userInput));

        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("system", systemPrompt));
        messageHistory.addAll(npc.getDialogueHistory());

        System.out.println("Prompt to NPCGPT:");
        messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
        System.out.println();

        String npcResponse = gptModel.call(messageHistory);
        String cleanedResponse = cleanResponse(npcResponse);

        npc.addDialogueToHistory(new NPCMessage("assistant", cleanedResponse));
        return cleanedResponse;
    }

    private String generateTaskDetails(NPCModel npc) {
        // Task details generation code remains the same
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