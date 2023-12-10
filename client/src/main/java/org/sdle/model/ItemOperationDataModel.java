package org.sdle.model;

public class ItemOperationDataModel {
    public String targetId;
    public String itemId;

    public int quantity;

    public ItemOperationDataModel(String targetId){
        this(targetId, null, 0);
    }

    public ItemOperationDataModel(String targetId, String itemId){
        this(targetId, itemId, 0);
    }


    public ItemOperationDataModel(String targetId, String itemId, int quantity){
        this.targetId = targetId;
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
