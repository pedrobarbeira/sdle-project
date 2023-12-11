package org.sdle.model.domain;

import java.util.Date;

public class ListOperationDataModel {
    public String id;
    public String name;
    public int itemNo;
    public int sharedNo;
    public Date timeStamp;
    public int version;

    public ListOperationDataModel(){}

    public ListOperationDataModel(String id, String name, int itemNo, int sharedNo, Date timeStamp, int version){
        this.id = id;
        this.name = name;
        this.itemNo = itemNo;
        this.sharedNo = sharedNo;
        this.timeStamp = timeStamp;
        this.version = version;
    }
}
