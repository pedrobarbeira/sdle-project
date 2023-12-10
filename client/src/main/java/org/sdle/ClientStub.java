package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ClientStub extends ApiComponent {
    public static final String API_BASE = "tcp://localhost:5555";
    private final ZContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClientStub() {
        this.ctx = new ZContext();
    }

    public Response sendRequest(Request request) {
        try {
            ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
            socket.connect(API_BASE);
            String requestStr = mapper.writeValueAsString(request);
            socket.send(requestStr);
            String responseStr = socket.recvStr();
            return mapper.readValue(responseStr, Response.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return error();
    }
}
