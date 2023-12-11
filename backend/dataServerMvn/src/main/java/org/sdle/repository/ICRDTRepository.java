package org.sdle.repository;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

public interface ICRDTRepository<T> {
    CRDT<T> getCRDT(String id);

    CRDT<T> put(T value);

    Cache getCache();

    CRDT<T> putCRDT(CRDT<T> value);

    CRDT<T> remove(String id);
}
