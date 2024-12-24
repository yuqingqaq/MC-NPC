package speech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import util.JsonLoader;
import util.ResourcePathUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;



public class CallOpenAIWhisper {
    private String modelName;
    private List<String> keys;
    private Random random;
    private OkHttpClient client;
    private ObjectMapper mapper;
    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());

    public CallOpenAIWhisper(String modelName, String keysPath) {
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

    private String postProcess(String responseJson) throws IOException {
        // The response JSON is in the format of {"text": "..."}
        JsonNode rootNode = mapper.readTree(responseJson);
        String text = rootNode.get("text").asText();
        return text;
    }

    public String call(File audioFile) {
        if (this.keys.isEmpty()) {
            System.out.println("No API keys available.");
            return "Error: API key not available.";
        }
        try {
            String currentKey = this.keys.get(random.nextInt(this.keys.size()));

            // Build the multipart form request body
            RequestBody fileBody = RequestBody.create(
                    audioFile,
                    MediaType.parse("audio/wav")
            );

            // Build the multipart request body with the file and model name
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", audioFile.getName(), fileBody)
                    .addFormDataPart("model", "whisper-1")
                    .build();

            // Build the POST request to the transcription endpoint
            Request request = new Request.Builder()
                    .url("https://apix.ai-gaochao.cn/v1/audio/transcriptions")
                    .header("Authorization", "Bearer " + currentKey)
                    .header("Content-Type", "multipart/form-data")
                    .post(requestBody)
                    .build();

            // Execute the request and handle the response
            try (Response response = client.newCall(request).execute()) {
                String responseBodyStr = response.body().string();
                if (response.isSuccessful()) {
                    return postProcess(responseBodyStr);
                } else {
                    System.out.println("Server returned error: " + response.code() + " " + response.message());
                    return "Server error: " + response.message() + " with body: " + responseBodyStr;
                }
            } catch (JsonProcessingException e) {
                System.out.println("JSON processing error: " + e.getMessage());
                return "JSON processing error: " + e.getMessage();
            }
        } catch (IOException e) {
            System.out.println("Failed to generate response from OpenAI: " + e.getMessage());
            return "Failed to generate response.";
        }
    }
}
