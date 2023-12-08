package org.sdle.api;

import org.sdle.api.handler.RequestHandler;
import org.sdle.api.handler.ShoppingListRequestHandler;

import java.util.Map;

public class Router {
    public static final String SHOPPINGLIST = "api/shoppinglist";
    public static final String SHOPPINGLIST_SHARE = "api/shoppinglist-share";
    private final Map<String, RequestHandler> handlers;

    public Router(ShoppingListRequestHandler shoppingListRequestHandler){
        handlers = Map.of(
                SHOPPINGLIST, shoppingListRequestHandler,
                SHOPPINGLIST_SHARE, shoppingListRequestHandler
        );
    }

    public Response route(Request request) {
        RequestHandler handler = handlers.get(request.getRoute());

        if(handler == null) return new Response(404, "Route not found");

        return handler.handle(request);
    }
}
