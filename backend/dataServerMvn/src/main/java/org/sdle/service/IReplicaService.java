package org.sdle.service;

import org.sdle.repository.crdt.operation.CRDTOp;

public interface IReplicaService <T> {

    void synchronize();
    void publish(CRDTOp<T> crdtOp);

    void listen();
}
