package org.sdle.server;

import org.sdle.api.Router;
import org.sdle.api.ServerStub;
import org.sdle.api.controller.SharedListController;
import org.sdle.api.controller.ShoppingItemController;
import org.sdle.api.controller.ShoppingListController;
import org.sdle.api.handler.ReplicaRequestHandler;
import org.sdle.api.handler.SharedListRequestHandler;
import org.sdle.api.handler.ShoppingItemRequestHandler;
import org.sdle.api.handler.ShoppingListRequestHandler;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.sdle.api.controller.ReplicaController;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.service.CRDTExecutionService;
import org.sdle.service.ReplicaService;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node extends Thread {
    private final ReplicaService<ShoppingList> replicaService;
    private final CRDTExecutionService<ShoppingList> executionService;
    private final NodeConfig config;
    public Node(NodeConfig config, CRDTExecutionService<ShoppingList> executionService) {
        ZContext ctx = new ZContext();
        this.replicaService = new ReplicaService<>(ctx);
        this.config = config;
        this.executionService = executionService;
    }

    public Node(NodeConfig config, ReplicaService<ShoppingList> replicaService, CRDTExecutionService<ShoppingList> executionService){
        this.config = config;
        this.replicaService = replicaService;
        this.executionService = executionService;
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

    private void initializeNode() throws IOException, InterruptedException {
        String dataRoot = this.config.nodeId;

        ShoppingListRepository repository = new ShoppingListRepository(dataRoot);
        ReplicaController replicaController = new ReplicaController(repository, replicaService);
        ReplicaRequestHandler replicaRequestHandler = initializeReplicaRequestHandler(replicaController);
        ShoppingListRequestHandler shoppingListRequestHandler = initializeShoppingListRequestHandler(repository, replicaController);
        SharedListRequestHandler sharedListRequestHandler = initializeSharedListRequestHandler(repository, replicaController);
        ShoppingItemRequestHandler shoppingItemRequestHandler = initializeShoppingItemRequestHanlder(repository, replicaController);

        Router router = new Router(
                replicaRequestHandler,
                shoppingListRequestHandler,
                sharedListRequestHandler,
                shoppingItemRequestHandler);
        ServerStub serverStub = new ServerStub(this.config.port, router);
        executionService.run(repository);
        serverStub.boot(this.config.threadNum);
    }

    private static ReplicaRequestHandler initializeReplicaRequestHandler(ReplicaController replicaController) {
        return new ReplicaRequestHandler(replicaController);
    }

    private static ShoppingListRequestHandler initializeShoppingListRequestHandler(
            ShoppingListRepository repository,
            ReplicaController replicaController) {
        ShoppingListController shoppingListController = new ShoppingListController(repository);
        return new ShoppingListRequestHandler(shoppingListController, replicaController);
    }

    private static SharedListRequestHandler initializeSharedListRequestHandler(
            ShoppingListRepository repository,
            ReplicaController replicaController) {
        SharedListController shoppingListController = new SharedListController(repository);
        return new SharedListRequestHandler(shoppingListController, replicaController);
    }

    private static ShoppingItemRequestHandler initializeShoppingItemRequestHanlder(
            ShoppingListRepository repository,
            ReplicaController replicaController) {
        ShoppingItemController shoppingListController = new ShoppingItemController(repository);
        return new ShoppingItemRequestHandler(shoppingListController, replicaController);
    }
}
