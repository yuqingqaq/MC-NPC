package system;

import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;
import prompt.ExpertPromptConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExpertSystem {
    private OpenAIGPT gptModel;

    public ExpertSystem(OpenAIGPT gptModel) {
        this.gptModel = gptModel;
    }

    public String interact(NPCModel npc, String userInput, String language) {
        String systemPrompt;
        if ("邮件写作大师".equals(npc.getNPCName().trim())) {
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.EMAIL_WRITING_MASTER_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.EMAIL_WRITING_MASTER_PROMPT;
            }
        } else if ("论文写作大师".equals(npc.getNPCName().trim())) {
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.PAPER_WRITING_MASTER_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.PAPER_WRITING_MASTER_PROMPT;
            }
        } else if ("文献阅读Agent".equals(npc.getNPCName().trim())) {
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.PAPER_REVIEW_MASTER_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.PAPER_REVIEW_MASTER_PROMPT;
            }
        }else if ("任务总结Agent".equals(npc.getNPCName().trim())) {
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.SUMMARY_MASTER_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.SUMMARY_MASTER_PROMPT;
            }
        }
        else if ("任务排序Agent".equals(npc.getNPCName().trim())) {
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.PLANNING_MASTER_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.PLANNING_MASTER_PROMPT;
            }
        } else if ("Question Expert".equals(npc.getNPCName().trim())) {
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.QUESTION_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.QUESTION_MASTER_PROMPT;
            }
        }
        else {
            // 默认使用心理健康 Prompt
            if ("zh".equals(language)) {
                systemPrompt = ExpertPromptConfig.SYSTEM_PROMPT_IN_CHINESE;
            } else {
                systemPrompt = ExpertPromptConfig.SYSTEM_PROMPT;
            }
        }
        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("system", systemPrompt));
        messageHistory.addAll(npc.getDialogueHistory());

        String chatHistoryMarkdown = "下面你会得到一段用户与NPC的对话，请根据对话内容给出你的建议：" + "```\n" + npc.getChatHistoryAsString() + "\n```";
        messageHistory.add(new NPCMessage("user", chatHistoryMarkdown + userInput));
        npc.addDialogueToHistory(new NPCMessage("user", userInput));

        System.out.println("Prompt to ExpertGPT:");
        messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
        // System.out.println(chatHistoryMarkdown);
        System.out.println();
        String npcResponse = gptModel.call(messageHistory);
        String cleanedResponse = cleanResponse(npcResponse);
        System.out.println(cleanedResponse);

        // 更新 NPC 的对话历史
        npc.addDialogueToHistory(new NPCMessage("assistant", cleanedResponse));

        return cleanedResponse;
    }

    private String cleanResponse(String response) {
        return Arrays.stream(response.split("\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }
}