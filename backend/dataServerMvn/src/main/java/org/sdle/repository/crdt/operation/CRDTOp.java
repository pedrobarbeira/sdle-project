package org.sdle.repository.crdt.operation;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.LocalDateTime;

public abstract class CRDTOp<T> {
    protected final String targetId;
    protected final Object value;
    protected final String version;
    protected final LocalDateTime timeStamp;
    CRDTOp(String targetId, Object value, String version, LocalDateTime timeStamp) {
        this.targetId = targetId;
        this.value = value;
        this.version = version;
        this.timeStamp = timeStamp;
    }
    public abstract void apply(CRDT<T> target);

    public String getTargetId(){
        return this.targetId;
    }
}
