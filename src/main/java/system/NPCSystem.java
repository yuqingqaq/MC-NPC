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
            String systemPrompt = "您是香港中文大学（深圳）下园校医务室的全科医生。";
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
        else{

            cautionNote = NPCPromptConfig.generateCautionNoteInZh();
            String systemPrompt = String.format("IMPORTANT Rules: %s", cautionNote);

            String taskDetails = generateTaskDetails(npc); // This method can stay here as it is specific to NPC

            if(npc.getNPCName().equals("徐扬生教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.HeadmasterPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("熊伟教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.SMEPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("唐文方教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.HSSPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("郑仲煊教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.MEDPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("唐本忠教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.SSEPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("戴建岗教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.SDSPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("叶小钢教授")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.MUSPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("图书馆工作人员")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.LibPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("体育馆工作人员")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig.GymPrompt() + userInput));
            }
            else if(npc.getNPCName().equals("朋辈心理辅导员")){
                npc.addDialogueToHistory(new NPCMessage("user", NPCPromptConfig. uBuddiesPromptZh() + userInput));
            }

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