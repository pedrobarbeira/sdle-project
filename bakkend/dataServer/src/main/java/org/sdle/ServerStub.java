package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerStub {
    //TODO consider implementing a MQ system here
    private final Router router;
    private final String port;
    private final ObjectMapper mapper = new ObjectMapper();

    private final ServerSocket serverSocket;

    public ServerStub(Router router, String port) throws IOException {
        this.router = router;
        this.port = port;
        this.serverSocket = new ServerSocket(Integer.parseInt(port));
    }

    public void listen() throws IOException {
        System.out.println("Listening " + this.port);
        int req = 0;
        while(true) {
            Socket clientSocket = this.serverSocket.accept();

            int finalReq = req;
            req = req + 1;
            Thread clientThread = new Thread(() -> {
                try {

                    handleClientRequest(clientSocket, finalReq);
                    } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            clientThread.start();
            //open port and listen
            //receive request string
            //handle request string in new thread
            //send response to broker
        }
    }

    private void handleClientRequest(Socket clientSocket, int req) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))){

            System.out.println("Request " + req);
            int i = 0;
            while(i < 20) {
                System.out.println(reader.readLine());
                i++;
            }

            System.out.println("Exiting " + req);
            writer.flush();
            clientSocket.close();
        }
    }

    private String handleRequestString(String requestString) throws IOException {
        Request request = mapper.readValue(requestString, Request.class);
        Response response = router.route(request);
        return mapper.writeValueAsString(response);
    }
}
