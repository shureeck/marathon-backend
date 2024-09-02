package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.UsersDao;
import lombok.AllArgsConstructor;
import utils.Utils;

import java.util.*;


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

            case "POST":
                HashMap<String, String> user = Utils.jsonToObject((String) request.get("body"), HashMap.class);
                String check = userCheck(user);
                if (check.length() != 0) {
                    return response.withStatusCode(400)
                            .withBody(Utils.objectToJson(Map.of("message", check)));
                }
                Dao userDao = UsersDao.builder()
                        .userName(user.get("username"))
                        .password(user.get("password"))
                        .firstName(user.get("firstname"))
                        .lastName(user.get("lastname"))
                        .build();
                int n = userDao.put(null);
                if (n == 0) {
                    return response.withStatusCode(500)
                            .withBody(Utils.objectToJson(Map.of("message", "Unknown error. 0 users were created.")));
                }
                String createUser = userDao.get();
                return response.withStatusCode(201).withBody(createUser);
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }
    }

    private String userCheck(HashMap<String, String> user) {
        StringBuilder builder = new StringBuilder();
        String[] arr = {"firstname", "lastname", "username", "password"};
        for (String s : arr) {
            if (Objects.isNull(user.get(s))) {
                builder.append(String.format("Required filed %s is not specified;", s));
            }
        }
        return builder.toString();
    }
}
