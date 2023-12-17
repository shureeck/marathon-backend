package utils;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
