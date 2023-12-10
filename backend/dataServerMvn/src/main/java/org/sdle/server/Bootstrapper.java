package org.sdle.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.sdle.model.ReplicaDataModel;
import org.sdle.model.ShoppingList;
import org.sdle.repository.Cache;
import org.sdle.repository.crdt.CRDT;
import org.sdle.server.workers.DataFetchWorker;
import org.sdle.server.workers.FileStorageWorker;
import org.sdle.service.CRDTExecutionService;
import org.sdle.service.ReplicaService;
import org.zeromq.ZContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class Bootstrapper {

    private final ServerConfig config;
    private final ZContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, NodeConfig> nodeMap;
    private boolean booted;
    private ReplicaService<ShoppingList> replicaService;
    private HashMap<String, CRDTExecutionService<ShoppingList>> executionServiceMap;

    public Bootstrapper(ServerConfig config){
        this.config = config;
        this.nodeMap = config.nodeMap;
        this.ctx = new ZContext();
        this.booted = true;
    }

    public void bootServer(){
        try {
            initializeServices();
            if(isNewServer()) {
                getData();
            }
            bootNodes();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeServices(){
        int timeOut = this.config.timeOut;
        String dataRoot = this.config.mainNodeId;

        NodeConfig nodeConfig = this.nodeMap.get(dataRoot);
        List<String> replicates = nodeConfig.replicates;

        this.executionServiceMap.put(dataRoot, new CRDTExecutionService<>(timeOut));
        for(String id : replicates){
            this.executionServiceMap.put(id, new CRDTExecutionService<>(timeOut));
        }
        this.replicaService = new ReplicaService<>(this.ctx);
    }
    private boolean isNewServer(){
        String dataRoot = this.config.mainNodeId;
        ClassLoader loader = getClass().getClassLoader();
        String path = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
        File file = new File(path);
        return file.exists();
    }

    private void getData() throws ExecutionException, JsonProcessingException, InterruptedException {
        String dataRoot = this.config.mainNodeId;
        NodeConfig nodeConfig = this.nodeMap.get(dataRoot);

        List<String> mainNodeReplicas = nodeConfig.replicatedOn;
        CompletableFuture<Void> asyncSynchronize = CompletableFuture.runAsync(()-> {
            try {
                synchronizeMainData(mainNodeReplicas);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        List<String> mainNodeReplicates = nodeConfig.replicates;
        synchronizeReplicatesData(mainNodeReplicates);

        asyncSynchronize.get();
    }

    private void synchronizeMainData(List<String> targets) throws ExecutionException, JsonProcessingException, InterruptedException {
        List<NodeConfig> replicasConfig = new ArrayList<>();
        for(String id : targets){
            replicasConfig.add(this.nodeMap.get(id));
        }
        CompletableFuture.runAsync(() -> openTmpListeners(targets));
        getNodeData(replicasConfig);
    }

    private void openTmpListeners(List<String> targets){
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
        List<NodeConfig> replicatesConfig = new ArrayList<>();
        for(String id : targets){
            replicatesConfig.add(this.nodeMap.get(id));
        }
        openNodeListeners(targets);
        getNodeData(replicatesConfig);
    }

    private void openNodeListeners(List<String> targets){
        HashMap<String, String> addressMap = this.config.addressMap;
        for(String target : targets){
            CRDTExecutionService<ShoppingList> crdtExecutionService = executionServiceMap.get(target);
            String address = addressMap.get(target);
            replicaService.subscribe(address, crdtExecutionService);
        }
    }

    private void getNodeData(List<NodeConfig> targets) throws ExecutionException, JsonProcessingException, InterruptedException {
        List<DataFetchWorker> workers = new ArrayList<>();
        for(NodeConfig target: targets){
            workers.add(new DataFetchWorker(this.ctx, target, config.mainNodeId, config.apiBase));
        }
        List<ReplicaDataModel> unmergedData = executeFetchWorkers(workers);
        mergeAndStoreData(unmergedData);
    }

    private List<ReplicaDataModel> executeFetchWorkers(List<DataFetchWorker> workers) throws JsonProcessingException, ExecutionException, InterruptedException {
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
        FileStorageWorker worker = new FileStorageWorker(data);
        Thread thread = new Thread(worker);
        thread.start();
        thread.join();
    }

    private void bootNodes() {
        String dataRoot = this.config.mainNodeId;
        NodeConfig nodeConfig = this.nodeMap.get(dataRoot);
        bootMainNode(nodeConfig);
        bootSecondaryNodes(nodeConfig);
        this.booted = true;
    }

    private void bootMainNode(NodeConfig nodeConfig){
        Node mainNode = new Node(nodeConfig, this.replicaService);
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
            Node node = new Node(config);
            node.start();
        }
    }
}
