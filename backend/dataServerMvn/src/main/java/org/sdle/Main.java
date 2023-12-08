package org.sdle;

import org.sdle.api.Router;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments provided. Provide only the server port.");
        }

        int port = Integer.parseInt(args[0]);

        Node stub = new Node(String.valueOf(port));

        if(!stub.createStorageFolder()) {
            return;
        }

        stub.listen();
    }
}