package logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UserActivityLogger {
    private static UserActivityLogger instance;
    private final String LOG_FILE_PATH = "logs/user_activity.log";
    private final BlockingQueue<String> logQueue;
    private final Thread loggerThread;
    private volatile boolean isRunning;
    
    private UserActivityLogger() {
        this.logQueue = new LinkedBlockingQueue<>();
        this.isRunning = true;
        this.loggerThread = new Thread(this::processLogQueue);
        this.loggerThread.start();
        
        // 创建日志目录
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }
    
    public static UserActivityLogger getInstance() {
        if (instance == null) {
            instance = new UserActivityLogger();
        }
        return instance;
    }
    
    public void logActivity(String playerName, String activityType, String details) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String logEntry = String.format("[%s] Player: %s | Activity: %s | Details: %s", 
                                      timestamp, playerName, activityType, details);
        logQueue.offer(logEntry);
    }
    
    private void processLogQueue() {
        while (isRunning) {
            try {
                String logEntry = logQueue.take();
                writeToFile(logEntry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void writeToFile(String logEntry) {
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(logEntry);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void shutdown() {
        isRunning = false;
        loggerThread.interrupt();
    }
}