package org.sdle.model;

import java.util.*;

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

    public List<ShoppingItem> getItems(){
        return new ArrayList<>(items.values());
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

    public void setItems(List<ShoppingItem> itemList) {
        items = new HashMap<>();

        for (ShoppingItem item : itemList) {
            items.put(item.getId(), item);
        }
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
