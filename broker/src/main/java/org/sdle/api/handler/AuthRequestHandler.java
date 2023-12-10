package org.sdle.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.service.AuthService;
import org.zeromq.ZMQ;

import java.util.HashMap;


public class AuthRequestHandler extends ApiComponent implements RequestHandler{
    private final AuthService service;

    public AuthRequestHandler(AuthService service) {
        this.service = service;
    }

    @Override
    public Response handle(Request request){
        String method = request.getMethod();
        switch(method){
            case Request.GET -> {
                return handleLoginRequest(request);
            }
            case Request.POST -> {
                return handleRegisterRequest(request);
            }
            case Request.PUT -> {
                return handleLogoutRequest(request);
            }
            default -> {
                return badRequest();
            }
        }
    }

    private Response handleLoginRequest(Request request){
        Object body = request.getBody();
        HashMap<String, String> bodyData = (HashMap<String, String>) body;
        String username = bodyData.get(Constants.USERNAME);
        String password = bodyData.get(Constants.PASSWORD);
        String token = service.generateToken(username, password);
        if(token != null){
            return ok(token);
        }
        return badRequest();
    }

    private Response handleRegisterRequest(Request request) {
        Object body = request.getBody();
        HashMap<String, String> bodyData = (HashMap<String, String>) body;
        String username = bodyData.get(Constants.USERNAME);
        String password = bodyData.get(Constants.PASSWORD);
        String token = service.register(username, password);
        if(token != null){
            return ok(token);
        }
        return badRequest();
    }

    private Response handleLogoutRequest(Request request){
        HashMap<String, String> headers = request.getHeaders();
        String token = headers.get(Headers.TOKEN);
        String username = (String) request.getBody();
        if(service.validateToken(username, token)){
            return ok(token);
        }
        return badRequest();
    }

    static class Constants{
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }
}
