package org.sdle.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.UserController;
import org.sdle.model.Token;
import org.sdle.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserRequestHandler extends AbstractRequestHandler {
    private final UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserRequestHandler(UserController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {
        switch (request.getRoute()) {
            case Router.LOGIN_ROUTE -> {
                return login(request);
            }
            case Router.AUTH_TOKEN_ROUTE -> {
                return verifyToken(request);
            }
            default -> {
                return buildResponse(null);
            }
        }
    }

    private Response login(Request request) {
        if(!Objects.equals(request.getMethod(), Request.POST)) {
            return buildResponse(405, "Method not allowed");
        }

        User user = mapper.convertValue(request.getBody(), User.class);

        if(user == null) return buildResponse(400, "Bad request - user not found in request body");

        Token token = controller.login(user.getUsername(), user.getPassword());

        if(token == null) return buildResponse(401, "Unauthorized - bad credentials");

        return buildResponse(token);
    }

    private Response verifyToken(Request request) {
        if(!Objects.equals(request.getMethod(), Request.GET)) {
            return buildResponse(405, "Method not allowed");
        }
        Token token = mapper.convertValue(request.getBody(), Token.class);

        if(token == null) return buildResponse(400, "Bad request - token not found");

        HashMap<String, String> response = controller.verifyToken(token);

        if(response == null) buildResponse(401, "Unauthorized - bad user token");

        return buildResponse(response);
    }
}
