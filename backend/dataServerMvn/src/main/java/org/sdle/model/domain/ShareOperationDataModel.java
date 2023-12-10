package org.sdle.model.domain;

public class ShareOperationDataModel {
    public String targetId;
    public String username;

    public ShareOperationDataModel(){}

    public ShareOperationDataModel(String targetId, String username){
        this.targetId = targetId;
        this.username = username;
    }
}
