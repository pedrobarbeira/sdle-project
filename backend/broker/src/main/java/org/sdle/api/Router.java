package org.sdle.api;

import org.sdle.handler.NodeRequestHandler;
import org.sdle.handler.RequestHandler;
import org.sdle.handler.UserRequestHandler;

import java.util.Map;

public class Router {
    public static final java.lang.String LOGIN_ROUTE = "api/login";
    public static final java.lang.String AUTH_TOKEN_ROUTE = "api/verify-token";

    public static final java.lang.String REGISTER_NODE_ROUTE = "api/register-node";

    private final Map<java.lang.String, RequestHandler> handlers;

    public Router(UserRequestHandler userRequestHandler, NodeRequestHandler nodeRequestHandler){
        handlers = Map.of(
                LOGIN_ROUTE, userRequestHandler,
                AUTH_TOKEN_ROUTE, userRequestHandler,
                REGISTER_NODE_ROUTE, nodeRequestHandler
        );
    }

    public Response route(Request request){
        RequestHandler handler = handlers.get(request.getRoute());

        if(handler == null) return new Response(404, "Route not found");

        return handler.handle(request);
    }
}
