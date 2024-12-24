package speech;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallWhisper {
    private String modelName;
    private List<String> keys;
    private Random random;
    private OkHttpClient client;
    private ObjectMapper mapper;
    private static final Logger LOGGER = Logger.getLogger(CallWhisper.class.getName());

    public CallWhisper() {
        this.modelName = modelName;
        this.keys = keys;
        this.random = new Random();
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();

        if (keys == null || keys.isEmpty()) {
            LOGGER.log(Level.INFO, "No API keys provided.");
        }
    }

    private String postProcess(String responseJson) throws IOException {
        // 解析 JSON 根节点
        JsonNode rootNode = mapper.readTree(responseJson);
        // 访问 "result" 数组的第一个元素
        JsonNode firstResult = rootNode.path("result").get(0);
        if (firstResult != null) {
            // 获取 "text" 字段的文本值
            JsonNode textNode = firstResult.get("text");
            if (textNode != null) {
                return textNode.asText();
            } else {
                throw new IOException("The 'text' field is missing in the JSON response.");
            }
        } else {
            throw new IOException("The 'result' array is empty or missing in the JSON response.");
        }
    }

    public String call(File audioFile) {
        try {
            RequestBody fileBody = RequestBody.create(
                    audioFile,
                    MediaType.parse("audio/wav")
            );

            // 确保使用正确的字段名和部分
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("files", audioFile.getName(), fileBody)
                    .addFormDataPart("keys", "audio.wav")
                    .addFormDataPart("lang", "auto")
                    .build();

            // 构建 POST 请求
            Request request = new Request.Builder()
                    .url("http://10.27.127.33:50001/api/v1/asr")
                    .header("Authorization", "Bearer " )
                    .post(requestBody)
                    .build();

            // 执行请求并处理响应
            try (Response response = client.newCall(request).execute()) {
                String responseBodyStr = response.body().string();
                if (response.isSuccessful()) {
                    return postProcess(responseBodyStr);
                } else {
                    System.out.println("Server returned error: " + response.code() + " " + response.message());
                    return "Server error: " + response.message() + " with body: " + responseBodyStr;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to generate response from OpenAI: " + e.getMessage());
            return "Failed to generate response.";
        }
    }

    private String getApiKey() {
        // 实现从您的配置或存储中检索 API 密钥
        return this.keys.get(0); // 示例假设使用列表中的第一个密钥
    }
}