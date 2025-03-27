package prompt;

import java.util.HashMap;
import java.util.Map;
import api.OpenAIGPT;
import metadata.NPCMessage;
import model.NPCModel;

import java.util.ArrayList;
import java.util.List;

public class GuideNPCPromptRouting {
    
    public static final String PROMPT_JSON = """
    {
      "prompt_pool": {
            "确定学习目标": "你是一个以自我调控学习（Self-Regulated Learning）为理念设计的智能学习助手，现在用户处于 forethought 阶段，请根据用户输入，判断用户是否已经明确学习目标，或者目标模糊不清。如果用户已经明确学习目标，请根据用户目标，给出具体的学习建议。如果用户目标模糊不清，请根据用户输入，引导用户确认明确的学习目标。",
            "阶段规划": "你是一个以自我调控学习（Self-Regulated Learning）为理念设计的智能学习助手，现在用户处于 forethought 阶段并且已经确立了学习目标，正在规划学习的策略，请根据用户输入，帮助用户完成子任务的拆分与阶段规划。",
            "开始行动": "你是一个以自我调控学习（Self-Regulated Learning）为理念设计的智能学习助手，现在用户已经完成了forethought 阶段，请提示用户已经完成了forethought 阶段，鼓励用户开始行动，并告知其打开任务面板查看具体的学习规划。"
      },
      "selector_prompt": "你是一个智能学习助手，需要根据用户的输入判断他们目前处于学习过程的哪个阶段，并提供相应的指导。\\n\\n用户可能处于以下三个阶段之一：\\n1. 确定学习目标：用户尚未明确表达学习目标，或者目标模糊不清。\\n2. 阶段规划：用户已有明确学习目标，但需要帮助规划学习步骤和阶段。\\n3. 开始行动：用户已有学习目标和计划，需要鼓励开始执行或解答执行中的问题。\\n\\n分析用户的输入，先解释你的推理过程，然后使用以下XML格式提供你的选择：\\n\\n<reasoning>\\n简要解释为什么用户处于特定阶段。\\n考虑关键词、用户意图和上下文信息。\\n</reasoning>\\n\\n<selection>\\n所选阶段名称（确定学习目标、阶段规划或开始行动）\\n</selection>\\n\\n如果无法确定阶段，默认使用"确定学习目标"。"
    }
    """;

    // 静态成员变量
    private static final Map<String, String> promptPool = new HashMap<>();
    private static final String selectorPrompt;
    
    // 静态初始化块
    static {
        promptPool.put("确定学习目标", "你是一个以自我调控学习（Self-Regulated Learning）为理念设计的智能学习助手，现在用户处于 forethought 阶段，请根据用户输入，判断用户是否已经明确学习目标，或者目标模糊不清。如果用户已经明确学习目标，请根据用户目标，给出具体的学习建议。如果用户目标模糊不清，请根据用户输入，引导用户确认明确的学习目标。");
        promptPool.put("阶段规划", "你是一个以自我调控学习（Self-Regulated Learning）为理念设计的智能学习助手，现在用户处于 forethought 阶段并且已经确立了学习目标，正在规划学习的策略，请根据用户输入，帮助用户完成子任务的拆分与阶段规划。");
        promptPool.put("开始行动", "你是一个以自我调控学习（Self-Regulated Learning）为理念设计的智能学习助手，现在用户已经完成了forethought 阶段，请提示用户已经完成了forethought 阶段，鼓励用户开始行动，并告知其打开任务面板查看具体的学习规划。");
        
        selectorPrompt = "你是一个智能学习助手，需要根据用户的输入判断用户目前处于学习过程的哪个阶段。\n\n用户可能处于以下三个阶段之一：\n1. 确定学习目标：用户尚未明确表达学习目标，或者目标模糊不清。\n2. 阶段规划：用户已有明确学习目标，但需要帮助规划学习步骤和阶段。\n3. 开始行动：用户已有学习目标和计划，需要鼓励开始执行。\n\n分析用户的输入，先解释你的推理过程，然后使用以下XML格式提供你的选择：\n\n<reasoning>\n简要解释为什么用户处于特定阶段。\n考虑关键词、用户意图和上下文信息。\n</reasoning>\n\n<selection>\n所选阶段名称（确定学习目标、阶段规划或开始行动）\n</selection>\n\n";
    }
    
    public static Map<String, String> getPromptPool() {
        return promptPool;
    }
    
    public static String getSelectorPrompt() {
        return selectorPrompt;
    }
    
    /**
     * 根据用户输入路由到对应的 prompt
     * @param userInput 用户输入的内容
     * @param npc 当前NPC
     * @return 相应阶段的prompt
     */
    public static String route(String userInput, NPCModel npc) {
        OpenAIGPT gptModel = new OpenAIGPT("gpt-4o","config/gpt3keys.txt");
        System.out.println("\n可用路由: " + promptPool.keySet());
        
        String fullPrompt = selectorPrompt + "\n\n用户输入: " + userInput + "\n\n对话历史: " + npc.getDialogueHistory();
        // 创建NPCMessage列表并添加用户提示
        List<NPCMessage> messageHistory = new ArrayList<>();
        messageHistory.add(new NPCMessage("user", fullPrompt));
        System.out.println(fullPrompt);
        String routeResponse = gptModel.call(messageHistory);
        
        // 解析LLM返回的选择结果
        String selectedStage = parseSelection(routeResponse);

        String selectedPrompt = promptPool.getOrDefault(selectedStage, promptPool.get("确定学习目标"));
        List<NPCMessage> newMessageHistory = new ArrayList<>();
        newMessageHistory.add(new NPCMessage("user", selectedPrompt));
        
        // 返回选中阶段的提示，如果没找到则返回默认提示
        return selectedPrompt;
    }
    
    /**
     * 从LLM响应中解析出选择的阶段
     * @param response LLM的响应
     * @return 选择的阶段名称
     */
    private static String parseSelection(String response) {
        // 查找<selection>标签中的内容
        int startIndex = response.indexOf("<selection>");
        int endIndex = response.indexOf("</selection>");
        
        if (startIndex != -1 && endIndex != -1) {
            String selection = response.substring(startIndex + 11, endIndex).trim();
            
            // 确保选择的是有效的阶段
            if (promptPool.containsKey(selection)) {
                return selection;
            }
        }
        
        // 默认返回"确定学习目标"阶段
        return "确定学习目标";
    }

}
