package item.paper;

import java.util.Arrays;
import java.util.List;

/**
 * Agent Prompt论文物品 - 专注于智能体提示工程
 */
public class AgentPromptPaperItem extends BasePaperItem {

    public AgentPromptPaperItem() {
        super(
            "文献搜集-Agent Prompt", 
            "The Art of Prompting: Advanced Techniques for LLM Agent Instruction", 
            "Johnson et al., 2024", 
            createPaperContent(),
            "文献阅读Agent"
        );
    }
    
    private static List<String> createPaperContent() {
        return Arrays.asList(
            "Abstract:\n",
            "As large language models (LLMs) become increasingly powerful, the way we instruct these models—known as prompting—has emerged as a critical research area. This paper investigates advanced prompting techniques specifically designed for LLM agents that perform complex tasks requiring reasoning, planning, and multi-step execution. We present a systematic analysis of prompting strategies and their impact on agent performance across diverse domains including problem-solving, tool use, and multi-agent collaboration.\n",
            "Our research introduces a novel framework for constructing effective agent prompts, considering elements such as role specification, task decomposition, reasoning guidance, and self-reflection mechanisms. Through extensive empirical evaluation, we demonstrate that carefully crafted prompts can significantly enhance agent capabilities without modifying the underlying model parameters, effectively serving as a form of 'soft programming' for LLM agents.\n",
            "\nPrompting Framework:\n",
            "We propose a structured approach to agent prompting with four key components:\n",
            "1. Agent Identity: Clearly defining the agent's role, expertise, and communication style\n",
            "2. Task Structuring: Breaking complex tasks into manageable steps with clear evaluation criteria\n",
            "3. Reasoning Scaffolds: Providing frameworks that guide the agent's thinking process (e.g., Chain-of-Thought, Tree-of-Thought)\n",
            "4. Feedback Mechanisms: Implementing self-criticism and refinement processes\n",
            "\nExperimental Results:\n",
            "Our experiments show that agents equipped with structured prompts outperform baseline approaches by 37% on problem-solving benchmarks and 42% on tool-use tasks. We observe especially strong improvements in tasks requiring multi-step reasoning and planning. Additionally, we find that different prompt structures are optimal for different task types, suggesting the need for task-specific prompt engineering.\n",
            "\nApplications:\n",
            "We demonstrate the practical applications of our prompting techniques in several domains, including automated research assistants, coding agents, and collaborative problem-solving systems. Case studies reveal that well-designed prompts enable agents to complete complex tasks that would otherwise require human intervention or more specialized training.\n",
            "\nConclusion:\n",
            "Our findings highlight prompting as a powerful yet underexplored mechanism for shaping agent behavior and capabilities. The techniques presented in this paper offer a practical approach to significantly enhancing LLM agent performance without requiring model retraining, making advanced AI capabilities more accessible and customizable for diverse applications."
        );
    }
}