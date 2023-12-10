package org.sdle.api;

public class Response {
    private int status;
    private Object body;

    public Response() {}

    public Response(int status, Object body){
        this.status = status;
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }
}
