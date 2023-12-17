package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.DishesDao;
import dao.SceduleDao;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;

@AllArgsConstructor
public class ScedulerHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler: ScedulerHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);
        switch (method) {
            case "GET":
                Dao dao = new SceduleDao();
                return response
                        .withStatusCode(200)
                        .withBody(dao.get());
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
