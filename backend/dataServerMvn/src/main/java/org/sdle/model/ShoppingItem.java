package org.sdle.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.UUID;

public class ShoppingItem {
    String id;
    String name;

    ItemType type;
    int quantity;

    public ShoppingItem(){
        this(UUID.randomUUID().toString(), "default item", ItemType.NUMERIC, 0);
    };

    public ShoppingItem(String name, ItemType type, int quantity){
        this(UUID.randomUUID().toString(), name, type, quantity);
    }

    public ShoppingItem(String id, String name, ItemType type, int quantity){
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public String getId(){
        return id;
    }

    public String getName(){ return name;}

    public String getType() {
        return type.getType();
    }

    public int getQuantity() { return quantity;}

    public void setQuantity(int newQuantity){
        this.quantity = newQuantity;
    }

    public void setId(String itemId) {
        this.id = itemId;
    }
}
