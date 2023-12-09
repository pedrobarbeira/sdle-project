package org.sdle.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.concurrent.ExecutorService;

public class UserRequestHandler extends AbstractRequestHandler {
    private final UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    private final ExecutorService workers;

    public UserRequestHandler(UserController controller, ExecutorService worker) {
        this.controller = controller;
        this.workers = worker;
    }

    @Override
    public Response handle(Request request) {
        try{
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
        }catch( JsonProcessingException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Response login(Request request) throws JsonProcessingException {
        if(!Objects.equals(request.getMethod(), Request.POST)) {
            return buildResponse(405, "Method not allowed");
        }
        String body = (String) request.getBody();
        User user = mapper.readValue(body, User.class);

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
