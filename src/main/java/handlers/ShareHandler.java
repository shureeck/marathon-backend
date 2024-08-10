package handlers;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dao.Dao;
import dao.MarathonAssignDao;
import entities.ShareRequestEntity;
import lombok.AllArgsConstructor;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ShareHandler implements IHandler {
    private LambdaLogger logger;

    @Override
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response) {
        logger.log("Handler:ShareHandler");
        String method = (String) request.get("httpMethod");
        logger.log("Method: " + method);
        Dao dao;
        switch (method) {
            case "POST":
                ShareRequestEntity shareRequest = Utils.jsonToObject((String) request.get("body"), ShareRequestEntity.class);
                if (Objects.isNull(shareRequest.getMarathonId())
                        || !shareRequest.getMarathonId().matches("\\d+")) {
                    return response.withStatusCode(400)
                            .withBody("{\"message\":\"Bad user input. MarathonID is incorrect.\"}");
                }
                dao = MarathonAssignDao.builder()
                        .marathonId(Integer.parseInt(shareRequest.getMarathonId()))
                        .build();
                ArrayList<String> currentUsersId = dao.get();

                if (Objects.isNull(currentUsersId)) {
                    return response.withStatusCode(500)
                            .withBody("{\"message\":\"Unknown server error.\"}");
                }
                // Get ids that should be deleted
                List<Integer> toDelete = currentUsersId.stream()
                        .filter(p -> !(shareRequest.getUsers().contains(p)))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                if (toDelete.size() > 0) {
                    logger.log("access to marathon with ID "
                            + shareRequest.getMarathonId()
                            + " will be deleted for users with IDs: "
                            + toDelete.stream().map(String::valueOf).collect(Collectors.joining(", ")));
                    ((MarathonAssignDao) dao).setUsers(toDelete);
                    ((MarathonAssignDao) dao).delete();
                }

                //Get objects to Insert
                List<Integer> toInsert = shareRequest.getUsers().stream()
                        .filter(p -> !(currentUsersId.contains(p)))
                        .map(p -> Integer.parseInt(p))
                        .collect(Collectors.toList());
                if (toInsert.size() > 0) {
                    logger.log("access to marathon with ID "
                            + shareRequest.getMarathonId()
                            + " will be granted for users with IDs: "
                            + toInsert.stream().map(String::valueOf).collect(Collectors.joining(", ")));
                    ((MarathonAssignDao) dao).setUsers(toInsert);
                    dao.put(null);
                }
                return response.withStatusCode(201);
            default:
                return response.withStatusCode(400)
                        .withBody(String.format("Method %s is not supported.", method));
        }

    }
}
