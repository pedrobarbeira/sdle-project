package org.sdle.api;

public class Response {
    int status;
    Object body;

    public Response(int status, Object body){
        this.status = status;
        this.body = body;
    }
}
