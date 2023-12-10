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

    public void handle(Request request, ZMQ.Socket socket){
        try {
            String method = request.getMethod();
            switch (method) {
                case AUTH -> authRequestHandler.handle(request, socket);
                default -> shoppingListRequestHandler.handle(request, socket);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
