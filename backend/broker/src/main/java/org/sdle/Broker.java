package org.sdle;

import org.sdle.api.Router;
import org.sdle.utils.UtilsConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Broker {
    private static final int numThreads = 8;
    private final Router router;
    private final int port;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public Broker(Router router, String port) throws IOException {
        this.router = router;
        this.port = Integer.parseInt(port);

        this.serverSocket = new ServerSocket(this.port);

        pool = Executors.newFixedThreadPool(numThreads);
    }

    public void listen() throws IOException {
        System.out.println("Listening on port: " + this.port);

        while(!Thread.currentThread().isInterrupted()) {
            Socket socket = this.serverSocket.accept();

            Runnable connectionHandler = new UtilsConnectionHandler(socket, this.router);

            pool.execute(connectionHandler);
        }
    }
}