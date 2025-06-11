package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.CookingDao;
import dao.Dao;
import dao.MarathonDao;
import enums.Languages;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static utils.Utils.translate;

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
                        ? getDataForGetWithDishId(response, parameters)
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
        String sqlResp = dao.get();

        if (Objects.nonNull(parameters) && parameters.containsKey("loc") && !parameters.get("loc").equals("ua")) {
            Languages lang = Languages.get(parameters.get("loc"));
            if (Objects.nonNull(lang)) {
                switch (lang) {
                    case english:
                        sqlResp = sqlResp.replaceAll("Тиждень", "Week")
                                .replaceAll("Понеділок", "Monday")
                                .replaceAll("Вівторок", "Tuesday")
                                .replaceAll("Середа", "Wednesday")
                                .replaceAll("Четвер", "Thursday")
                                .replaceAll("П'ятниця", "Friday")
                                .replaceAll("Субота", "Saturday")
                                .replaceAll("Неділя", "Sunday")
                                .replaceAll("Сніданок", "Breakfast")
                                .replaceAll("Перекус", "Snack")
                                .replaceAll("Обід", "Lunch")
                                .replaceAll("Вечеря", "Supper");
                        break;
                    case polish:
                        sqlResp = sqlResp.replaceAll("Тиждень", "Tydzień")
                                .replaceAll("Понеділок", "Poniedziałek")
                                .replaceAll("Вівторок", "Wtorek")
                                .replaceAll("Середа", "Środa")
                                .replaceAll("Четвер", "Czwartek")
                                .replaceAll("П'ятниця", "Piątek")
                                .replaceAll("Субота", "Sobota")
                                .replaceAll("Неділя", "Niedziela")
                                .replaceAll("Сніданок", "Śniadanie")
                                .replaceAll("Перекус", "Przekąska")
                                .replaceAll("Обід", "Obiad")
                                .replaceAll("Вечеря", "Kolacja");
                        break;
                }
            }
        }
        return response.withStatusCode(200).withBody(sqlResp);
    }

    private APIGatewayProxyResponseEvent getDataForGetWithDishId(APIGatewayProxyResponseEvent response, HashMap<String, String> parameters) {
        String dishID = parameters.get("dish");
        Dao dao = new CookingDao(Integer.valueOf(dishID));
        String sqlResp = dao.get();

        if (parameters.containsKey("loc") && !parameters.get("loc").equals("ua")) {
            Languages lang = Languages.get(parameters.get("loc"));
            sqlResp = translate(sqlResp, lang);
        }
        if (Objects.nonNull(sqlResp)) {
            return response
                    .withStatusCode(200)
                    .withBody(sqlResp);
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
