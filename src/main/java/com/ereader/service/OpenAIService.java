package com.ereader.service;

import com.ereader.config.Constants;
import com.ereader.model.ApiConfig;
import com.ereader.model.api.Choose;
import com.ereader.model.api.Response;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class OpenAIService {

    public static OpenAIService INSTANCE = new OpenAIService();
    private static final String API_KEY = "your-openai-api-key";
    private static final String MODEL_NAME= "text-davinci-003";

    private static final String ENDPOINT = "https://api.openai.com/v1/completions";
    private static final HttpClient client = HttpClient.newHttpClient();
    private ApiConfig apiConfig = null;

    public String translate(String text){
        if(Objects.isNull(apiConfig)){
            apiConfig = loadConfig();
        }


        Map<String, String> body = new HashMap<>();

        body.put("modelName", apiConfig.getModel());
        body.put("systemPrompt", apiConfig.getTemplate());
        body.put("userPrompt", text);

        String template = """
                {
                   "model": "{{modelName}}",
                   "messages": [
                     {"role": "system", "content": "{{systemPrompt}}"},
                     {"role": "user", "content": "{{userPrompt}}"}
                   ],
                   "temperature": 0.3
                }
                """;
        for (Map.Entry<String, String> entry : body.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }


        String jsonRequestBody = template;
        Gson gson = new Gson();
        log.info("config={},req={}",gson.toJson(apiConfig),jsonRequestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiConfig.getEndpoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiConfig.getKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            log.info("Response: " + responseBody);

            return extractTranslation(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractTranslation(String responseBody) {
        Gson gson = new Gson();
        Response response = gson.fromJson(responseBody, Response.class);
        Choose choose = response.getChoices().get(0);
        return choose.getMessage().getContent();
    }


    public ApiConfig loadConfig(){
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(Constants.CONFIG_PATH)) {
            props.load(in);
        } catch (Exception e) {
            log.error("load config error",e);
        }
        ApiConfig config = new ApiConfig();
        config.setEndpoint((String) props.get("api.endpoint"));
        config.setKey((String) props.get("api.key"));
        config.setTemplate((String) props.get("api.template"));
        config.setModel((String) props.get("api.model"));
        return config;
    }
}
