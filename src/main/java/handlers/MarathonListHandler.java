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
                    dao = new MarathonListDao(Integer.valueOf(parameters.get("user")));
                } else {
                    dao = new MarathonListDao();
                }
                return response.withStatusCode(200)
                        .withBody(dao.get());
            case "POST":
                dao = new MarathonListDao(1); //:TODO Need ta add getting USER_ID from token
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
