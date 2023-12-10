package org.sdle.api;

public abstract class ApiComponent {

    public Response buildResponse(int status, Object body) {
        return new Response(status, body);
    }

    public Response ok(Object body) {
        return new Response(StatusCode.OK, body);
    }

    public Response badRequest(){
        return new Response(StatusCode.BAD_REQUEST, Message.BAD_REQUEST);
    }

    public Response unauthorized(){
        return new Response(StatusCode.UNAUTHORIZED, Message.UNAUTHORIZED);
    }

    public Response notAllowed(){
        return buildResponse(StatusCode.NOT_ALLOWED, Message.NOT_ALLOWED);
    }

    public Response notFound(){
        return buildResponse(StatusCode.NOT_FOUND, Message.NOT_FOUND);
    }

    public Response error(){
        return buildResponse(StatusCode.ERROR, Message.ERROR);
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

    public static class Headers {
        public static final String TOKEN = "token";
        public static final String KEY = "key";
        public static final String USER = "user";
    }
}
