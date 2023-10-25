package org.sdle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.model.ShoppingItem;
import org.sdle.model.ShoppingList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingListRepository {
    HashMap<String, ShoppingList> cache = new HashMap<>();
    ClassLoader loader = ShoppingListRepository.class.getClassLoader();
    ObjectMapper mapper = new ObjectMapper();

    public ShoppingList getById(String id){
        if(cache.containsKey(id)) return cache.get(id);
        else{
            try{
                String path = String.format("data/%s.json", id);
                InputStream stream = loader.getResourceAsStream(path);
                ShoppingList shoppingList = mapper.readValue(stream, ShoppingList.class);
                cache.put(shoppingList.getId(), shoppingList);
                return shoppingList;
            }catch(IOException e){
                System.out.println(e);
            }
        }
        return null;
    }

    public List<ShoppingList> getAll(){
        //TODO add code to fetch all shopping items from db
        return new ArrayList<>();
    }

    public ShoppingItem put(ShoppingList item){
        //TODO add code to put item in db
        return null;
    }

    public ShoppingItem put(List<ShoppingList> items){
        //TODO add code to put list of items in db
        return null;
    }

    public ShoppingItem update(ShoppingList item){
        //TODO add code to update shopping item in db;
        return null;
    }

    public void delete(String id){
        //TODO add code to remove item from db
    }

    public void persist(){
        //TODO add code to persist cache into memory
    }

}
