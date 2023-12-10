package org.sdle.api;

import org.sdle.api.handler.*;

import java.util.Map;

public class Router extends  ApiComponent {
    public static final String API_REPLICA = "api/replica";
    public static final String API_SHOPPINGLIST = "api/shoppinglist";
    public static final String API_SHARED = "api/shared";
    public static final String API_ITEMS = "api/items";
    private final Map<String, RequestHandler> handlers;

    public Router(ReplicaRequestHandler replicaRequestHandler,
                  ShoppingListRequestHandler shoppingListRequestHandler,
                  SharedListRequestHandler sharedListRequestHandler,
                  ShoppingItemRequestHandler shoppingItemRequestHandler){
        handlers = Map.of(
                API_REPLICA, replicaRequestHandler,
                API_SHOPPINGLIST, shoppingListRequestHandler,
                API_SHARED, sharedListRequestHandler,
                API_ITEMS, shoppingItemRequestHandler
        );
    }

    public Response route(Request request) {
        String username = request.headers.get(Headers.USER);
        if(username == null){
            return unauthorized();
        }
        RequestHandler handler = handlers.get(request.getRoute());
        if(handler == null) {
            return notFound();
        }

        return handler.handle(request);
    }
}
