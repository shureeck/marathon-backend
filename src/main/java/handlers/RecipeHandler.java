package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.CookingDao;
import dao.Dao;
import dao.DishesDao;
import lombok.AllArgsConstructor;
import utils.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;

@AllArgsConstructor
public class RecipeHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler: RecipeHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);
        switch (method) {
            case "POST":
                HashMap<String, String> recipe = Utils.jsonToObject((String) request.get("body"), HashMap.class);
                Dao daoDishes = new DishesDao();
                Dao daoCooking;
                int id;
                try {
                    id = Integer.valueOf(daoDishes.put(recipe.get("tittle")));
                    daoCooking = new CookingDao(id);
                } catch (IllegalStateException e) {
                    return response.withStatusCode(422).withBody(e.getMessage());
                }

                daoCooking.put(recipe.get("text"));
                recipe.put("id", String.valueOf(id));
                return response
                        .withStatusCode(200)
                        .withBody(Utils.objectToJson(recipe));
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }
}
