package prompt;

import java.util.HashMap;
import java.util.Map;

public class TaskPrompts {

    private static boolean introCompleted = false;

    public enum TaskStage {
        INTRO, ADMIN, TA, TB, TC, TD, DAOYUAN, CONFERENCE, LIBRARY, GYM, END
    }

    private static final Map<TaskStage, String[]> prompts = new HashMap<>();

    static {
        prompts.put(TaskStage.INTRO, new String[] {
                "序幕：风启新程",
                "你站在校园的入口，面前是一片充满生机的校园景象。" +
                        "这是一个全新的世界，承载着智慧与梦想。整理好你的思绪，准备开始一场非凡的探索之旅吧！",
        });

        prompts.put(TaskStage.ADMIN, new String[] {
                "前往：智慧的起点",
                "校长徐扬生正在行政楼等候你的到来。他站在宽敞明亮的大堂中央，准备为你讲述校园的历史与精神。" +
                        "踏上台阶，推开行政楼的大门，去见他吧！",
                "第一块校徽碎片已找到！这是一段象征着起点的旅程。校徽碎片不仅连接着校园的每一个角落，" +
                        "也藏着学校的精神与故事。继续探索吧，剩下的碎片正等着你。"
        });

        prompts.put(TaskStage.TA, new String[] {
                "前往：智慧的足迹",
                "经管学院位于TA教学楼，那里是一座充满智慧与活力的学术殿堂。" +
                        "院长熊伟正在教学楼门口等着与你会面。他希望你能感受这里的学术氛围，快去TA教学楼吧！",
                "第二块校徽碎片已找到！你正在逐步拼凑出港中深的精神象征。继续前进吧，还有更多故事等待你去发现。"
        });

        prompts.put(TaskStage.TB, new String[] {
                "前往：人文的魅力",
                "人文社科学院位于TB教学楼，这里充满了浓厚的人文气息。" +
                        "院长唐文方期待与你见面，与你分享学院独特的人文故事。" +
                        "走向TB教学楼，感受思想的深度与文化的魅力吧！",
                "第三块校徽碎片已找到！讲台是知识传递的起点，文学从这里启航。继续前进吧，更多的故事等待你去发现。"
        });

        prompts.put(TaskStage.TC, new String[] {
                "前往：生命的守护",
                "医学院位于TC教学楼，那里是一片关乎生命与智慧的学术领地。" +
                        "院长郑仲煊正等待你的到来，他将为你展示医学的责任与科学的力量。" +
                        "迈向TC教学楼，去感受生命科学的魅力吧！",
                "你找到了一块校徽碎片！医学的核心是连接理论与实践、关怀与责任。继续探索吧，下一块碎片正在等待你的发现！"
        });

        prompts.put(TaskStage.TD, new String[] {
                "前往：科技的未来",
                "TD教学楼是理工学院的所在地，这里是创新与实践的集中地。" +
                        "院长唐本忠在教学楼前等着你，他希望带你了解学院在科技领域的成就。" +
                        "朝着TD教学楼出发，去发现科技的无限可能吧！",
                "你找到了一块校徽碎片！科技的力量源自探索与创新，理工学院为你的每一步成长提供了支撑。继续前进吧，新的探索正等待着你，"
        });

        prompts.put(TaskStage.DAOYUAN, new String[] {
                "前往：数据的力量",
                "道远楼是数据科学学院的所在地，这里是数据与智慧交织的地方。" +
                        "院长戴建岗正在大堂等你，他将带你了解学院的前沿研究与独特资源。" +
                        "走向道远楼，探索数据的强大力量吧！",
                "你找到了一块校徽碎片！数据科学的价值在于将复杂的世界转化为可解读的信息，" +
                        "而你的探索就是一场数据与现实的结合之旅。继续前进吧，更多的碎片等待着你！"
        });

        prompts.put(TaskStage.CONFERENCE, new String[] {
                "前往：旋律的律动",
                "逸夫国际会议中心是音乐学院的所在地，这里充满了艺术的氛围。" +
                        "院长叶小钢正在等着你，他将带你走入音乐的世界，感受旋律的魅力。" +
                        "前往会议中心，去体验艺术的无限可能吧！",
                "你成功获得了校徽碎片！音乐的意义在于突破界限，连接世界，而你的探索旅程正如一段交响曲，" +
                        "充满了未知和美好。继续前进，新的线索在等着你！"
        });

        prompts.put(TaskStage.LIBRARY, new String[] {
                "前往：智慧的殿堂",
                "图书馆是校园内的知识宝库，书香四溢，安静而充满力量。" +
                        "工作人员正等待你的到来，他们将为你介绍馆内的资源与特色。" +
                        "走向图书馆，去开启一段智慧的探索之旅吧！",
                "你找到了图书馆的校徽碎片！这里的每一本书都等待着被打开，释放出它们的智慧与故事。继续前行吧，去到最后一站!"
        });

        prompts.put(TaskStage.GYM, new String[] {
                "前往：勇气的挑战",
                "体育馆是一片充满活力的场所，运动的激情在这里挥洒。" +
                        "工作人员正热情地等待你的到来，他们将带你体验运动的乐趣与挑战。" +
                        "朝着体育馆迈进，释放你的能量吧！",
                "你成功找到了泳池底下的校徽碎片！这不仅是一次水下探索，更是一场自我挑战的旅程。带着这份勇气继续前行吧。"
        });

        prompts.put(TaskStage.END, new String[] {
                "终章：初心相承",
                "校长徐扬生正在行政楼的大堂等候你的归来。他将为你总结这段非凡的旅程。" +
                        "回到行政楼，与校长见面，完成你的探索吧！",
                "掌心的校徽温暖而有分量，像是一颗星辰，点亮了你的前路。从这一刻开始，新的篇章，已悄然展开。"
        });
    }

    public static String getTitle(TaskStage stage) {
        return prompts.get(stage)[0];
    }

    public static String getContent(TaskStage stage) {
        return prompts.get(stage)[1];
    }

    public static String getOutCome(TaskStage stage) {
        return prompts.get(stage)[2];
    }

    // 检查是否完成 INTRO 阶段
    public static boolean isIntroCompleted() {
        return introCompleted;
    }

    // 标记 INTRO 阶段为完成
    public static void setIntroCompleted(boolean completed) {
        introCompleted = completed;
    }
}