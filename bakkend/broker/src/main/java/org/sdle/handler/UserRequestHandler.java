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
            return buildResponse(null);
        }

        User user;

        try {
            user = new ObjectMapper().readValue((String) request.getBody(), User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return buildResponse(controller.login(user.getUsername(), user.getPassword()));
    }

    private Response verifyToken(Request request) {
        if(!Objects.equals(request.getMethod(), Request.GET)) {
            return buildResponse(null);
        }
        Token token;
        try {
            token = new ObjectMapper().readValue((String) request.getBody(), Token.class);
        } catch (JsonProcessingException e) {
            System.err.println("Json Processing exception: " + e.getMessage());
            return buildResponse(null);
        }

        return buildResponse(controller.verifyToken(token));
    }
}
