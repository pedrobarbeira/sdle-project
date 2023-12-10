package org.sdle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.model.ShoppingListDataModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ShoppingListRepository {
    private static final String DATA_DIR = "data/";
    private String subDir;
    private final HashMap<String, ShoppingListDataModel> cache;
    private final ObjectMapper mapper = ObjectFactory.getMapper();

    public ShoppingListRepository(){
        this.cache = new HashMap<>();
    }

    public String getIdFromName(String name){
        ShoppingListDataModel shoppingList = cache.get(name);
        return shoppingList.id;
    }

    public void put(ShoppingListDataModel shoppingList){
        String key = shoppingList.name;
        if(cache.containsKey(key)) {
            ShoppingListDataModel cachedModel = cache.get(key);

            if (shoppingList.timeStamp.after(cachedModel.timeStamp)) {
                cache.put(key, shoppingList);
                writeToMemory(shoppingList);
            }
        } else {
            cache.put(key, shoppingList);
        }

        writeToMemory(cache.get(key));
    }

    public void put(List<ShoppingListDataModel> shoppingLists){
        for(ShoppingListDataModel shoppingList : shoppingLists){
            put(shoppingList);
        }
    }

    public void remove(String name){
        cache.remove(name);
        removeFromMemory(name);
    }

    public void writeToMemory(ShoppingListDataModel shoppingList) {
        ClassLoader loader = ShoppingListRepository.class.getClassLoader();
        String dirPath = Objects.requireNonNull(loader.getResource(DATA_DIR)).getPath();

        String userDirPath = dirPath + "/" + this.subDir;
        File userDir = new File(userDirPath);
        try {
            if (!userDir.exists()) {
                boolean created = userDir.mkdirs();
                if (!created) {
                        throw new IOException("Failed to create directory: " + userDirPath);
                }
            }

            String filePath = userDirPath + "/" + shoppingList.name;
            File file = new File(filePath);

            OutputStream os = new FileOutputStream(file);
            String jsonData = mapper.writeValueAsString(shoppingList);
            os.write(jsonData.getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFromMemory(String name) {
        ClassLoader loader = ShoppingListRepository.class.getClassLoader();
        String dirPath = Objects.requireNonNull(loader.getResource(DATA_DIR)).getPath();

        String userDirPath = dirPath + "/" + this.subDir;
        File userDir = new File(userDirPath);
        try {
            if (!userDir.exists()) {
                boolean created = userDir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + userDirPath);
                }
            }

            String filePath = userDirPath + "/" + name;
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new IOException("Failed to delete file: " + filePath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSubDir(String subDir) {
        this.subDir = subDir;
    }
}
