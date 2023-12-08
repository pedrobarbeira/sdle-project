package org.sdle.repository;

import org.sdle.repository.crdt.CRDT;

public interface ICRDTRepository<T> {
    CRDT<T> getCRDT(String id);

    CRDT<T> putCRDt(T value);
}
