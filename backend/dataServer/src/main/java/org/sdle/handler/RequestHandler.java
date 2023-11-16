package org.sdle.handler;

import org.sdle.api.Request;
import org.sdle.api.Response;

public interface RequestHandler {
    Response handle(Request request);
    Response buildResponse(Object body);
}
