package clinic;

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

public class huatuoAPI {
    private String modelName;
    private Random random;
    private OkHttpClient client;
    private ObjectMapper mapper;
    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());

    public huatuoAPI(String modelName){
        this.modelName = modelName;
        this.random = new Random();
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();

    }

    private String postProcess(String responseJson) throws IOException {
        ResponseData response = mapper.readValue(responseJson, ResponseData.class);
        return response.getChoices().get(0).getMessage().getContent();
    }

    public String call(List<NPCMessage> npcMessageHistory) {
        try {
            List<Message> messageHistory = npcMessageHistory.stream()
                    .map(Message::fromNPCMessage)
                    .collect(Collectors.toList());

            String json = mapper.writeValueAsString(new RequestData(this.modelName, messageHistory, 0.6, 0.8, 0.6, 0.8, 1));
            RequestBody body = RequestBody.create(json, okhttp3.MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://61.241.103.33:32001/v1/chat/" + "/completions")
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();
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
