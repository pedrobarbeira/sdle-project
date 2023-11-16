package org.sdle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.model.ShoppingList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ShoppingListRepository {
    //TODO separate cache logic from DAO layer
    private final String dataRoot;

    HashMap<String, ShoppingList> cache = new HashMap<>();
    ClassLoader loader = ShoppingListRepository.class.getClassLoader();
    ObjectMapper mapper = ObjectFactory.getObjectMapper();

    public ShoppingListRepository(String dataRoot){
        this.dataRoot = dataRoot;
    }

    private String filePathFromResources(String id){
        String dir = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
        return String.format("%s/%s.json", dir, id);
    }

    private String buildFilePath(String id){
        return String.format("%s/%s.json", dataRoot, id);
    }

    private void loadFromMemory(String id){
        String path = buildFilePath(id);
        InputStream stream = loader.getResourceAsStream(path);
        if(stream != null){
            try{
                ShoppingList shoppingList = mapper.readValue(stream, ShoppingList.class);
                cache.put(shoppingList.getId(), shoppingList);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }else{
            System.out.printf("File [%s] was not found\n", path);
        }
    }

    private void writeToMemory(ShoppingList item){
        File file = new File(filePathFromResources(item.getId()));
        try{
            if(!file.exists()) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            String jsonData = mapper.writeValueAsString(item);
            os.write(jsonData.getBytes(StandardCharsets.UTF_8));
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public ShoppingList getById(String id){
        if(!cache.containsKey(id)){
            loadFromMemory(id);
        }
        return cache.get(id);
    }

    public List<ShoppingList> getAll(){
        File dir = new File(Objects.requireNonNull(loader.getResource(dataRoot)).getPath());
        File[] dirContents = Objects.requireNonNull(dir.listFiles());
        if(dirContents.length > cache.size()){
            for(File file : dirContents){
                String id = file.getName().split("\\.", 2)[0];
                if(!cache.containsKey(id)){
                    loadFromMemory(id);
                }
            }
        }
        List<ShoppingList> toReturn = new ArrayList<>();
        for(String key : cache.keySet()){
            toReturn.add(cache.get(key));
        }
        return toReturn;
    }

    public ShoppingList put(ShoppingList item){
        cache.put(item.getId(), item);
        writeToMemory(item);
        return item;
    }

    public List<ShoppingList> put(List<ShoppingList> items){
        for(ShoppingList item : items){
            put(item);
        }
        return items;
    }

    public ShoppingList update(ShoppingList item){
        if(cache.containsKey(item.getId())){
            return put(item);
        }
        String path = buildFilePath(item.getId());
        if(loader.getResource(path) != null){
            return put(item);
        }
        return null;
    }

    public boolean delete(String id){
        cache.remove(id);
        File file = new File(filePathFromResources(id));
        if(file.exists()){
            return file.delete();
        }
        return false;
    }
}
