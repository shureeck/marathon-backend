package entities;

import lombok.Getter;
import utils.Utils;

import java.util.HashMap;
import java.util.Objects;
public class Secret {
    private static Secret secret = null;
    private HashMap<String, Object> secretValue;
    @Getter
    private String secretName = "postgers-secret-credentials";
    private boolean valid = false;

    private Secret() {

            System.out.println("Get secret:" + secretName);
            String secretString = Utils.getSecretValue(secretName);
            System.out.println(secretString);
            secretValue = Utils.jsonToObject(secretString, HashMap.class);
            valid = true;

    }

    public static Secret getInstance() {
        if (Objects.isNull(secret)) {
            secret = new Secret();
        }
        return secret;
    }

    public String getValue(String key) {
        System.out.println("Get secret value: " + key);
        if (Objects.isNull(secretValue)) {
            System.out.println("Map is NULL");
            return null;
        } else {
            System.out.println(secretValue.get(key));
            Object value = secretValue.get(key);
            return String.valueOf(value);
        }
    }

    public boolean isValid() {
        return valid;
    }
}
