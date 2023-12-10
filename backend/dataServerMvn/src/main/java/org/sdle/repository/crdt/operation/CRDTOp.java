package org.sdle.repository.crdt.operation;

import org.sdle.repository.crdt.CRDT;

import java.util.Date;

public abstract class CRDTOp<T> {
    public static final String OP_SHARED_ADD = "op-shared-add-user";
    public static final String OP_SHARED_RM = "op-shared-rm-user";
    public static final String OP_LIST_CREATE = "op-list-add";
    public static final String OP_LIST_UPDATE = "op-list-update";
    public static final String OP_LIST_RM = "op-list-rm";
    public static final String OP_ITEM_CREATE = "op-item-create";
    public static final String OP_ITEM_UPDATE = "op-item-update";
    public static final String OP_ITEM_RM = "op-item-rm";

    protected final String targetId;
    protected String type;
    protected final Object value;
    protected final int version;
    protected final Date timeStamp;
    CRDTOp(String targetId, Object value, int version, Date timeStamp, String type) {
        this.targetId = targetId;
        this.value = value;
        this.version = version;
        this.timeStamp = timeStamp;
    }
    public abstract void apply(CRDT<T> target);

    public String getTargetId(){
        return this.targetId;
    }

    protected boolean validOp(CRDT<T> target){
        return version > target.getVersion();
    }
}
