package org.sdle.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.zeromq.ZMQ;

public interface RequestHandler {
    Response handle(Request request);
}
