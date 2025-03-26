package item.paper;

import java.util.Arrays;
import java.util.List;

/**
 * ExpeL论文物品 - 专注于智能体经验学习
 */
public class ExcelPaperItem extends BasePaperItem {

    public ExcelPaperItem() {
        super(
            "文献搜集-ExpeL", 
            "ExpeL: LLM Agents Are Experiential Learners", 
            "Wang et al., 2023", 
            createPaperContent(),
            "文献阅读Agent"
        );
    }
    
    private static List<String> createPaperContent() {
        return Arrays.asList(
            "Abstract:\n",
            "The recent surge in research interest in applying large language models (LLMs) to decision-making tasks has flourished by leveraging the extensive world knowledge embedded in LLMs. While there is a growing demand to tailor LLMs for custom decision-making tasks, finetuning them for specific tasks is resource-intensive and may diminish the model's generalization capabilities. Moreover, state-of-the-art language models like GPT-4 and Claude are primarily accessible through API calls, with their parametric weights remaining proprietary and unavailable to the public. This scenario emphasizes the growing need for new methodologies that allow learning from agent experiences without requiring parametric updates.\n",
            "To address these problems, we introduce the Experiential Learning (ExpeL) agent. Our agent autonomously gathers experiences and extracts knowledge using natural language from a collection of training tasks. At inference, the agent recalls its extracted insights and past experiences to make informed decisions. Our empirical results highlight the robust learning efficacy of the ExpeL agent, indicating a consistent enhancement in its performance as it accumulates experiences. We further explore the emerging capabilities and transfer learning potential of the ExpeL agent through qualitative observations and additional experiments.\n",
            "\nMethod:\n",
            "The ExpeL framework consists of three main components: (1) Experience Collection, where the agent interacts with tasks and stores both successful and failed attempts; (2) Knowledge Extraction, where the agent analyzes its experiences to form generalizable insights; and (3) Experience Recall, where the agent leverages past experiences and extracted knowledge to solve new tasks.\n",
            "\nResults:\n",
            "Our experiments across various environments show that ExpeL agents achieve significantly better performance compared to standard prompting methods. The agent's performance improves with more experiences, and it demonstrates the ability to transfer knowledge across similar task domains. Qualitative analysis reveals that the agent develops strategic thinking patterns and learns to avoid common pitfalls through its experiences.\n",
            "\nConclusion:\n",
            "The ExpeL framework offers a promising approach for enhancing LLM agent capabilities without parametric updates, enabling experiential learning purely through natural language. This approach opens new possibilities for developing adaptive agents that continuously improve through their interactions with the environment."
        );
    }
}