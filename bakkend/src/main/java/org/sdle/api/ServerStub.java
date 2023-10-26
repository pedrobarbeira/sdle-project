package org.sdle.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.handler.RequestHandler;
import org.sdle.handler.ShoppingListHandler;

import java.io.IOException;
import java.util.Map;

public class ServerStub {
    public static final String SHOPPING_LIST_ROUTE = "api/shopping";

    private final Map<String, RequestHandler> handlers;
    private final ObjectMapper mapper = new ObjectMapper();

    public ServerStub(ShoppingListHandler shoppingListHandler){
        handlers = Map.of(
                SHOPPING_LIST_ROUTE, shoppingListHandler
        );
    }

    public String router(String requestString) throws IOException {
        Request request = mapper.readValue(requestString, Request.class);
        RequestHandler handler = handlers.get(request.getRoute());
        Response response = handler.handle(request);
        if(response != null){
            return mapper.writeValueAsString(response);
        }
        return "Could not process request";
    }
}
