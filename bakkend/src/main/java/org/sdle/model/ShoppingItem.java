package org.sdle.model;

import java.util.UUID;

public class ShoppingItem {
    String id = UUID.randomUUID().toString();;
    String name;
    int quantity;

    public ShoppingItem(){};

    public ShoppingItem(String name, int quantity){
        this.name = name;
        this.quantity = quantity;
    }

    public ShoppingItem(String id, String name, int quantity){
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId(){
        return id;
    }

    public String getName(){ return name;}

    public int getQuantity() { return quantity;}

    public void setQuantity(int newQuantity){
        this.quantity = newQuantity;
    }
}
