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
        String systemPrompt, npcResponse;

        if (npc.getRole().equals("下园校医务室-全科医生")) {
            systemPrompt = "您是香港中文大学（深圳）下园校医务室的全科医生。";
            npc.addDialogueToHistory(new NPCMessage("user", userInput));
            List<NPCMessage> messageHistory = new ArrayList<>();
            messageHistory.add(new NPCMessage("system", systemPrompt));
            messageHistory.addAll(npc.getDialogueHistory());
            npcResponse = clinicAgent.call(messageHistory);
            System.out.println("NPC Response:");
            System.out.println(npcResponse);
            System.out.println();
        } else {
            String cautionNote = NPCPromptConfig.generateCautionNoteInZh();
            String taskDetails = generateTaskDetails(npc); // This method can stay here as it is specific to NPC

            // 基于NPC类型构建system prompt
            if(npc.getNPCName().equals("徐扬生教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.HeadmasterPrompt());
            } else if(npc.getNPCName().equals("熊伟教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.SMEPrompt());
            } else if(npc.getNPCName().equals("唐文方教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.HSSPrompt());
            } else if(npc.getNPCName().equals("郑仲煊教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.MEDPrompt());
            } else if(npc.getNPCName().equals("唐本忠教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.SSEPrompt());
            } else if(npc.getNPCName().equals("戴建岗教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.SDSPrompt());
            } else if(npc.getNPCName().equals("叶小钢教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.MUSPrompt());
            } else if(npc.getNPCName().equals("罗智泉教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.SRIBDPrompt());
            } else if(npc.getNPCName().equals("图书馆工作人员")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.LibPrompt());
            } else if(npc.getNPCName().equals("体育馆工作人员")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.GymPrompt());
            } else if(npc.getNPCName().equals("朋辈心理辅导员")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.uBuddiesPromptZh());
            } else if(npc.getNPCName().equals("王教授")) {
                systemPrompt = String.format("IMPORTANT Rules: %s\n%s", cautionNote, NPCPromptConfig.ProfWangPromptZh());
            } else {
                // 默认system prompt
                systemPrompt = String.format("IMPORTANT Rules: %s", cautionNote);
            }

            // 直接添加用户输入，不带提示词
            npc.addDialogueToHistory(new NPCMessage("user", userInput));

            List<NPCMessage> messageHistory = new ArrayList<>();
            messageHistory.add(new NPCMessage("system", systemPrompt));
            messageHistory.addAll(npc.getDialogueHistory());

            System.out.println("Prompt to NPCGPT:");
            messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
            System.out.println();

            npcResponse = gptModel.call(messageHistory);
        }

        return npcResponse;
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