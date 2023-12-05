package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.sdle.api.Router;
import org.sdle.utils.UtilsConnectionHandler;
import org.sdle.utils.UtilsHash;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node {
    private static final int numThreads = 8;
    private String id;
    private final Router router;
    private final int port;
    private final ServerSocket serverSocket;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService pool;
    public Node(String port) throws IOException {
        this.id = UtilsHash.generateMD5(port);
        this.router = new Router(ObjectFactory.initializeShoppingListRequestHandler(this.id));
        this.port = Integer.parseInt(port);

        serverSocket = new ServerSocket(this.port);

        pool = Executors.newFixedThreadPool(numThreads);
    }

    public boolean createStorageFolder() {
        try {
            String resourcesPath = Node.class.getClassLoader().getResource("data").getPath();
            String correctedResourcesPath = resourcesPath.substring(resourcesPath.indexOf("/") + 1);

            String folderPathString = correctedResourcesPath + File.separator + this.id;
            Path folderPath = Paths.get(folderPathString);

            Files.createDirectories(folderPath);
            System.out.println("Folder '" + this.id + "' created under resources/data");

            return true;
        } catch (FileAlreadyExistsException e) {
            System.out.println("Folder '" + this.id + "' already exists under resources/data");
            return true;
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        return false;
    }

    public void listen() throws IOException {
        System.out.println("Listening on port: " + this.port);

        while(!Thread.currentThread().isInterrupted()) {
            Socket socket = this.serverSocket.accept();

            //socket.setSoTimeout();

            Runnable connectionHandler = new UtilsConnectionHandler(socket, this.router);

            pool.execute(connectionHandler);
        }
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
}
