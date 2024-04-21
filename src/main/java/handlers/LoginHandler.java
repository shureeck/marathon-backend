package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dao.Dao;
import dao.UsersDao;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import utils.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class LoginHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler:LoginHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);

        switch (method) {
            case "POST":
                HashMap<String, String> credentials = Utils.jsonToObject((String) request.get("body"), HashMap.class);
                Dao dao = UsersDao.builder()
                        .userName(credentials.get("username"))
                        .password(credentials.get("password"))
                        .build();
                String userData = dao.get();
                if (Objects.isNull(userData)) {
                    return response.withStatusCode(401)
                            .withBody("Authorization failed. Username or password is incorrect");
                }
                logger.log("User data:" + userData);
                HashMap<String, String> tokenObject = (HashMap<String, String>) Utils.jsonToObject(userData, List.class).get(0);
                logger.log("Object:" + tokenObject);
                tokenObject.put("expiration", new DateTime().plusHours(4).toString());
                String token = JWT.create()
                        .withPayload(Utils.objectToJson(tokenObject))
                        .sign(Algorithm.HMAC256("key".getBytes()));
                return response
                        .withStatusCode(200)
                        .withBody(String.format("{\"token\":\"%s\"}", token));
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
