package org.sdle;

import org.sdle.api.Router;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Broker stub = new Broker("5555");
            stub.listen();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}