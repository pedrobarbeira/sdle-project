package org.sdle.service;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.operation.CRDTOp;

public class ReplicaService<T> implements IReplicaService<T> {
    ICRDTExecutionService<T> crdtExecutionService;

    public ReplicaService(ICRDTExecutionService<T> crdtExecutionService){
        this.crdtExecutionService = crdtExecutionService;
    }

    public void synchronize(){
        //synchronizes with replica nodes
    }

    public void publish(CRDTOp<T> crdtOp){
        //sends CRDTOp to replica nodes
    }

    public void listen(){
        //listens for CRDTOps sent from replica nodes
    }

    private void subscribe(String address){

    }
}
