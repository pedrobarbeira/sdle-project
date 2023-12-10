package org.sdle.model.domain;

public class SharedOperationDataModel {
    public String targetId;
    public String username;

    public SharedOperationDataModel(){}

    public SharedOperationDataModel(String targetId, String username){
        this.targetId = targetId;
        this.username = username;
    }
}
