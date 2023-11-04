package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class Node {

    private final Router router;
    private final int port;
    private final ZContext context = new ZContext();
    private final ZMQ.Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();

    public Node(Router router, String port) {
        this.router = router;
        this.port = Integer.parseInt(port);
        this.socket = context.createSocket(SocketType.REP);
        this.socket.bind("tcp://*:" + this.port);
    }

    public void listen() throws IOException {
        System.out.println("Listening on port: " + this.port);

        while(!Thread.currentThread().isInterrupted()) {
            String request = this.socket.recvStr();
            String response = handleRequest(request);

            this.socket.send(response);
        }
    }

    private String handleRequest(String requestString) throws IOException {
        Request request = mapper.readValue(requestString, Request.class);
        Response response = router.route(request);
        return mapper.writeValueAsString(response);
    }
}
