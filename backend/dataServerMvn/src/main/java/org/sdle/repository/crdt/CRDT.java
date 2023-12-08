package org.sdle.repository.crdt;

import java.time.LocalDateTime;
import java.util.UUID;

public class CRDT<T> {
    private T value;
    private String version;
    private LocalDateTime timeStamp;

    public CRDT(T value){
        this(value, UUID.randomUUID().toString(), LocalDateTime.now());
    }

    public CRDT(T value, String version){
        this(value, version, LocalDateTime.now());
    }

    public CRDT(T value, String version, LocalDateTime timeStamp){
        this.value = value;
        this.version = version;
        this.timeStamp = timeStamp;
    }

    public T getValue(){
        return this.value;
    }

    public String getVersion(){
        return this.version;
    }

    public LocalDateTime getTimeStamp(){
        return this.timeStamp;
    }

    public void setValue(T value){
        this.value = value;
    }

    public void setVersion(String version){
        this.version = version;
    }

    public void setTimeStamp(LocalDateTime timeStamp){
        this.timeStamp = timeStamp;
    }
}

