package org.sdle.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.UserController;
import org.sdle.model.Token;
import org.sdle.model.User;
import org.sdle.service.TokenService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserRequestHandler extends AbstractRequestHandler {

    public static final List<String> ID_METHODS = Arrays.asList(Request.POST, Request.GET);

    private final UserController controller;

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

        User user = this.parse(request.getBody(), User.class);

        if(user == null) return buildResponse(400, "Bad request - user not found");

        Token token = controller.login(user.getUsername(), user.getPassword());

        if(token == null) return buildResponse(401, "Unauthorized - bad credentials");

        return buildResponse(token);
    }

    private Response verifyToken(Request request) {
        if(!Objects.equals(request.getMethod(), Request.GET)) {
            return buildResponse(405, "Method not allowed");
        }
        Token token = this.parse(request.getBody(), Token.class);

        if(token == null) return buildResponse(400, "Bad request - token not found");

        HashMap<String, String> response = controller.verifyToken(token);

        if(response == null) buildResponse(401, "Unauthorized - bad user token");

        return buildResponse(response);
    }

    private <T> T parse(Object body, Class<T> expectedClass) {
        try {
            return new ObjectMapper().readValue((String) body, expectedClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
