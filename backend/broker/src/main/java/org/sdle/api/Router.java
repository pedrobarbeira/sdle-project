package org.sdle.api;

import org.sdle.handler.RequestHandler;
import org.sdle.handler.UserRequestHandler;

import java.util.Map;

public class Router {
    public static final String LOGIN_ROUTE = "api/login";
    public static final String AUTH_TOKEN_ROUTE = "api/verify-token";

    private final Map<String, RequestHandler> handlers;

    public Router(UserRequestHandler userRequestHandler){
        handlers = Map.of(
                LOGIN_ROUTE, userRequestHandler,
                AUTH_TOKEN_ROUTE, userRequestHandler
        );
    }

    public Response route(Request request){
        RequestHandler handler = handlers.get(request.getRoute());

        if(handler == null) return new Response(404, "Route not found");

        return handler.handle(request);
    }
}
