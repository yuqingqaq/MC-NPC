package prompt;

import java.util.HashMap;
import java.util.Map;

public class TaskPrompts {

    public enum TaskStage {
        ADMIN, TA, TB, TC, TD, DAOYUAN, CONFERENCE, LIBRARY, GYM, END
    }

    private static final Map<TaskStage, String[]> prompts = new HashMap<>();

    static {
        prompts.put(TaskStage.ADMIN, new String[] {
                "序幕：风启新程",
                "你站在行政楼的大堂中央，周围是宽敞明亮的空间，校长徐扬生正微笑着欢迎你。" +
                        "他告诉你，校园的每一个角落都藏着故事，而你的任务是去探索这些故事，收集散落的校徽碎片。" +
                        "现在，迈出你的第一步，去发现这段旅程的意义吧！"
        });

        prompts.put(TaskStage.TA, new String[] {
                "起点：智慧的足迹",
                "现在你来到TA教学楼，经管学院的院长熊伟正在门口欢迎你。" +
                        "他向你介绍学院的丰富资源和无限可能，鼓励你以智慧和视野开启新的学习旅程。" +
                        "走进教学楼，感受知识的氛围，开始你的探索吧！"
        });

        prompts.put(TaskStage.TB, new String[] {
                "探索：人文的魅力",
                "你来到TB教学楼，这里充满了浓厚的人文气息。人文社科学院的院长唐文方正微笑着向你招手。" +
                        "他邀请你了解学院在人文研究领域的深厚积淀，并希望你能在这里发现人文的魅力。" +
                        "走进教学楼，感受思想的碰撞，开启一场人文的探索之旅吧！"
        });

        prompts.put(TaskStage.TC, new String[] {
                "责任：生命的守护",
                "现在你站在TC教学楼前，医学院的院长郑仲煊正在等待你的到来。" +
                        "他讲述了医学教育的责任与担当，并邀请你走进学院，感受生命科学的魅力。" +
                        "走进教学楼，开启一场关乎生命与智慧的探索吧！"
        });

        prompts.put(TaskStage.TD, new String[] {
                "创新：科技的未来",
                "你来到TD教学楼，理工学院的院长唐本忠正热情地向你介绍学院的创新成就。" +
                        "他希望你能把握科技发展的脉搏，走进学院，探索属于未来的科学技术。" +
                        "走进教学楼，感受创新的力量，发现属于你的未来吧！"
        });

        prompts.put(TaskStage.DAOYUAN, new String[] {
                "洞察：数据的力量",
                "现在你来到道远楼，数据科学学院的院长戴建岗正在向你展示数据科学的魅力。" +
                        "他强调了数据在现代社会中的重要性，希望你能走进学院，感受数据驱动的世界。" +
                        "迈入道远楼，开启一场关于洞察和智慧的探索吧！"
        });

        prompts.put(TaskStage.CONFERENCE, new String[] {
                "艺术：旋律的律动",
                "你来到逸夫国际会议中心，音乐学院的院长叶小钢正邀请你走入音乐的世界。" +
                        "他希望你能通过艺术找到属于自己的节奏与和谐，感受音乐的无限可能。" +
                        "走进会议中心，让艺术的旋律引领你的探索吧！"
        });

        prompts.put(TaskStage.LIBRARY, new String[] {
                "求知：智慧的殿堂",
                "现在你来到图书馆，这里是智慧的象征，书香弥漫，环境优雅。" +
                        "工作人员向你介绍了图书馆的丰富资源，并邀请你走进书海，感受知识的力量。" +
                        "进入图书馆，开启一场求知的旅程吧！"
        });

        prompts.put(TaskStage.GYM, new String[] {
                "活力：勇气的挑战",
                "你来到体育馆，这里充满了活力与激情。" +
                        "工作人员热情地欢迎你，并介绍了健身房、篮球场和游泳池等设施。" +
                        "走进体育馆，释放你的能量，迎接属于你的挑战吧！"
        });

        prompts.put(TaskStage.END, new String[] {
                "初心相承",
                "你回到行政楼，校长徐扬生正微笑着等待你的归来。" +
                        "他为你祝贺完成了这场探索之旅，并告诉你，这是一段新的开始。" +
                        "带上你的收获和勇气，开启属于你的大学篇章吧！"
        });
    }

    public static String getTitle(TaskStage stage) {
        return prompts.get(stage)[0];
    }

    public static String getContent(TaskStage stage) {
        return prompts.get(stage)[1];
    }
}