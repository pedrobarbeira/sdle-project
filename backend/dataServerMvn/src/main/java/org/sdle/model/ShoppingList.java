package org.sdle.model;

import java.util.*;

public class ShoppingList {

    private String id;
    private String name;
    private HashMap<String, ShoppingItem> items;

    private Set<String> authorizedUsers;

    public ShoppingList(){
        this(UUID.randomUUID().toString(), "Default Name", new HashMap<>(), new HashSet<>());
    };

    public ShoppingList(String name){
        this(UUID.randomUUID().toString(), name, new HashMap<>(), new HashSet<>());
    }
    public ShoppingList(String id, String name, HashMap<String, ShoppingItem> items, Set<String> authorizedUsers){
        this.id = id;
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

    public Set<String> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void addAuthorizedUser(String username) {
        authorizedUsers.add(username);
    }
}
