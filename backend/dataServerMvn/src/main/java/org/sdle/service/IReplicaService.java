package org.sdle.service;

import org.sdle.repository.crdt.operation.CRDTOp;

public interface IReplicaService <T> {

    void publish(CRDTOp<T> crdtOp);

    void subscribe(String address, CRDTExecutionService<T> executionService);

}
