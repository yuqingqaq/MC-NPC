//package api;
//// OpenAIGPT.java
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class OpenAIGPT {
//    private String apiKey;
//
//    public OpenAIGPT() throws IOException {
//        this.apiKey = new String(Files.readAllBytes(Paths.get("src/main/resources/config/api_keys.txt"))).trim();
//    }
//
//    public String getResponse(String prompt) {
//        String responseBody = null;
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//            HttpPost post = new HttpPost("https://api.openai.com/v1/completions");
//            post.setHeader("Authorization", "Bearer " + this.apiKey);
//            String json = "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 150}";
//            post.setEntity(new StringEntity(json));
//            responseBody = client.execute(post, httpResponse ->
//                    EntityUtils.toString(httpResponse.getEntity()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return responseBody;
//    }
//}
//
