package org.sdle.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.config.ServerConfig;
import org.sdle.server.ObjectFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ServerStub{
    public static final String API_WORKERS = "inproc://workers";
    private final int port;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Router router;
    private final ZContext ctx;

    public ServerStub(int port, Router router) {
        this.port = port;
        this.router = router;
        this.ctx = new ZContext();
    }

    public void boot(int threadNum) throws IOException {
        ZMQ.Socket clients = ctx.createSocket(SocketType.ROUTER);
        ServerConfig serverConfig = ObjectFactory.getServerConfig();
        String apiBase = serverConfig.apiBase;
        String address = String.format("%s:%d", apiBase, this.port);
        clients.bind(address);

        ZMQ.Socket workers = ctx.createSocket(SocketType.DEALER);
        workers.bind(API_WORKERS);

        ExecutorService executorService = ObjectFactory.getExecutorService();
        for(int i = 0; i < threadNum; i++){
            ServerWorker worker = new ServerWorker(ctx);
            executorService.execute(worker);
        }
        ZMQ.proxy(clients, workers, null);
    }

    class ServerWorker implements Runnable {
        private final ZContext ctx;

        public ServerWorker(ZContext ctx){
            this.ctx = ctx;
        }
        @Override
        public void run() {
            ZMQ.Socket socket = ctx.createSocket(SocketType.REP);
            socket.connect(API_WORKERS);
            while(true){
                try {
                    String requestStr = socket.recvStr();
                    Request request = mapper.readValue(requestStr, Request.class);

                    Response response = router.route(request);
                    String responseStr = mapper.writeValueAsString(response);
                    socket.send(responseStr);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
