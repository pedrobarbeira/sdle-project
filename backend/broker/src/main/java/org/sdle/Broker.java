package org.sdle;

import org.sdle.api.Router;
import org.sdle.handler.NodeRequestHandler;
import org.sdle.handler.UserRequestHandler;
import org.sdle.utils.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;


public class Broker {
    private final Router router;
    private final int port;
    private final ServerSocket serverSocket;
    private final ExecutorService workers;

    public Broker(String port, ExecutorService workers) throws IOException {
        this.port = Integer.parseInt(port);
        this.serverSocket = new ServerSocket(this.port);
        this.workers = workers;
        UserRequestHandler userHandler = ObjectFactory.initializeUserRequestHandler(this.workers);
        NodeRequestHandler nodeHandler = ObjectFactory.initializeNodeRequestHandler(this.workers);
        this.router = new Router(userHandler, nodeHandler);
    }

    public void listen() throws IOException {
        System.out.println("Listening on port: " + this.port);
        while(!Thread.currentThread().isInterrupted()) {
            Socket socket = this.serverSocket.accept();

            Runnable connectionHandler = new ConnectionHandler(socket, this.router);

            workers.execute(connectionHandler);
        }
    }
}
