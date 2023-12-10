package org.sdle.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.zeromq.ZMQ;

public abstract class ApiComponent {
    public static final ObjectMapper mapper = ObjectFactory.getMapper();

    protected Response buildResponse(int status, Object body) {
        return new Response(status, body);
    }

    protected Response ok(Object body) {
        return new Response(StatusCode.OK, body);
    }

    protected Response badRequest(){
        return new Response(StatusCode.BAD_REQUEST, Message.BAD_REQUEST);
    }

    protected Response unauthorized(){
        return new Response(StatusCode.UNAUTHORIZED, Message.UNAUTHORIZED);
    }

    protected Response notAllowed(){
        return buildResponse(StatusCode.NOT_ALLOWED, Message.NOT_ALLOWED);
    }

    protected Response notFound(){
        return buildResponse(StatusCode.NOT_FOUND, Message.NOT_FOUND);
    }

    protected Response error(){
        return buildResponse(StatusCode.ERROR, Message.ERROR);
    }

    protected void badRequestResponse(ZMQ.Socket socket) throws JsonProcessingException {
        Response response = badRequest();
        String responseStr = mapper.writeValueAsString(response);
        socket.send(responseStr);
    }

    protected void okResponse(Object body, ZMQ.Socket socket) throws JsonProcessingException {
        Response response = ok(body);
        String responseStr = mapper.writeValueAsString(response);
        socket.send(responseStr);
    }

    public static class StatusCode{
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
        public static final int NOT_ALLOWED = 405;
        public static final int ERROR = 500;
    }

    public static class Message{
        public static final String BAD_REQUEST = "Bad request";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String NOT_FOUND = "Not found";
        public static final String NOT_ALLOWED = "Method not allowed";
        public static final String ERROR = "Internal server error";
    }

    public static class Headers{
        public static final String TOKEN = "token";
        public static final String USER = "user";
    }
}
