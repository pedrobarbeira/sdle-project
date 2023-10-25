package org.sdle.model;

import java.util.HashMap;
import java.util.UUID;

public class ShoppingList {

    private String id;
    private String name;
    private HashMap<String, ShoppingItem> items;

    public ShoppingList(){};
    public ShoppingList(String id, String name, HashMap<String, ShoppingItem> items){
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public String getId(){
        return id;
    }

    public String getName(){return this.name;}

    public HashMap<String, ShoppingItem> getItems(){
        return items;
    }

    public ShoppingItem getById(String id){
        return items.get(id);
    }

    public boolean putItem(ShoppingItem item){
        String itemId = item.getId();
        if(!items.containsKey(itemId)){
            items.put(itemId, item);
            return true;
        }
        return false;
    }
}
