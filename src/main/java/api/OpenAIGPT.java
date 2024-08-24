package api;

import api.metadata.Message;
import api.metadata.RequestData;
import api.metadata.ResponseData;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import metadata.NPCMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.JsonLoader;
import util.ResourcePathUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenAIGPT {
    private String modelName;
    private List<String> keys;
    private Random random;
    private OkHttpClient client;
    private ObjectMapper mapper;
    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());

    public OpenAIGPT(String modelName, String keysPath) {
        this.modelName = modelName;
        this.random = new Random();
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();

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
                    System.err.println("No valid API keys found in the file.");
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
        ResponseData response = mapper.readValue(responseJson, ResponseData.class);
        return response.getChoices().get(0).getMessage().getContent();
    }

    public String call(List<NPCMessage> npcMessageHistory) {
        if (this.keys.isEmpty()) {
            System.err.println("No API keys available.");
            return "Error: API key not available.";
        }
        try {
            List<Message> messageHistory = npcMessageHistory.stream()
                    .map(Message::fromNPCMessage)
                    .collect(Collectors.toList());

            String currentKey = this.keys.get(random.nextInt(this.keys.size()));
            String json = mapper.writeValueAsString(new RequestData(this.modelName, messageHistory, 0.6, 0.8, 0.6, 0.8, 1));
            RequestBody body = RequestBody.create(json, okhttp3.MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    //.url("https://api.openai.com/v1/chat/" + "/completions")
                    .url("http://61.241.103.33:8900/v1/chat/completions")
                    .header("Authorization", "Bearer " + currentKey)
                    .header("Content-Type", "application/json")
//                    .header("Accept", "application/json")
//                    .header("Connection", "keep-alive")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String responseBodyStr = response.body().string();
                if (response.isSuccessful()) {
                    return postProcess(responseBodyStr);
                } else {
                    System.err.println("Server returned error: " + response.code() + " " + response.message());
                    return "Server error: " + response.message() + " with body: " + responseBodyStr;
                }
            } catch (JsonProcessingException e) {
                System.err.println("JSON processing error: " + e.getMessage());
                return "JSON processing error: " + e.getMessage();
            }
        } catch (IOException e) {
            System.err.println("Failed to generate response from OpenAI: " + e.getMessage());
            return "Failed to generate response.";
        }
    }

}