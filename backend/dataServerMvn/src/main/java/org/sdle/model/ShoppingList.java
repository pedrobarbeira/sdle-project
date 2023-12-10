package org.sdle.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ShoppingList {

    private String id;
    private String primaryNodeId;
    private String name;
    private HashMap<String, ShoppingItem> items;
    private Set<String> authorizedUsers;

    public ShoppingList(){};

    public ShoppingList(String id, String primaryNodeId, String name, HashMap<String, ShoppingItem> items, Set<String> authorizedUsers){
        this.id = id;
        this.primaryNodeId = primaryNodeId;
        this.name = name;
        this.items = items != null ? items : new HashMap<>();
        this.authorizedUsers = authorizedUsers != null ? authorizedUsers : new HashSet<>();
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

    public void setItems(HashMap<String, ShoppingItem> items) {
        this.items = items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthorizedUsers(Set<String> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }

    public Set<String> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void addAuthorizedUser(String username) {
        authorizedUsers.add(username);
    }

    public void setPrimaryNodeId(String primaryNodeId) {
        this.primaryNodeId = primaryNodeId;
    }

    public String getPrimaryNodeId() {
        return primaryNodeId;
    }
}
