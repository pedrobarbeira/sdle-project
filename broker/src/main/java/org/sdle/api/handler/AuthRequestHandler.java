package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.service.AuthService;
import org.zeromq.ZMQ;

public class AuthRequestHandler extends ApiComponent implements RequestHandler{
    private final AuthService service;

    public AuthRequestHandler(AuthService service) {
        this.service = service;
    }

    @Override
    public void handle(Request request, ZMQ.Socket socket) {

    }
}
