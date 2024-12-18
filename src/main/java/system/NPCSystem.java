package system;

import clinic.huatuoAPI;
import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;
import prompt.NPCPromptConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NPCSystem {
    private OpenAIGPT gptModel;
    private huatuoAPI clinicAgent;

    public NPCSystem(OpenAIGPT gptModel,huatuoAPI clinicAgent) {
        this.gptModel = gptModel;
        this.clinicAgent = clinicAgent;
    }

    public String interact(NPCModel npc, String userInput, String language) {
        String introduction, cautionNote, npcResponse;

        if (npc.getRole().equals("下园校医务室-全科医生")){
            String systemPrompt = "您是香港中文大学（深圳）校医务室的全科医生。";
            npc.addDialogueToHistory(new NPCMessage("user", userInput));
            List<NPCMessage> messageHistory = new ArrayList<>();
            messageHistory.add(new NPCMessage("system", systemPrompt));
            messageHistory.addAll(npc.getDialogueHistory());
            npcResponse = clinicAgent.call(messageHistory);
            System.out.println("NPC Response:");
            System.out.println(npcResponse);
            System.out.println();
//            npcResponse = "Testtest";
        }
        else {
            if ("zh".equals(language)) {
                introduction = NPCPromptConfig.generateIntroductionInZh(npc.getNPCName(), npc.getRole(), npc.getDescription(), npc.getRelationship());
                cautionNote = NPCPromptConfig.generateCautionNoteInZh();
            } else {
                introduction = NPCPromptConfig.generateIntroduction(npc.getNPCName(), npc.getRole(), npc.getDescription(), npc.getRelationship());
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

            npcResponse = gptModel.call(messageHistory);
        }



        String cleanedResponse = cleanResponse(npcResponse);

        npc.addDialogueToHistory(new NPCMessage("assistant", cleanedResponse));

        // Check if the dialogue completion condition is met
        if (npc.checkDialogueCompletionCondition()) {
            npc.completeAllTasks();
        }
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