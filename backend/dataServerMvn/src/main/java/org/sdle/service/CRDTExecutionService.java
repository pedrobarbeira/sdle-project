package org.sdle.service;

import org.sdle.repository.ICRDTRepository;
import org.sdle.repository.crdt.CRDT;
import org.sdle.repository.crdt.operation.CRDTOp;

import java.util.Queue;

public class CRDTExecutionService<T> implements ICRDTExecutionService<T>{
    private ICRDTRepository<T> repository;
    private final Queue<CRDTOp<T>> operationQueue;
    private final int timeOut;

    public CRDTExecutionService(Queue<CRDTOp<T>> operationQueue, int timeOut) {
        this.operationQueue = operationQueue;
        this.timeOut = timeOut;
    }

    @Override
    public void run(ICRDTRepository<T> repository) throws InterruptedException {
        this.repository = repository;
        while(true){
            if(this.operationQueue.isEmpty()){
                wait(this.timeOut);
            }else{
                CRDTOp<T> operation = operationQueue.remove();
                CRDT<T> target = repository.getCRDT(operation.getTargetId());
                operation.apply(target);
            }
        }
    }

    void addOperation(CRDTOp<T> operation){
        this.operationQueue.add(operation);
    }
}
