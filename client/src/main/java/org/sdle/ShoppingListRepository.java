package org.sdle;

import org.sdle.model.ShoppingList;

import java.util.HashMap;
import java.util.List;

public class ShoppingListRepository {
    private final HashMap<String, ShoppingList> cache;

    public ShoppingListRepository(){
        this.cache = new HashMap<>();
    }

    public String getIdFromName(String name){
        ShoppingList shoppingList = cache.get(name);
        return shoppingList.id;
    }

    public void put(ShoppingList shoppingList){
        String key = shoppingList.name;
        cache.put(key, shoppingList);
    }

    public void put(List<ShoppingList> shoppingLists){
        for(ShoppingList shoppingList : shoppingLists){
            put(shoppingList);
        }
    }

    public void remove(String name){
        cache.remove(name);
    }
}
