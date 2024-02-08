package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.MarathonListDao;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;


@AllArgsConstructor
public class MarathonListHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler:MarathonListHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);
        HashMap<String, String> parameters = (HashMap<String, String>) request.get("queryStringParameters");
        Dao dao;
        switch (method) {
            case "GET":
                if (Objects.nonNull(parameters) && parameters.containsKey("user")) {
                    dao = MarathonListDao.builder().userID(Integer.valueOf(parameters.get("user"))).build();
                } else if (Objects.nonNull(parameters) && parameters.containsKey("dish")) {
                    dao = MarathonListDao.builder().dishID(Integer.valueOf(parameters.get("dish"))).build();
                } else {
                    dao = MarathonListDao.builder().build();
                }
                return response.withStatusCode(200)
                        .withBody(dao.get());
            case "POST":
                dao = MarathonListDao.builder().userID(1).build(); //:TODO Need ta add getting USER_ID from token
                String result = dao.put((String) request.get("body"));
                if (result.isEmpty()) {
                    return response.withStatusCode(201);
                } else if (Objects.isNull(result)) {
                    return response.withStatusCode(500).withBody("Unknown server error");
                } else {
                    return response.withStatusCode(500).withBody(result);
                }

            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
