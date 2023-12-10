package org.sdle.model;

public class ShoppingListDataModel {
    public String id;
    public String name;
    public int itemNo;
    public int sharedNo;

    public ShoppingListDataModel(){}

    public ShoppingListDataModel(String id, String name, int itemNo, int sharedNo){
        this.id = id;
        this.name = name;
        this.itemNo = itemNo;
        this.sharedNo = sharedNo;
    }
}
