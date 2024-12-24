package speech;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.InputStream;

public class AudioPlayer {
    private Player player;
    private Thread playerThread;

    public void playAudio(String filename) {
        try {
            System.out.println("Try playing audio file: " + filename);
            InputStream fileInputStream = new FileInputStream(filename);
            player = new Player(fileInputStream);
            playerThread = new Thread(() -> {
                try {
                    player.play();
                    System.out.println("Audio playback started.");
                } catch (Exception e) {
                    System.out.println("Error playing audio file: " + e.getMessage());
                }
            });
            playerThread.start();
        } catch (Exception e) {
            System.out.println("Error setting up audio file: " + e.getMessage());
        }
    }

    public void stopAudio() {
        if (player != null) {
            player.close();
            playerThread.interrupt();
            //System.out.println("Audio playback stopped.");
        }
    }
}