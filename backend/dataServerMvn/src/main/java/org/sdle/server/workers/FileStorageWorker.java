package org.sdle.server.workers;

import org.sdle.model.ReplicaDataModel;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileStorageWorker implements Runnable{
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