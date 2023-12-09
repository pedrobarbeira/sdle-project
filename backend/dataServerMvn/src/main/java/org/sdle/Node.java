package org.sdle;

import org.sdle.api.Router;
import org.sdle.api.ServerStub;
import org.sdle.api.handler.ShoppingListRequestHandler;
import org.sdle.config.NodeConfig;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.service.CRDTExecutionService;
import org.sdle.service.ReplicaService;

import java.io.IOException;

public class Node extends Thread {
    private final ReplicaService<ShoppingList> replicaService;
    NodeConfig config;
    public Node(NodeConfig config) throws IOException {
        this.replicaService = new ReplicaService<>(config.dataRoot);
        this.config = config;
    }

    @Override
    public void run() {
        try {
            ShoppingListRepository repository = new ShoppingListRepository(config.dataRoot);
            ShoppingListController controller = new ShoppingListController(repository);
            ShoppingListRequestHandler requestHandler = new ShoppingListRequestHandler(controller);
            Router router = new Router(requestHandler);
            ServerStub serverStub = new ServerStub(config.port, router);
            replicaService.synchronize(serverStub, config.replicatedOn);
            serverStub.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
