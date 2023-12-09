package org.sdle.repository;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.util.Collection;
import java.util.HashMap;

public class Cache {
    private String dataRoot;
    private HashMap<String, CRDT<ShoppingList>> data;

    public Cache(String dataRoot){
        this(dataRoot, new HashMap<>());
    }

    public Cache(String dataRoot, HashMap<String, CRDT<ShoppingList>> data){
        this.dataRoot = dataRoot;
        this.data = data;
    }

    public String getDataRoot(){
        return this.dataRoot;
    }

    public void setDataRoot(String dataRoot){
        this.dataRoot = dataRoot;
    }

    public HashMap<String, CRDT<ShoppingList>> getData(){
        return this.data;
    }

    public void setData(HashMap<String, CRDT<ShoppingList>> data){
        this.data = data;
    }

    public CRDT<ShoppingList> put(String id, CRDT<ShoppingList> item){
        return data.put(id, item);
    }

    public int size(){
        return data.size();
    }

    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    public Collection<CRDT<ShoppingList>> getValues(){
        return data.values();
    }

    public CRDT<ShoppingList> remove(String key){
        return data.remove(key);
    }

    public CRDT<ShoppingList> get(String key){
        return data.get(key);
    }
}
