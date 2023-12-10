package org.sdle.model;

public class ItemOperationDataModel {
    public String targetId;
    public String itemId;
    public String operation;
    public int quantity;

    public ItemOperationDataModel(String targetId){
        this(targetId, null, null);
    }

    public ItemOperationDataModel(String targetId, String itemId){
        this(targetId, itemId, null);
    }

    public ItemOperationDataModel(String targetId, String itemId, String operation){
        this(targetId, itemId, operation, 0);
    }

    public ItemOperationDataModel(String targetId, String itemId, String operation, int quantity){
        this.targetId = targetId;
        this.itemId = itemId;
        this.operation = operation;
        this.quantity = quantity;
    }
}
