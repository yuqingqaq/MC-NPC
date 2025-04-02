package item.paper;

import java.util.Arrays;
import java.util.List;

/**
 * Agent Prompt论文物品 - 专注于智能体提示工程
 */
public class AgentPromptPaperItem extends BasePaperItem {

    public AgentPromptPaperItem() {
        super(
"文献搜集-ToolFormer",
"Toolformer: Language Models Can Teach Themselves to Use Tools",
"Timo Schick, Jane Dwivedi-Yu, et al., 2023",
            createPaperContent(),
            "文献阅读Agent"
        );
    }
    
    private static List<String> createPaperContent() {
        return Arrays.asList(
                "摘要:\n",
                "Toolformer 提出了一个新框架，赋予语言模型调用外部工具的能力，如计算器、翻译 API 和搜索引擎等，显著扩展了语言模型的功能。与传统需要大量人工设计的工具调用框架不同，Toolformer 通过自监督学习生成工具调用数据，并让模型自动学习在适当场景使用工具。它展示了大语言模型不仅可以生成文本，还能通过调用外部工具完成语言模型原生无法处理的复杂任务。\n",
                "\n背景:\n",
                "语言模型（如 GPT）在静态训练数据上学习，但缺乏动态实时处理能力。Toolformer 的目标是让语言模型能够动态调用外部工具，从而突破这些限制，让模型更加通用和实用。\n",
                "\n核心技术:\n",
                "自监督工具调用：\n",
                "  自动生成工具调用标注：\n",
                " Toolformer 在少量标注数据的基础上，通过自监督学习生成大规模的工具调用训练数据。\n",
                "    例如，在给定任务中插入工具调用的上下文，并记录其结果。\n",
                "  训练阶段：\n",
                " 模型学会自主判断何时需要调用工具，并在生成过程中动态插入调用指令。\n",
                "工具调用模块：\n",
                "  工具类型：\n",
                " 支持多种工具，包括：\n",
                "    计算器： 用于数学问题。\n",
                "    翻译 API： 用于语言转换任务。\n",
                "    搜索引擎： 用于实时知识查询。\n",
                "  调用流程：\n",
                " 模型通过生成调用代码（如 API 请求）与工具对接，获取结果后将其与生成过程结合。\n",
                "动态工具选择：\n",
                "  模型在推理过程中判断是否需要调用工具，以及调用哪种工具。\n",
                "  工具调用是以自然语言生成的代码形式，符合语言模型生成的工作机制。\n",
                "\n实验结果:\n",
                "  Toolformer 在需要外部帮助的任务上显著优于不调用工具的模型。\n",
                "  尤其在数学计算、翻译任务和实时信息查询上效果突出。\n",
                "\n链接:\n",
                "arXiv:2302.04761"       );
    }
}