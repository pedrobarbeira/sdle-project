package org.sdle.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.sdle.config.ServerConfig;
import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;
import org.sdle.server.ObjectFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class ShoppingListRepository implements IShoppingListRepository, ICRDTRepository<ShoppingList> {

    public static final String DATA_DIR = "data";
    private Cache cache;
    private ClassLoader loader;
    private final ObjectMapper mapper = new ObjectMapper();

    public ShoppingListRepository(String nodeId) {
        this(nodeId, new HashMap<>());
    }

    public ShoppingListRepository(String nodeId, HashMap<String, CRDT<ShoppingList>> data){
        this(nodeId, data, ShoppingListRepository.class.getClassLoader());
    }

    public ShoppingListRepository(String nodeId, HashMap<String, CRDT<ShoppingList>> data, ClassLoader loader){
        try {
            ServerConfig serverConfig = ObjectFactory.getServerConfig();
            String dataRoot = serverConfig.dataRoot;
            String dataPath = String.format("%s/%s/s", DATA_DIR, dataRoot, nodeId);
            this.cache = new Cache(dataPath, data);
            this.loader = loader;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private String filePathFromResources(String id){
        String dataRoot = this.cache.getDataRoot();
        String dir = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
        return String.format("%s/%s.json", dir, id);
    }

    private String buildFilePath(String id){
        String dataRoot = this.cache.getDataRoot();
        return String.format("%s/%s.json", dataRoot, id);
    }

    private void loadFromMemory(String id){
        String path = buildFilePath(id);
        InputStream stream = loader.getResourceAsStream(path);
        if(stream != null){
            try{
                TypeFactory typeFactory = mapper.getTypeFactory();
                JavaType typeReference = typeFactory.constructParametricType(CRDT.class, ShoppingList.class);
                CRDT<ShoppingList> shoppingList = mapper.readValue(stream, typeReference);
                String shoppingListId = shoppingList.getValue().getId();
                cache.put(shoppingListId, shoppingList);
            }catch(IOException e){
                System.err.println(e.getMessage());
            }
        }else{
            System.err.printf("File [%s] was not found\n", path);
        }
    }

    private void writeToMemory(CRDT<ShoppingList> item){
        ShoppingList shoppingList = item.getValue();
        File file = new File(filePathFromResources(shoppingList.getId()));
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
        CRDT<ShoppingList> shoppingList = getCRDT(id);
        if(shoppingList != null) {
            return shoppingList.getValue();
        }
        return null;
    }

    public List<ShoppingList> getAll(){
        String dataRoot = this.cache.getDataRoot();
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
        for(CRDT<ShoppingList> crdt : cache.getValues()){
            toReturn.add(crdt.getValue());
        }
        return toReturn;
    }

    public List<ShoppingList> getAllFromUser(String username){
        String dataRoot = this.cache.getDataRoot();
        URL resourceUrl = loader.getResource(dataRoot);
        if (resourceUrl == null) {
            throw new IllegalStateException("Resource directory not found: " + dataRoot);
        }
        List<ShoppingList> allLists = getAll();
        List<ShoppingList> toReturn = new ArrayList<>();
        for(ShoppingList list : allLists){
            if(list.getAuthorizedUsers() != null && list.getAuthorizedUsers().contains(username)) {
                toReturn.add(list);
            }
        }

        return toReturn;
    }

    public ShoppingList put(ShoppingList item){
        return putCRDt(item).getValue();
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
        ShoppingList shoppingList = this.getById(id);
        shoppingList.addAuthorizedUser(username);
        return put(shoppingList);
    }

    public Set<String> getAuthorizedUsers(String id){
        ShoppingList shoppingList = this.getById(id);
        return shoppingList.getAuthorizedUsers();
    }

    @Override
    public CRDT<ShoppingList> getCRDT(String id) {
        if(!cache.containsKey(id)){
            loadFromMemory(id);
        }
        return cache.get(id);
    }

    @Override
    public CRDT<ShoppingList> putCRDt(ShoppingList value) {
        UUID uuid = UUID.randomUUID();
        CRDT<ShoppingList> shoppingList = new CRDT<>(value, uuid.toString(), Date.from(Instant.now()));
        cache.put(value.getId(), shoppingList);
        writeToMemory(shoppingList);
        return shoppingList;
    }

    @Override
    public Cache getCache(){
        getAll();
        return this.cache;
    }
}
