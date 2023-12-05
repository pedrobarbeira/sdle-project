package org.sdle.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.UUID;

public class ShoppingItem {
    String id;
    String name;
    boolean checked;
    int quantity;

    public ShoppingItem(){
        this(UUID.randomUUID().toString(), "default item", false, 0);
    };

    public ShoppingItem(String name, boolean checked, int quantity){
        this(UUID.randomUUID().toString(), name, checked, quantity);
    }

    public ShoppingItem(String id, String name, boolean checked, int quantity){
        this.id = id;
        this.name = name;
        this.checked = checked;
        this.quantity = quantity;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){ return this.name;}

    public boolean isChecked() {
        return this.checked;
    }

    public int getQuantity() { return this.quantity;}

    public void setQuantity(int newQuantity){
        this.quantity = newQuantity;
    }

    public void setId(String itemId) {
        this.id = itemId;
    }
}
