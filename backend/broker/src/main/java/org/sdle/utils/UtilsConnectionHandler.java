package org.sdle.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;

import java.net.Socket;

public class UtilsConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final Router router;

    private static final ObjectMapper mapper = new ObjectMapper();

    public UtilsConnectionHandler(Socket socket, Router router)
    {
        this.router = router;
        this.clientSocket = socket;
    }

    @Override
    public void run()
    {
        try {
            String requestStr = UtilsTcp.receiveTcpMessage(this.clientSocket);

            if(requestStr == null) return;

            Request request = mapper.readValue(requestStr, Request.class);
            Response response = router.route(request);
            String responseStr = mapper.writeValueAsString(response);

            UtilsTcp.sendTcpMessage(this.clientSocket, responseStr);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to process request:\n" + e.getMessage());
        }
    }
}