package handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import utils.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Handler {
    public APIGatewayProxyResponseEvent handle(LinkedHashMap<String, Object> object, Context context) {
        IHandler handler;
        String path = (String) object.get("path");
        String method = (String) object.get("httpMethod");

        LambdaLogger logger = context.getLogger();
        logger.log(Utils.objectToJson(object));
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json",
                        "Access-Control-Allow-Origin", "*",
                        "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
                        "Access-Control-Allow-Methods", "POST,GET,OPTIONS"))
                .withIsBase64Encoded(false);

        /**
         * Autorization check
         */
        String token = (String) ((HashMap<String, Object>) object.get("headers"))
                .get("Authorization");
        if (!path.equals("/login")) {
            if (Objects.nonNull(token)) {
                JWTVerifier verifier = JWT.require(Algorithm.HMAC256("key".getBytes())).build();
                try {
                    DecodedJWT decodedJWT = verifier.verify(token);
                } catch (SignatureVerificationException e) {
                    logger.log(e.getMessage());
                    return response.withStatusCode(401).withBody("{\"message\":" + e.getMessage() + "}");
                }
            } else {
                logger.log("{\"message\":\"Autorization token is missing\"}");
                return response.withStatusCode(401).withBody("{\"message\":\"Autorization token is missing\"}");
            }
        }

        /**
         * Autorization check
         */

        switch (path) {
            case "/":
                handler = new RootHandler(logger);
                break;
            case "/all":
                handler = new AllRecipesHandler(logger);
                break;
            case "/sceduler":
                handler = new ScedulerHandler(logger);
                break;
            case "/dishes":
                handler = new DishesHandler(logger);
                break;
                case "/recipe":
                handler = new RecipeHandler(logger);
                break;
            case "/login":
                handler = new LoginHandler(logger);
                break;
            case "/marathon_list":
                handler = new MarathonListHandler(logger);
                break;
            case "/users":
                handler = new UsersHandler(logger);
                break;
            case "/marathonTittle":
                handler = new MarathonTitleHandler(logger);
                break;
            default:
                return response
                        .withStatusCode(400)
                        .withBody("Unknown request");
        }
        return handler.handle(object, response);
    }
}
