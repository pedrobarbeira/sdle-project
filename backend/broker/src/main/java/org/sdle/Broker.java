package org.sdle;

import org.sdle.api.Router;
import org.sdle.utils.UtilsConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Broker {
    private static final int numThreads = 8;
    private final Router router;
    private final int port;
    private final ServerSocket serverSocket;
    private final ExecutorService workers;

    public Broker(String port) throws IOException {
        this.port = Integer.parseInt(port);

        this.serverSocket = new ServerSocket(this.port);

        this.workers = Executors.newFixedThreadPool(numThreads);

        this.router = new Router(ObjectFactory.initializeUserRequestHandler(this.workers), ObjectFactory.initializeNodeRequestHandler(this.workers));
    }

    public void listen() throws IOException {
        System.out.println("Listening on port: " + this.port);

        while(!Thread.currentThread().isInterrupted()) {
            Socket socket = this.serverSocket.accept();

            Runnable connectionHandler = new UtilsConnectionHandler(socket, this.router);
            workers.execute(connectionHandler);
        }
    }
}
