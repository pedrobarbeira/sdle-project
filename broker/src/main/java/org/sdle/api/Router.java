package org.sdle.api;

import org.sdle.api.handler.AuthRequestHandler;
import org.sdle.api.handler.OperationRequestHandler;

public class Router extends ApiComponent{
    public static final String API_AUTH = "api/auth";

    private final OperationRequestHandler shoppingListRequestHandler;
    private final AuthRequestHandler authRequestHandler;

    public Router(OperationRequestHandler shoppingListRequestHandler, AuthRequestHandler authRequestHandler) {
        this.authRequestHandler = authRequestHandler;
        this.shoppingListRequestHandler = shoppingListRequestHandler;
    }

    public Response handle(Request request){
        try {
            String route = request.route;
            switch (route) {
                case API_AUTH -> {
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
