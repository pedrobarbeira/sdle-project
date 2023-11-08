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
            status = 500;
        }else{
            status = 200;
        }
        return new Response(status, body);
    }

    @Override
    public Response buildResponse(int status, Object body) {
        return new Response(status, body);
    }
}
