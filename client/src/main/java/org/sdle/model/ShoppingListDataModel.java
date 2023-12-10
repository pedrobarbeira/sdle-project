package org.sdle.model;

import java.util.Date;

public class ShoppingListDataModel {
    public String id;
    public String name;
    public int itemNo;
    public int sharedNo;
    public Date timeStamp;

    public ShoppingListDataModel(){
        this.timeStamp = new Date();
    }

    public ShoppingListDataModel(String id, String name, int itemNo, int sharedNo, Date timeStamp){
        this.id = id;
        this.name = name;
        this.itemNo = itemNo;
        this.sharedNo = sharedNo;
        this.timeStamp = timeStamp;
    }
}
