package org.sdle.models;

import java.util.HashMap;
import java.util.UUID;

public class ShoppingList {

    private HashMap<String, ShoppingItem> itemMap;
    private String id;

    public ShoppingList(){
        itemMap = new HashMap<>();
        id = UUID.randomUUID().toString();
    }

    public String getId(){
        return id;
    }

    public HashMap<String, ShoppingItem> getItem(){
        return itemMap;
    }

    public ShoppingItem getById(String id){
        return itemMap.get(id);
    }

    public boolean putItem(ShoppingItem item){
        String itemId = item.getId();
        if(!itemMap.containsKey(itemId)){
            itemMap.put(itemId, item);
            return true;
        }
        return false;
    }
}
