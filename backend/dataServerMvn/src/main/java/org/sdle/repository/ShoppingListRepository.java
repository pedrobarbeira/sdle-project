package org.sdle.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.model.ShoppingList;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ShoppingListRepository {

    private String DATA_ROOT;

    HashMap<String, ShoppingList> cache = new HashMap<>();
    ClassLoader loader = ShoppingListRepository.class.getClassLoader();
    ObjectMapper mapper = new ObjectMapper();

    public ShoppingListRepository(String nodeId) {
        this.DATA_ROOT = String.format("data/%s", nodeId);
    }

    private String filePathFromResources(String id){
        String dir = Objects.requireNonNull(loader.getResource(DATA_ROOT)).getPath();
        return String.format("%s/%s.json", dir, id);
    }

    private String buildFilePath(String id){
        return String.format("%s/%s.json", DATA_ROOT, id);
    }

    private void loadFromMemory(String id){
        String path = buildFilePath(id);
        InputStream stream = loader.getResourceAsStream(path);
        if(stream != null){
            try{
                ShoppingList shoppingList = mapper.readValue(stream, ShoppingList.class);
                cache.put(shoppingList.getId(), shoppingList);
            }catch(IOException e){
                System.err.println(e.getMessage());
            }
        }else{
            System.err.printf("File [%s] was not found\n", path);
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
            System.err.println(e.getMessage());
        }
    }

    public ShoppingList getById(String id){
        if(!cache.containsKey(id)){
            loadFromMemory(id);
        }
        return cache.get(id);
    }

    public List<ShoppingList> getAll(){
        File dir = new File(Objects.requireNonNull(loader.getResource(DATA_ROOT)).getPath());
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

    public Map<String, ShoppingList> getAllFromUser(String username){

        URL resourceUrl = loader.getResource(DATA_ROOT);
        if (resourceUrl == null) {
            throw new IllegalStateException("Resource directory not found: " + DATA_ROOT);
        } else {
            System.out.println(resourceUrl);
        }

        File dir = new File(Objects.requireNonNull(loader.getResource(DATA_ROOT)).getPath());
        File[] dirContents = Objects.requireNonNull(dir.listFiles());
        if(dirContents.length > cache.size()){
            for(File file : dirContents){
                String id = file.getName().split("\\.", 2)[0];
                if(!cache.containsKey(id)){
                    loadFromMemory(id);
                }
            }
        }
        Map<String, ShoppingList> toReturn = new HashMap<>();
        for(String key : cache.keySet()){
            if(cache.get(key).getAuthorizedUsers().contains(username)) {
                toReturn.put(key, cache.get(key));
            }
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

    public ShoppingList addAuthorizedUser(String id, String username) {
        ShoppingList s = this.getById(id);
        s.addAuthorizedUser(username);
        return put(s);
    }
}
