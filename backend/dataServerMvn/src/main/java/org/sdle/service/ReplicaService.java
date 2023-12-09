package org.sdle.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.api.ServerStub;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.sdle.model.ReplicaDataModel;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.repository.crdt.operation.CRDTOp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ReplicaService<T> implements IReplicaService<T> {
    private String dataRoot;

    public ReplicaService(String dataRoot){
        try {
            ServerConfig config = ObjectFactory.getServerConfig();
            this.dataRoot = dataRoot;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void synchronize(ServerStub serverStub, List<String> targets) throws IOException {
        String responseStr = serverStub.requestDataFromReplicas(targets, dataRoot);
        if(responseStr != null) {
            List<ReplicaDataModel> dataList = new ObjectMapper().readValue(responseStr, new TypeReference<>() {
            });
            for (ReplicaDataModel data : dataList) {
                FileStorageWorker worker = new FileStorageWorker(data);
                Thread thread = new Thread(worker);
                thread.start();
            }
        }
    }

    public void publish(CRDTOp<T> crdtOp){
        //sends CRDTOp to replica nodes
    }

    public void listen(){
        //listens for CRDTOps sent from replica nodes
    }

    private void subscribe(String address){

    }

    static class FileStorageWorker implements Runnable{
        private final ReplicaDataModel dataModel;

        public FileStorageWorker(ReplicaDataModel dataModel){
            this.dataModel = dataModel;
        }

        @Override
        public void run() {
            try {
                createDirectoryIfNotExists(dataModel.dataRoot);
                ShoppingListRepository tmpRepository = new ShoppingListRepository(dataModel.dataRoot);
                for (String id : dataModel.deletedIds) {
                    tmpRepository.delete(id);
                }
                for (ShoppingList item : dataModel.shoppingLists.values()) {
                    tmpRepository.putCRDt(item);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        private void createDirectoryIfNotExists(String dataRoot) throws IOException {
            ClassLoader loader = getClass().getClassLoader();
            String path = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
            File file = new File(path);
            if(!file.exists() || !file.isDirectory()){
                Files.createDirectory(Path.of(path));
            }
        }
    }
}
