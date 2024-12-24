package speech;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import util.JsonLoader;
import util.ResourcePathUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;


public class CallOpenAITTS {
    private String modelName;
    private List<String> keys;
    private Random random;
    private OkHttpClient client;
    private ObjectMapper mapper;
    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());

    public CallOpenAITTS(String modelName, String keysPath) {
        this.modelName = modelName;
        this.random = new Random();
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();

        //Extract API key
        try (InputStream is = ResourcePathUtil.getResourceAsStream(keysPath)) {
            if (is == null) {
                LOGGER.log(Level.SEVERE, "API keys file not found: " + keysPath);
                this.keys = Collections.emptyList();
                return;
            }

            // 使用 BufferedReader 来从 InputStream 读取
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                List<String> lines = reader.lines()
                        .map(String::trim)
                        .filter(line -> line.length() >= 4)
                        .collect(Collectors.toList());
                if (lines.isEmpty()) {
                    System.out.println("No valid API keys found in the file.");
                    this.keys = Collections.emptyList();
                } else {
                    this.keys = lines;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read API keys from file: " + e.getMessage(), e);
            this.keys = Collections.emptyList();
        }
    }

    public File call(String inputText, String voice) {
//        if (this.keys.isEmpty()) {
//            System.out.println("No API keys available.");
//            return null;
//        }
        try {
            String currentKey = this.keys.get(random.nextInt(this.keys.size()));

            String json = mapper.writeValueAsString(new TTSRequest(inputText, voice));
            RequestBody requestBody = RequestBody.create(
                    json,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://apix.ai-gaochao.cn/v1/audio/speech")
                    .header("Authorization", "Bearer " + currentKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    byte[] audioData = response.body().bytes();
                    // Save the audio data to a file
                    Path tempDir = Files.createTempDirectory("tts_tmp");
//                    tempDir.toFile().deleteOnExit(); // Optional: marks the directory for deletion on JVM exit
                    String fileName = "tts_" + UUID.randomUUID().toString() + ".mp3";
                    Path audioFilePath = tempDir.resolve(fileName);
                    File audioFile = audioFilePath.toFile();
                    try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                        fos.write(audioData);
                    }
                    System.out.println("Audio file created at: " + audioFilePath.toAbsolutePath());
                    System.out.println("Tmp Audio File:" + audioFile.getName());
                    // Return the audio file
                    return audioFile;

                } else {
                    String responseBodyStr = response.body().string();
                    System.out.println("Server returned error: " + response.code() + " " + response.message());
                    System.out.println("Response body: " + responseBodyStr);
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to generate speech from OpenAI: " + e.getMessage());
            return null;
        }
    }
    private static class TTSRequest {
        private String model;
        private String input;
        private String voice;

        public TTSRequest(String input, String voice) {
            this.model = "tts-1";
            this.input = input;
            this.voice = voice;
        }

        public String getModel() {
            return model;
        }

        public String getInput() {
            return input;
        }

        public String getVoice() {
            return voice;
        }
    }

}
