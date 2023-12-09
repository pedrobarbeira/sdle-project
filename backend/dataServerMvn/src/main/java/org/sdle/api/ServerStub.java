package org.sdle.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerStub extends Thread {
    public static final String API_BASE = "tcp://localhost";
    private final int port;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Router router;
    private ZContext ctx;

    public ServerStub(int port, Router router) throws IOException {
        this.port = port;
        this.router = router;
        this.ctx = new ZContext();
    }

    public void run(){
        ZMQ.Socket socket = ctx.createSocket(SocketType.REP);
        socket.connect(String.format("%s:%d", API_BASE, port));

        while (true) {
            try {
                String requestStr = socket.recvStr(0);
                Request request = mapper.readValue(requestStr, Request.class);
                Response response = router.route(request);
                String responseStr = mapper.writeValueAsString(response);
                socket.send(responseStr, 0);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class Worker implements Callable<String> {
        private final ZContext ctx;
        private final NodeConfig replica;
        private final String dataRoot;

        public Worker(ZContext ctx, NodeConfig replica, String dataRoot){
            this.ctx = ctx;
            this.replica = replica;
            this.dataRoot = dataRoot;
        }

        @Override
        public String call() throws Exception {
            ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
            socket.connect(String.format("%s:%d", API_BASE, replica.port));
            HashMap<String, String> headers = new HashMap<>(){{
                put(ApiComponent.Headers.DATA_ROOT, dataRoot);
            }};
            Request request = new Request(Router.REPLICA, Request.GET, headers, replica.dataRoot);
            String requestStr = mapper.writeValueAsString(request);
            socket.send(requestStr);
            return socket.recvStr();
        }
    }

    public String requestDataFromReplicas(List<String> targets, String dataRoot) throws IOException {
        ServerConfig config = ObjectFactory.getServerConfig();
        HashMap<String, NodeConfig> nodeMap = config.nodeMap;
        List<NodeConfig> replicas = new ArrayList<>();
        for (String root : targets) {
            replicas.add(nodeMap.get(root));
        }
        List<Worker> workers = new ArrayList<>();
        for (NodeConfig replica : replicas) {
            workers.add(new Worker(this.ctx, replica, dataRoot));
        }
        return executeWorkers(workers);
    }

    private String executeWorkers(List<Worker> workers) {
        StringBuilder toReturn = new StringBuilder();
        try {
            ServerConfig config = ObjectFactory.getServerConfig();
            ExecutorService executorService = Executors.newFixedThreadPool(config.maxActiveThreads);
            List<Future<String>> futures = new ArrayList<>();
            for(Worker worker : workers){
                futures.add(executorService.submit(worker));
            }
            for(Future<String> future : futures){
                toReturn.append(future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn.toString();
    }
}
