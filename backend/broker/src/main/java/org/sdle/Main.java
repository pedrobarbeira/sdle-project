package org.sdle;

import org.sdle.api.Router;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Router router = new Router(ObjectFactory.initializeUserRequestHandler());
        Broker stub = new Broker(router, "5555");
        stub.listen();
    }
}