package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.utils.UtilsConnectionHandler;
import org.sdle.utils.UtilsHash;
import org.sdle.utils.UtilsTcp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;
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
    public Node(Router router, String port) throws IOException {
        this.router = router;
        this.port = Integer.parseInt(port);

        serverSocket = new ServerSocket(this.port);

        pool = Executors.newFixedThreadPool(numThreads);
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

    public boolean register() {
        try {
            Request request = new Request("api/register-node", Request.POST, null, mapper.convertValue(this, Map.class));

            String requestStr = mapper.writeValueAsString(request);
            Socket socket = new Socket("localhost", 5555);

            UtilsTcp.sendTcpMessage(socket, requestStr);

            String responseStr = UtilsTcp.receiveTcpMessage(socket);
            Response response = mapper.readValue(responseStr, Response.class);

            if(response.getStatus() != 200) {
                System.err.println(response.getBody());
                return false;
            }

            String id = (String) mapper.convertValue(response.getBody(), Map.class).get("id");
            if(id == null) return false;

            this.id = id;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
}
