package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.CookingDao;
import dao.Dao;
import dao.MarathonDao;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

@AllArgsConstructor
public class RootHandler implements IHandler {
    private LambdaLogger logger;
    private String userRole;
    private int userId;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler: RootHandler");
        String method = (String) request.get("httpMethod");
        HashMap<String, String> parameters = (HashMap<String, String>) request.get("queryStringParameters");
        switch (method) {
            case "GET":
                logger.log("Method: RootHandler::GET");
                return (Objects.nonNull(parameters) && parameters.containsKey("dish"))
                        ? getDataForGetWithDishId(response, parameters.get("dish"))
                        : getDataForGetMarathon(response, parameters);
            case "POST":
                logger.log("Method: RootHandler::POST");
                Dao dao = MarathonDao.builder().build();
                try {
                    return response
                            .withStatusCode(200)
                            .withBody(dao.put((String) request.get("body")));
                } catch (IllegalStateException e) {
                    return response.withStatusCode(422).withBody(e.getMessage());
                }

            case "DELETE":
                logger.log("Method: RootHandler::DELETE");
                String validation = deleteParametersValidations(parameters);
                if (!validation.isEmpty()) {
                    return response.withStatusCode(400).withBody(validation);
                }
                Dao marathonDao = MarathonDao.builder().build();
                String result = ((MarathonDao) marathonDao).delete(parameters);
                if (result.isEmpty()) {
                    return response.withStatusCode(200);
                } else {
                    return response.withStatusCode(500).withBody(result);
                }
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }

    private APIGatewayProxyResponseEvent getDataForGetMarathon(APIGatewayProxyResponseEvent response,
                                                               HashMap<String, String> parameters) {
        int id = -1;
        if (Objects.nonNull(parameters) && parameters.containsKey("id")) {
            id = Integer.valueOf(parameters.get("id"));
        }
        Dao dao = MarathonDao.builder().id(id).userId(userId).userRole(userRole).build();
        return response.withStatusCode(200).withBody(dao.get());
    }

    private APIGatewayProxyResponseEvent getDataForGetWithDishId(APIGatewayProxyResponseEvent response, String dishID) {
        Dao dao = new CookingDao(Integer.valueOf(dishID));
        String result = dao.get();
        if (Objects.nonNull(result)) {
            return response
                    .withStatusCode(200)
                    .withBody(dao.get());
        } else {
            return response
                    .withStatusCode(404)
                    .withBody("Code:404: Requested recipe with ID " + dishID + " not found.");
        }
    }

    private String deleteParametersValidations(HashMap<String, String> params) {
        String[] requireParams = {"week", "day", "schedule", "time", "marathon", "food"};
        String result = "";
        for (String p : requireParams) {
            if (Objects.isNull(params.get(p)) || params.get(p).isEmpty()) {
                result += p + " is require parameter;";
            }
        }
        return result;
    }
}
