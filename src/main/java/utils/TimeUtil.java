package utils;

public class TimeUtil {
    public static long parseEstimatedTime(String estimatedTime) {
        try {
            String[] timeParts = estimatedTime.split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);
            return (minutes * 60L) + seconds; // 转换为总秒数
        } catch (Exception e) {
            return 600L; // 默认 10 分钟
        }
    }

    public static String formatTime(long totalSeconds) {
        if (totalSeconds < 0) {
            return "00:00";
        }
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}