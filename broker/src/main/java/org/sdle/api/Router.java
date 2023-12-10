package org.sdle.api;

import org.sdle.api.handler.AuthRequestHandler;
import org.sdle.api.handler.ShoppingListRequestHandler;
import org.zeromq.ZMQ;

public class Router extends ApiComponent{
    public static final String AUTH = "api/auth";

    private final ShoppingListRequestHandler shoppingListRequestHandler;
    private final AuthRequestHandler authRequestHandler;

    public Router(ShoppingListRequestHandler shoppingListRequestHandler, AuthRequestHandler authRequestHandler) {
        this.authRequestHandler = authRequestHandler;
        this.shoppingListRequestHandler = shoppingListRequestHandler;
    }

    public Response handle(Request request){
        try {
            String route = request.route;
            switch (route) {
                case AUTH -> {
                    return authRequestHandler.handle(request);
                }
                default -> {
                    return shoppingListRequestHandler.handle(request);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return error();
        }
    }
}
