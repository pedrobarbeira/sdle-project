package org.sdle.api;

import org.sdle.handler.RequestHandler;
import org.sdle.handler.ShoppingRequestHandler;

import java.util.Map;

public class Router {
    public static final String SHOPPING_LIST_ROUTE = "api/shopping";

    private final Map<String, RequestHandler> handlers;

    public Router(ShoppingRequestHandler shoppingListHandler){
        handlers = Map.of(
                SHOPPING_LIST_ROUTE, shoppingListHandler
        );
    }

    public Response route(Request request){
        RequestHandler handler = handlers.get(request.getRoute());
        return handler.handle(request);
    }
}
