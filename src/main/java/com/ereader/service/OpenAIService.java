package com.ereader.service;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class OpenAIService {

    public static OpenAIService INSTANCE = new OpenAIService();
    private static final String API_KEY = "your-openai-api-key";
    private static final String MODEL_NAME= "text-davinci-003";

    private static final String ENDPOINT = "https://api.openai.com/v1/completions";
    private static final HttpClient client = HttpClient.newHttpClient();

    public String translate(String text){
        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL_NAME);
        body.put("prompt", "Translate the following text to Chinese: " + text);
        body.put("max_tokens", 5 * text.length());

        Gson gson = new Gson();
        String jsonRequestBody = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("Response: " + responseBody);

            // 从 JSON 响应中提取翻译内容
            String translatedText = extractTranslation(responseBody);
            System.out.println("Translated Text: " + translatedText);
            return translatedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String extractTranslation(String responseBody) {
        Gson gson = new Gson();
        Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);
        Map<String, Object> choice = (Map<String, Object>) ((java.util.List) responseMap.get("choices")).get(0);
        return (String) choice.get("text");
    }
}
