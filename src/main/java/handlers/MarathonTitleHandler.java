package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.MarathonDao;
import dao.MarathonListDao;
import dao.MarathonListHeaderDao;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

@AllArgsConstructor
public class MarathonTitleHandler implements IHandler {
    private LambdaLogger logger;
    private String userRole;
    private int userId;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler:MarathonTitleHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);
        HashMap<String, String> parameters = (HashMap<String, String>) request.get("queryStringParameters");
        switch (method) {
            case "GET":
                int id = -1;
                if (Objects.nonNull(parameters) && parameters.containsKey("id")) {
                    id = Integer.valueOf(parameters.get("id"));
                }
                Dao dao = MarathonListHeaderDao.builder().marathonId(id).userId(userId).userRole(userRole).build();
                return response.withStatusCode(200)
                        .withBody(dao.get());
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
