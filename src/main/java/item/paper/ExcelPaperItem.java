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
            "摘要:\n",
            "最近，研究人员对将大型语言模型（LLM）应用于决策任务的兴趣激增，这得益于LLM中嵌入的广泛世界知识。虽然越来越需要为定制决策任务调整LLM，但为特定任务微调它们需要大量资源，并可能削弱模型的泛化能力。此外，像GPT-4和Claude这样的最先进语言模型主要通过API调用访问，其参数权重仍然是专有的，无法公开获取。这种情况强调了开发新方法的迫切需求，这些方法允许从智能体经验中学习，而无需参数更新。\n",
            "为了解决这些问题，我们引入了经验学习（ExpeL）智能体。我们的智能体自主收集经验，并使用自然语言从一系列训练任务中提取知识。在推理时，智能体回忆其提取的见解和过去的经验，以做出明智的决策。我们的实验证明了ExpeL智能体的强大学习效果，表明随着经验的积累，其性能持续提升。我们还通过定性观察和额外实验探索了ExpeL智能体的新兴能力和迁移学习潜力。\n",
            "\n方法:\n",
            "ExpeL框架由三个主要组件组成：(1) 经验收集，智能体与任务交互并存储成功和失败的尝试；(2) 知识提取，智能体分析其经验以形成可推广的见解；(3) 经验回忆，智能体利用过去的经验和提取的知识解决新任务。\n",
            "\n结果:\n",
            "我们的实验表明，ExpeL智能体在各种环境中表现显著优于标准提示方法。智能体的性能随着经验的增加而提高，并展示了跨相似任务领域的知识迁移能力。定性分析显示，智能体通过其经验发展出战略思维模式，并学会避免常见陷阱。\n",
            "\n结论:\n",
            "ExpeL框架提供了一种无需参数更新即可增强LLM智能体能力的有前途的方法，纯粹通过自然语言实现经验学习。这种方法为开发能够通过与环境交互不断改进的自适应智能体开辟了新的可能性。"
        );
    }
}