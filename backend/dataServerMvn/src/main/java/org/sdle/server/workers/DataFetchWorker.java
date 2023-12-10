package org.sdle.server.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Router;
import org.sdle.config.NodeConfig;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class DataFetchWorker implements Callable<String> {
    private final ZContext ctx;
    private final NodeConfig target;
    private final String dataRoot;
    private final String apiBase;
    private final ObjectMapper mapper = new ObjectMapper();

    public DataFetchWorker(ZContext ctx, NodeConfig target, String dataRoot, String apiBase){
        this.ctx = ctx;
        this.target = target;
        this.dataRoot = dataRoot;
        this.apiBase = apiBase;
    }

    @Override
    public String call() throws Exception {
        ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
        socket.connect(String.format("%s:%d", this.apiBase, target.port));
        HashMap<String, String> headers = new HashMap<>(){{
            put(ApiComponent.Headers.DATA_ROOT, dataRoot);
        }};
        Request request = new Request(Router.REPLICA, Request.GET, headers, target.nodeId);
        String requestStr = mapper.writeValueAsString(request);
        socket.send(requestStr);
        return socket.recvStr();
    }
}