package org.sdle.repository;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.util.HashMap;

public interface ICRDTRepository<T> {
    CRDT<T> getCRDT(String id);

    CRDT<T> putCRDt(T value);

    Cache getCache();
}
