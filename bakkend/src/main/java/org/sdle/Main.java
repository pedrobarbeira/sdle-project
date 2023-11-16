package org.sdle;

import org.sdle.api.Router;
import org.sdle.configuration.ServerConfig;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerConfig config = ObjectFactory.loadServerConfig();
        Router router = new Router(ObjectFactory.initializeShoppingRequestHandler());
        ServerStub stub = new ServerStub(router, config.getPort());
        stub.listen();
    }
}