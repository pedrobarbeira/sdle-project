package org.sdle.api.handler;

import org.sdle.api.Request;
import org.zeromq.ZMQ;

public interface RequestHandler {
    void handle(Request request, ZMQ.Socket socket);
}
