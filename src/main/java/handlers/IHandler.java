package handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.LinkedHashMap;

public interface IHandler {
    public APIGatewayProxyResponseEvent handle (LinkedHashMap<String, Object> request, APIGatewayProxyResponseEvent response);
}
