package org.sdle.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.sdle.api.ServerStub;
import org.sdle.config.NodeConfig;
import org.sdle.repository.crdt.operation.CRDTOp;

import java.io.IOException;
import java.util.List;

public interface IReplicaService <T> {

    void synchronize(ServerStub serverStub, List<String> targets) throws IOException;
    void publish(CRDTOp<T> crdtOp);

    void listen();
}
