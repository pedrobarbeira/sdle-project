package org.sdle.repository.crdt;

import org.sdle.model.ShoppingList;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class CRDT<T> {
    private T value;
    private int version;
    private Date timeStamp;
    private boolean dirty;

    public CRDT(){}

    public CRDT(T value){
        this(value, 1, Date.from(Instant.now()));
    }

    public CRDT(T value, int version){
        this(value, version, Date.from(Instant.now()));
    }

    public CRDT(T value, int version, Date timeStamp){
        this.value = value;
        this.version = version;
        this.timeStamp = timeStamp;
        this.dirty = true;
    }

    public T getValue(){
        return this.value;
    }

    public int getVersion(){
        return this.version;
    }

    public Date getTimeStamp(){
        return this.timeStamp;
    }

    public boolean isDirty(){return this.dirty;};

    public void setValue(T value){
        this.value = value;
    }

    public void setVersion(int version){
        this.version = version;
    }

    public CRDT<T> incrementVersion(){
        this.version += 1;
        return this;
    }
    public void setTimeStamp(Date timeStamp){
        this.timeStamp = timeStamp;
    }
    public void setDirty(boolean isDirty){
        this.dirty = isDirty;
    }
}

