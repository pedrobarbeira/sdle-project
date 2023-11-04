package org.sdle.api;

import org.sdle.handler.RequestHandler;
import org.sdle.handler.ShoppingListRequestHandler;

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

    public Response route(Request request){
        RequestHandler handler = handlers.get(request.getRoute());

        handler = (handler == null && request.getRoute().startsWith("api/shoppinglist")) ? handlers.get("api/shoppinglist") : handler;

        Response response = handler.handle(request);
        return response;
    }
}
