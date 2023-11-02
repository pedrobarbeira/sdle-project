package org.sdle;

import org.sdle.api.Router;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Router router = new Router(ObjectFactory.initializeShoppingRequestHandler());
        ServerStub stub = new ServerStub(router, "5555");
        stub.listen();
    }
}