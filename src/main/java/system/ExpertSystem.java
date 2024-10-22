package system;

import api.OpenAIGPT;
import model.NPCModel;
import metadata.NPCMessage;

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
        if ("zh".equals(language)) {
            systemPrompt = ExpertPromptConfig.SYSTEM_PROMPT_IN_CHINESE;
        } else {
            systemPrompt = ExpertPromptConfig.SYSTEM_PROMPT;
        }

        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("system", systemPrompt));

        String chatHistoryMarkdown = "```\n" + npc.getChatHistoryAsString() + "\n```";
        messageHistory.add(new NPCMessage("user", chatHistoryMarkdown + userInput));

        System.out.println("Prompt to ExpertGPT:");
        messageHistory.forEach(m -> System.out.println(m.getSender() + ": " + m.getContent()));
        System.out.println();
        String npcResponse = gptModel.call(messageHistory);
        String cleanedResponse = cleanResponse(npcResponse);

        return cleanedResponse;
    }

    private String cleanResponse(String response) {
        return Arrays.stream(response.split("\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));
    }
}