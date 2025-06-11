package utils;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.AIPromt;
import entities.AIResponse;
import enums.Languages;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {
    public static <T> T jsonToObject(String json, Class<T> object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String objectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSecretValue(String secretName) {
        try {
            AWSSecretsManager manager = AWSSecretsManagerClientBuilder.standard().withRegion("eu-west-1").build();
            GetSecretValueRequest request = new GetSecretValueRequest().withSecretId(secretName);
            GetSecretValueResult result = manager.getSecretValue(request);
            return result.getSecretString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return "NULL";
        }
    }

    public static String translate(String text, Languages language) {
        String chatGPTURL = "https://api.openai.com/v1/chat/completions";
        String token = "Bearer " + System.getenv("API_TOKEN");
        AIPromt promt = new AIPromt();
        promt.setMessage(text + "Translate from ukrainian to " + language.name() + ". Keep html tags.");

        URI uri = URI.create(chatGPTURL);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(objectToJson(promt)))
                .build();
        try {
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String obj = response.body().toString();
            AIResponse aiResponse = jsonToObject(obj, AIResponse.class);

            return Objects.nonNull(aiResponse) ? aiResponse.getMessage() : text;
        } catch (IOException | InterruptedException e) {
            return text;
        }
    }
}
