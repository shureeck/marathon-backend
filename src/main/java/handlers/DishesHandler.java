package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.DishesDao;
import enums.Languages;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static utils.Utils.translate;

@AllArgsConstructor
public class DishesHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler: DishesHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);
        HashMap<String, String> parameters = (HashMap<String, String>) request.get("queryStringParameters");
        switch (method) {
            case "GET":
                Dao dao = new DishesDao();
                if (Objects.nonNull(parameters) && parameters.containsKey("id")) {
                    ((DishesDao) dao).setId(Integer.valueOf(parameters.get("id")));
                }
                String sqlResp = dao.get();

                if (Objects.nonNull(parameters) && parameters.containsKey("loc") && !parameters.get("loc").equals("ua")) {
                    Languages lang = Languages.get(parameters.get("loc"));
                    sqlResp = translate(sqlResp, lang);
                }
                return response
                        .withStatusCode(200)
                        .withBody(sqlResp);
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
