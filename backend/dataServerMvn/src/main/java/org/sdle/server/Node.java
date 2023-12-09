package org.sdle.server;

import org.sdle.api.Router;
import org.sdle.api.ServerStub;
import org.sdle.api.handler.ShoppingListRequestHandler;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.service.ReplicaService;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node extends Thread {
    private final ReplicaService<ShoppingList> replicaService;
    private final NodeConfig config;
    public Node(NodeConfig config) {
        ZContext ctx = new ZContext();
        this.replicaService = new ReplicaService<>(ctx);
        this.config = config;
    }

    public Node(NodeConfig config, ReplicaService<ShoppingList> replicaService){
        this.config = config;
        this.replicaService = replicaService;
    }

    @Override
    public void run() {
        try{
            registerReplicaAddresses();
            initializeNode();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerReplicaAddresses() throws IOException {
        ServerConfig serverConfig = ObjectFactory.getServerConfig();
        List<String> replicatedOn = this.config.replicatedOn;
        List<String> addresses = new ArrayList<>();

        HashMap<String, String> addressMap = serverConfig.addressMap;
        for(String id : replicatedOn){
            String address = addressMap.get(id);
            addresses.add(address);
        }

        this.replicaService.registerReplicatedOn(addresses);
    }

    private void initializeNode() throws IOException {
        String dataRoot = this.config.dataRoot;

        ShoppingListRepository repository = new ShoppingListRepository(dataRoot);
        ShoppingListController controller = new ShoppingListController(repository);
        ShoppingListRequestHandler handler = new ShoppingListRequestHandler(controller);
        Router router = new Router(handler);

        ServerStub serverStub = new ServerStub(this.config.port, router);
        serverStub.boot(this.config.threadNum);
    }
}
