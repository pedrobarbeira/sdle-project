package org.sdle.api.handler;

import org.sdle.api.Request;
import org.sdle.api.Response;

public interface RequestHandler {
    Response handle(Request request);
    Response buildResponse(int status, Object body);
}
