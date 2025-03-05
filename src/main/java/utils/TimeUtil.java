package utils;

public class TimeUtil {
    public static long parseEstimatedTime(String estimatedTime) {
        try {
            String[] timeParts = estimatedTime.split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);
            return (minutes * 60L) + seconds; // 转换为总秒数，使用 60L 避免可能的整数溢出
        } catch (Exception e) {
            // 如果解析失败，返回默认值（比如 10 分钟）
            return 600L;
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

    public static String formatLongTime(long totalMinutes) {
        return totalMinutes + " min";
    }
}