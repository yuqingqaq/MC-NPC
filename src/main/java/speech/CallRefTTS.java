package speech;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallRefTTS {
    private String url;
    private String apiKey;
    private ObjectMapper msgPackMapper;
    private OkHttpClient client;
    private static final Logger LOGGER = Logger.getLogger(CallRefTTS.class.getName());

    public CallRefTTS(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.msgPackMapper = new ObjectMapper(new MessagePackFactory());
    }

    public File call(String text) {
        try {
            List<ServeReferenceAudio> references = new ArrayList<>();
//            String referencePath = "audio/demo2.mp3";
//            File refFile = new File(referencePath);
//            if (!refFile.exists()) {
//                LOGGER.log(Level.WARNING, "Reference audio file does not exist: " + referencePath);
//            }
//
//            references.add(new ServeReferenceAudio(referencePath, "Associated reference text"));

            ServeTTSRequest request = new ServeTTSRequest();
            request.setText(text);
            request.setChunkLength(200);
            request.setFormat("mp3");
            request.setMp3Bitrate(64);
            request.setReferences(new ArrayList<>());
            request.setReferenceId(null);
            request.setNormalize(true);
            request.setOpusBitrate(-1000);
            request.setLatency("normal");
            request.setStreaming(false);
            request.setEmotion(null); // 如果有情感参数，需要设置具体的情感对象
            request.setMaxNewTokens(1024);
            request.setTopP(0.7);
            request.setRepetitionPenalty(1.2);
            request.setTemperature(0.7);

            byte[] requestBodyBytes = msgPackMapper.writeValueAsBytes(request);
            RequestBody requestBody = RequestBody.create(requestBodyBytes, MediaType.parse("application/msgpack"));
            Request httpRequest = new Request.Builder()
                    .url(this.url)
                    .header("Authorization", "Bearer " + this.apiKey)
                    .header("Content-Type", "application/msgpack")
                    .post(requestBody)
                    .build();

            Response response = null;

            try {
                response = client.newCall(httpRequest).execute();

                if (!response.isSuccessful()) {
                    LOGGER.log(Level.SEVERE, "Failed to call TTS API. Code: " + response.code() + ", Message: " + response.message());
                    return null;
                }

                byte[] audioData = response.body().bytes();
                Path tempDir = Files.createTempDirectory("tts_tmp");
                String fileName = "tts_response_" + UUID.randomUUID().toString() + ".mp3";
                Path audioFilePath = tempDir.resolve(fileName);
                File audioFile = audioFilePath.toFile();
                System.out.println("Audio file created at: " + audioFilePath.toAbsolutePath());
                System.out.println("Tmp Audio File:" + audioFile.getName());

                try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                    fos.write(audioData);
                }

                LOGGER.log(Level.INFO, "Audio file created at: " + audioFilePath.toAbsolutePath());
                return audioFile;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error handling the HTTP response or writing the audio file", e);
                return null;
            } finally {
                if (response != null) {
                    response.close();  // Make sure to close the response to avoid resource leaks
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in generating speech: " + e.getMessage(), e);
            return null;
        }
    }

    class ServeReferenceAudio {
        private String audio;
        private String text;

        public ServeReferenceAudio(String audio, String text) {
            this.audio = audio;
            this.text = text;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private static class ServeTTSRequest {
        private String text;
        private int chunkLength;
        private String format;
        private int mp3Bitrate;
        private List<ServeReferenceAudio> references;
        private String referenceId;
        private boolean normalize;
        private int opusBitrate;
        private String latency;
        private boolean streaming;
        private String emotion;
        private int maxNewTokens;
        private double topP;
        private double repetitionPenalty;
        private double temperature;

        // Constructor
        public ServeTTSRequest() {
        }

        // Getters and Setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getChunkLength() {
            return chunkLength;
        }

        public void setChunkLength(int chunkLength) {
            this.chunkLength = chunkLength;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getMp3Bitrate() {
            return mp3Bitrate;
        }

        public void setMp3Bitrate(int mp3Bitrate) {
            this.mp3Bitrate = mp3Bitrate;
        }

        public List<ServeReferenceAudio> getReferences() {
            return references;
        }

        public void setReferences(List<ServeReferenceAudio> references) {
            this.references = references;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public boolean isNormalize() {
            return normalize;
        }

        public void setNormalize(boolean normalize) {
            this.normalize = normalize;
        }

        public int getOpusBitrate() {
            return opusBitrate;
        }

        public void setOpusBitrate(int opusBitrate) {
            this.opusBitrate = opusBitrate;
        }

        public String getLatency() {
            return latency;
        }

        public void setLatency(String latency) {
            this.latency = latency;
        }

        public boolean isStreaming() {
            return streaming;
        }

        public void setStreaming(boolean streaming) {
            this.streaming = streaming;
        }

        public String getEmotion() {
            return emotion;
        }

        public void setEmotion(String emotion) {
            this.emotion = emotion;
        }

        public int getMaxNewTokens() {
            return maxNewTokens;
        }

        public void setMaxNewTokens(int maxNewTokens) {
            this.maxNewTokens = maxNewTokens;
        }

        public double getTopP() {
            return topP;
        }

        public void setTopP(double topP) {
            this.topP = topP;
        }

        public double getRepetitionPenalty() {
            return repetitionPenalty;
        }

        public void setRepetitionPenalty(double repetitionPenalty) {
            this.repetitionPenalty = repetitionPenalty;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
    }


}