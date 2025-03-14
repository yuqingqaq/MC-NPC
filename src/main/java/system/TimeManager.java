package system;

import model.AdaptiveSubTaskModel;
import utils.TimeUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TimeManager {
    private static TimeManager instance;
    private Timer timer;

    private TimeManager() {
        timer = new Timer(true);
    }

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    // 开启子任务的时间更新逻辑
    public void startTrackingTime(AdaptiveSubTaskModel subTask) {
        if (subTask.getStatus() == AdaptiveSubTaskModel.TaskStatus.IN_PROGRESS) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (subTask.getRemainingTime() > 0) {
                        subTask.reduceRemainingTime(1); // 每秒减少 1 秒
                    } else {
                        subTask.setStatus(AdaptiveSubTaskModel.TaskStatus.COMPLETED); // 时间耗尽后标记为完成
                        this.cancel(); // 停止计时
                    }
                }
            }, 0, 1000); // 每秒执行一次
        }
    }

    // 停止对子任务的时间追踪
    public void stopTrackingTime(AdaptiveSubTaskModel subTask) {
        subTask.setStatus(AdaptiveSubTaskModel.TaskStatus.COMPLETED);
    }

    // 格式化时间
    public String formatTime(long totalSeconds) {
        return TimeUtil.formatTime(totalSeconds);
    }
}