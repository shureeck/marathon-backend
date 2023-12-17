package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dao.Dao;
import dao.UsersDao;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;


@AllArgsConstructor
public class UsersHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler: UsersHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method:" + method);

        switch (method) {
            case "GET":
                Dao dao = new UsersDao();
                return response
                        .withStatusCode(200)
                        .withBody(dao.get());
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
