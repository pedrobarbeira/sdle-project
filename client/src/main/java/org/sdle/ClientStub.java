package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class ClientStub extends ApiComponent {
    ClientConfig clientConfig;
    private final ZContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClientStub() throws IOException {
        this.ctx = new ZContext();
        this.clientConfig = ObjectFactory.getClientConfig();
    }

    public Response sendRequest(Request request) {
        int retries = 0;
        while(retries < clientConfig.communicationRetries) {
            try {
                ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
                socket.setSendTimeOut(clientConfig.communicationTimeout);
                socket.setReceiveTimeOut(clientConfig.communicationTimeout);
                socket.connect(clientConfig.apiBase);

                String requestStr = mapper.writeValueAsString(request);
                if(!socket.send(requestStr)){
                    retries++;
                    socket.close();
                    continue;
                }

                String responseStr = socket.recvStr();
                if(responseStr == null) {
                    retries++;
                    socket.close();
                    continue;
                }
                return mapper.readValue(responseStr, Response.class);
            }catch(Exception e){
                retries++;
                e.printStackTrace();
            }
        }
        return error();
    }
}
