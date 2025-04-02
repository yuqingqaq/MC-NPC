package item.paper;

import java.util.Arrays;
import java.util.List;

/**
 * ExpeL论文物品 - 专注于智能体经验学习
 */
public class ExcelPaperItem extends BasePaperItem {

    public ExcelPaperItem() {
        super(
                "文献搜集-CoT",
                "Self-Consistency Improves Chain of Thought Reasoning in Language Models",
                "Wang et al., 2023",
                createPaperContent(),
                "文献阅读Agent"
        );
    }

    private static List<String> createPaperContent() {
        return Arrays.asList(
                "摘要：\n",
                "Chain of Thought (CoT) 是一种提示工程技术，用于提升大语言模型在复杂推理任务中的表现。通过提示模型逐步思考，CoT 能够生成连贯的推理链，将问题分解为多个可管理的步骤。论文还提出了 Self-Consistency 策略，通过对多条推理路径进行采样和投票，进一步提高了模型的准确性和稳健性。\n",
                "背景：\n",
                "传统语言模型往往在复杂任务中表现不佳，例如多步骤数学推理、逻辑题和常识推理。原因在于模型缺乏连贯的推理能力，容易直接生成幻觉性答案。\n",
                "CoT 通过让模型'逐步思考，显著减少了错误答案的生成，并让模型的推理过程更易解释。\n",
                "核心技术：\n",
                "Chain of Thought (CoT) Prompting：\n",
                "  利用提示词，模型生成一系列连贯的推理步骤，而不是直接输出答案。\n",
                "  示例：\n",
                "问题： 如果Sally 有 5 个苹果，Tom 给了她 3 个苹果，她现在有多少个？\n",
                "CoT 输出：\n",
                " Sally 一开始有 5 个苹果。Tom 给了她 3 个苹果，所以她现在有 5 + 3 = 8 个苹果。\n",
                "答案：8\n",
                "Self-Consistency：\n",
                "  使用随机采样生成多条推理路径（即多次生成 CoT）。\n",
                "  对生成的答案进行投票，选择出现频率最高的答案作为最终结果。\n",
                "  这种方法有效减少了错误推理路径对最终答案的影响。\n",
                "实验结果：\n",
                "  在数学推理和常识推理任务上，CoT 提高了 GPT-3 的准确率，尤其是 Self-Consistency 的加入进一步优化了模型性能。\n",
                "应用场景：\n",
                "数学推理： 提高数学题解答的准确性。\n",
                "逻辑推理： 解决需要多步骤推理的复杂问题。\n",
                "常识问答： 通过分步推理生成更合理的答案。"
        );
    }
}