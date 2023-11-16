package org.sdle.handler;

import org.sdle.api.Request;
import org.sdle.api.Response;

public abstract class AbstractRequestHandler implements RequestHandler{

    @Override
    public abstract Response handle(Request request);

    @Override
    public Response buildResponse(Object body) {
        int status;
        if(body == null){
            status = StatusCode.ERROR;
        }else{
            status = StatusCode.OK;
        }
        return new Response(status, body);
    }

    private static class StatusCode{
        public static final int ERROR = 500;
        public static final int OK = 200;
    }
}
