package org.sdle;

import org.sdle.model.ShoppingListDataModel;

import java.util.HashMap;
import java.util.List;

public class ShoppingListRepository {
    private final HashMap<String, ShoppingListDataModel> cache;

    public ShoppingListRepository(){
        this.cache = new HashMap<>();
    }

    public String getIdFromName(String name){
        ShoppingListDataModel shoppingList = cache.get(name);
        return shoppingList.id;
    }

    public void put(ShoppingListDataModel shoppingList){
        String key = shoppingList.name;
        cache.put(key, shoppingList);
    }

    public void put(List<ShoppingListDataModel> shoppingLists){
        for(ShoppingListDataModel shoppingList : shoppingLists){
            put(shoppingList);
        }
    }

    public void remove(String name){
        cache.remove(name);
    }
}
