package prompt;

import java.util.HashMap;
import java.util.Map;

public class TimeBasedPrompts {
    public enum TimePeriod {
        MORNING, NOON, AFTERNOON, EVENING
    }

    private static final Map<TimePeriod, String[]> prompts = new HashMap<>();

    static {
        prompts.put(TimePeriod.MORNING, new String[] {
            "早安！", 
            "早晨的图书馆静谧适宜，沉浸在书海中开始新的一天吧！你今天还有课程在TA101教室，准备好迎接知识的洗礼了么？又或者，可以到礼文堂听一场令人振奋的演讲，开阔视野！"
        });
        prompts.put(TimePeriod.NOON, new String[] {
            "午间时光", 
            "中午来临，荷花池畔正是散步的好地方，享受片刻的宁静吧！饿了么？快乐食间等你来挑选美味的午餐，补充能量，准备下午的挑战！"
        });
        prompts.put(TimePeriod.AFTERNOON, new String[] {
            "精彩下午", 
            "下午时分，是不是该到综合体育馆释放活力，挥洒汗水了？如果喜欢艺术，TC101 SPACE的艺术展览定会让你眼前一亮。科研楼的实验室也是求知者的天堂，继续你的科研探索吧！"
        });
        prompts.put(TimePeriod.EVENING, new String[] {
            "夜晚的安排", 
            "晚上好！回到逸夫书院，这里不仅是你舒适的宿舍，也是满载一天学习成果的港湾。或者，去大学体育馆参加一场晚间运动，完美结束这充实的一天。"
        });
    }

    public static String getTitle(TimePeriod period) {
        return prompts.get(period)[0];
    }

    public static String getContent(TimePeriod period) {
        return prompts.get(period)[1];
    }
}