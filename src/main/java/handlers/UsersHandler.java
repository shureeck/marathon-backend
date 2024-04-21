package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.UsersDao;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;


@AllArgsConstructor
public class UsersHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler: UsersHandler");
        String method = (String) request.get("httpMethod");
        HashMap<String, String> parameters = (HashMap<String, String>) request.get("queryStringParameters");
        logger.log("Method:" + method);
        switch (method) {
            case "GET":
                Dao dao;
                if (Objects.nonNull(parameters) && Objects.nonNull(parameters.get("marathonID"))) {
                    dao = UsersDao.builder().marathonId(parameters.get("marathonID")).build();
                } else {
                    dao = UsersDao.builder().build();
                }
                return response
                        .withStatusCode(200)
                        .withBody(dao.get());
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
