package org.sdle;

import org.sdle.api.Router;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int numThreads = 8;
    public static void main(String[] args) {
        try {
            ExecutorService workers = Executors.newFixedThreadPool(numThreads);
            Broker stub = new Broker("5555", workers);
            stub.listen();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}