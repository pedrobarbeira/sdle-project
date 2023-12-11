package org.sdle.server.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Router;
import org.sdle.config.ServerConfig;
import org.sdle.server.ObjectFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class DataFetchWorker implements Callable<String> {
    private final ZContext ctx;
    private final String target;
    HashMap<String, String> addressMap;
    private final ObjectMapper mapper = ObjectFactory.getMapper();

    public DataFetchWorker(ZContext ctx, ServerConfig config, String targets){
        this.ctx = ctx;
        this.target = targets;
        this.addressMap = config.addressMap;
    }

    @Override
    public String call() throws Exception {
        ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
        String address = addressMap.get(target);
        socket.connect(String.format(address));

        HashMap<String, String> headers = new HashMap<>();
        Request request = new Request(Router.API_REPLICA, Request.GET, headers, target);

        String requestStr = mapper.writeValueAsString(request);
        socket.send(requestStr);
        return socket.recvStr();
    }
}