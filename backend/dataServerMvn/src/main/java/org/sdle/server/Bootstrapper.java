package org.sdle.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.sdle.model.domain.ReplicaDataModel;
import org.sdle.model.ShoppingList;
import org.sdle.repository.Cache;
import org.sdle.repository.crdt.CRDT;
import org.sdle.server.workers.DataFetchWorker;
import org.sdle.server.workers.FileStorageWorker;
import org.sdle.service.CRDTExecutionService;
import org.sdle.service.ReplicaService;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Bootstrapper {

    private final ServerConfig config;
    private final ZContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, NodeConfig> nodeMap;
    private boolean booted;
    private ReplicaService<ShoppingList> replicaService;
    private HashMap<String, CRDTExecutionService<ShoppingList>> executionServiceMap;

    public Bootstrapper() throws IOException {
        executionServiceMap = new HashMap<>();
        this.config = ObjectFactory.getServerConfig();
        this.nodeMap = config.nodeMap;
        this.ctx = new ZContext();
        this.booted = false;
    }

    public void bootServer() {
        try {
            initializeServices();
            getData();
            bootNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeServices(){
        System.out.println("Initializing services");
        int timeOut = this.config.timeOut;
        String mainNodeId = this.config.mainNodeId;

        NodeConfig nodeConfig = this.nodeMap.get(mainNodeId);
        List<String> replicates = nodeConfig.replicates;

        this.executionServiceMap.put(mainNodeId, new CRDTExecutionService<>(timeOut));
        for(String id : replicates){
            this.executionServiceMap.put(id, new CRDTExecutionService<>(timeOut));
        }
        this.replicaService = new ReplicaService<>(this.ctx);
    }

    private void getData() throws ExecutionException, IOException, InterruptedException {
        System.out.println("Fetching data");
        String dataRoot = this.config.mainNodeId;
        NodeConfig nodeConfig = this.nodeMap.get(dataRoot);

        List<String> tmpListeners = ObjectFactory.getServerConfig().tmpListeners;
        CompletableFuture<Void> asyncSynchronize = CompletableFuture.runAsync(()-> {
            try {
                synchronizeMainData(tmpListeners);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        List<String> mainNodeReplicates = nodeConfig.replicates;
        synchronizeReplicatesData(mainNodeReplicates);

        asyncSynchronize.get();
    }

    private void synchronizeMainData(List<String> targets) throws ExecutionException, JsonProcessingException, InterruptedException {
        System.out.println("Synchronizing main node data");
        CompletableFuture.runAsync(() -> openTmpListeners(targets));
        getNodeData(targets);
    }

    private void openTmpListeners(List<String> targets){
        System.out.println("Opening temporary listeners");
        HashMap<String, String> addressMap = this.config.addressMap;
        for(String target : targets) {
            CRDTExecutionService<ShoppingList> crdtExecutionService = this.executionServiceMap.get(target);
            String address = addressMap.get(target);
            CompletableFuture.runAsync(() -> replicaService.startTmpListener(address, crdtExecutionService));
        }
        while(!this.booted);
        replicaService.stopTmpListeners();
    }

    private void synchronizeReplicatesData(List<String> targets) throws ExecutionException, JsonProcessingException, InterruptedException {
        System.out.println("Synchronizing replicates data");
        openNodeListeners(targets);
        getNodeData(targets);
    }

    private void openNodeListeners(List<String> targets){
        System.out.println("Opening node listeners");
        HashMap<String, String> addressMap = this.config.addressMap;
        for(String target : targets){
            System.out.printf("Opening node listener on [%s]%n", target);
            CRDTExecutionService<ShoppingList> crdtExecutionService = executionServiceMap.get(target);
            String address = addressMap.get(target);
            replicaService.subscribe(address, crdtExecutionService);
        }
    }

    private void getNodeData(List<String> targets) throws ExecutionException, JsonProcessingException, InterruptedException {
        System.out.println("Fetching node data");
        List<DataFetchWorker> workers = new ArrayList<>();
        for(String target: targets){
            workers.add(new DataFetchWorker(this.ctx, this.config, target));
        }
        List<ReplicaDataModel> unmergedData = executeFetchWorkers(workers);
        mergeAndStoreData(unmergedData);
    }

    private List<ReplicaDataModel> executeFetchWorkers(List<DataFetchWorker> workers) throws JsonProcessingException, ExecutionException, InterruptedException {
        System.out.println("Executing fetch workers");
        List<Future<String>> futures = new ArrayList<>();
        ExecutorService executorService = ObjectFactory.getExecutorService();
        for(DataFetchWorker worker : workers){
            futures.add(executorService.submit(worker));
        }
        List<ReplicaDataModel> unmergedData = new ArrayList<>();
        for(Future<String> future : futures){
            String rawData = future.get();
            ReplicaDataModel data = this.mapper.readValue(rawData, ReplicaDataModel.class);
            unmergedData.add(data);
        }
        return unmergedData;
    }

    private void mergeAndStoreData(List<ReplicaDataModel> unmergedData) throws InterruptedException {
        ReplicaDataModel baseDataModel = unmergedData.remove(0);
        Cache baseData = baseDataModel.data;
        for(ReplicaDataModel repositoryDataModel : unmergedData){
            Cache repositoryData = repositoryDataModel.data;
            mergeRepositoryData(baseData, repositoryData);
        }
        baseDataModel.data = baseData;
        storeData(baseDataModel);
    }

    private void mergeRepositoryData(Cache baseData, Cache toMerge){
        System.out.println("Merging data");
        String dataRoot = this.config.mainNodeId;
        CRDTExecutionService<ShoppingList> executionService = this.executionServiceMap.get(dataRoot);
        for(CRDT<ShoppingList> crdt : toMerge.getValues()){
            ShoppingList shoppingList = crdt.getValue();
            if(!baseData.containsKey(shoppingList.getId())){
                baseData.put(shoppingList.getId(), crdt);
                continue;
            }
            CRDT<ShoppingList> current = baseData.get(shoppingList.getId());
            CRDT<ShoppingList> result = executionService.mergeCRDT(current, crdt);
            baseData.put(shoppingList.getId(), result);
        }
    }

    private void storeData(ReplicaDataModel data) throws InterruptedException {
        System.out.println("Storing data");
        FileStorageWorker worker = new FileStorageWorker(data);
        Thread thread = new Thread(worker);
        thread.start();
        thread.join();
    }

    private void bootNodes() {
        System.out.println("Booting nodes");
        String mainNodeId = this.config.mainNodeId;
        NodeConfig nodeConfig = this.nodeMap.get(mainNodeId);
        bootMainNode(nodeConfig);
        bootSecondaryNodes(nodeConfig);
        this.booted = true;
    }

    private void bootMainNode(NodeConfig nodeConfig){
        System.out.println("Booting main node");
        String nodeId = nodeConfig.nodeId;
        CRDTExecutionService<ShoppingList> executionService = executionServiceMap.get(nodeId);
        Node mainNode = new Node(nodeConfig, this.replicaService, executionService);
        mainNode.start();
    }
    private void bootSecondaryNodes(NodeConfig mainNodeConfig){
        List<String> replicates = mainNodeConfig.replicates;
        List<NodeConfig> nodeConfigs = new ArrayList<>();
        for(String id : replicates){
            NodeConfig config = this.nodeMap.get(id);
            nodeConfigs.add(config);
        }
        for(NodeConfig config : nodeConfigs){
            String nodeId = config.nodeId;
            System.out.printf("Booting seconary node %s%n", nodeId);
            CRDTExecutionService<ShoppingList> executionService = executionServiceMap.get(nodeId);
            Node node = new Node(config, executionService);
            node.start();
        }
    }
}
