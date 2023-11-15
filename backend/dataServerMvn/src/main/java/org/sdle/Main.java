package org.sdle;

import org.sdle.api.Router;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments provided. Provide only the server port.");
        }

        int port = Integer.parseInt(args[0]);

        Router router = new Router(ObjectFactory.initializeShoppingListRequestHandler());
        Node stub = new Node(router, String.valueOf(port));

        if(!stub.register()) return;

        stub.listen();
    }
}