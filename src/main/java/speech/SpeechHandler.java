package speech;

import javax.sound.sampled.*;
import java.io.*;

public class SpeechHandler {
    private boolean isRecording = false;
    private ByteArrayOutputStream out;
    private TargetDataLine line;

    public void startRecording() throws LineUnavailableException {
        isRecording = true;
        System.out.println("Recording started");

        try {
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            System.out.println("Microphone line opened and started");

            Thread thread = new Thread(() -> {
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                try {
                    while (isRecording) {
                        int count = line.read(buffer, 0, buffer.length);
                        if (count > 0) {
                            out.write(buffer, 0, count);
                        }
                    }
                    out.close();
                    System.out.println("Audio data captured and stream closed");

                } catch (Exception e) {
                    System.out.println("Recording error: " + e.getMessage());
                }
            });
            thread.start();
        } catch (Exception e) {
            System.out.println("Microphone not accessible: " + e.getMessage());
        }
    }

    public String stopRecording() throws IOException {
        isRecording = false;
        String text = "";
        try {
            Thread.sleep(100); // 等待线程可能需要的处理时间

            if (line != null) {
                line.stop();
                line.close();
                System.out.println("Microphone line closed");
            }

            byte[] audioData = out.toByteArray();
            System.out.println("Audio data size: " + audioData.length + " bytes");

            // Create a temporary WAV file
            File wavFile = createWavFile(audioData);

            if (wavFile != null) {
                System.out.println("Audio recorded and stored in " + wavFile.getAbsolutePath());

                try {
                    // Pass the WAV file to speech recognition
                    text = SpeechToTextService.CallWhisper(wavFile);
                    System.out.println("Recognized text: " + text);

                    // Optional: Delete the temporary file after processing
                    wavFile.delete();
                } catch (Exception e) {
                    text = "ASR错误: " + e.getMessage();
                    System.out.println("Error calling recognizeAudio: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            text = "Speech recognition error: " + e.getMessage();
            System.out.println("Speech recognition error: " + e.getMessage());
        }
        
        return text;
    }

    private File createWavFile(byte[] audioData) {
        try {
            // Define the audio format used in startRecording()
            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);

            // Create a temporary file
            File tempFile = File.createTempFile("recording", ".wav");
            tempFile.deleteOnExit(); // Ensure file is deleted when JVM exits

            // Create audio input stream from byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

            // Write to WAV file
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, tempFile);

            return tempFile;
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error creating WAV file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}