package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sdle.repository.crdt.CRDT;

import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CRDTOpListsCreate.class, name = CRDTOp.OP_LISTS_CREATE),
        @JsonSubTypes.Type(value = CRDTOpListsUpdate.class, name = CRDTOp.OP_LISTS_UPDATE),
        @JsonSubTypes.Type(value = CRDTOpListsRemove.class, name = CRDTOp.OP_LISTS_REMOVE),
        @JsonSubTypes.Type(value = CRDTOpSharedAddUser.class, name = CRDTOp.OP_SHARED_ADD),
        @JsonSubTypes.Type(value = CRDTOpSharedAddUser.class, name = CRDTOp.OP_SHARED_RM),
        @JsonSubTypes.Type(value = CRDTOpItemsCreate.class, name = CRDTOp.OP_ITEMS_CREATE),
        @JsonSubTypes.Type(value = CRDTOpItemsUpdate.class, name = CRDTOp.OP_ITEMS_UPDATE),
        @JsonSubTypes.Type(value = CRDTOpItemsRemove.class, name = CRDTOp.OP_ITEMS_REMOVE),
})
public abstract class CRDTOp<T> {
    public static final String OP_LISTS_CREATE = "op-list-add";
    public static final String OP_LISTS_UPDATE = "op-list-update";
    public static final String OP_LISTS_REMOVE = "op-list-rm";
    public static final String OP_SHARED_ADD = "op-shared-add-user";
    public static final String OP_SHARED_RM = "op-shared-rm-user";
    public static final String OP_ITEMS_CREATE = "op-item-create";
    public static final String OP_ITEMS_UPDATE = "op-item-update";
    public static final String OP_ITEMS_REMOVE = "op-item-rm";

    public String targetId;
    public Object value;
    public int version;
    public String type;
    public Date timeStamp;
    public CRDTOp(){}
    CRDTOp(String targetId, Object value, int version, Date timeStamp, String type) {
        this.targetId = targetId;
        this.value = value;
        this.version = version;
        this.timeStamp = timeStamp;
        this.type = type;
    }
    public abstract void apply(CRDT<T> target);

    public String getTargetId(){
        return this.targetId;
    }

    protected boolean validOp(CRDT<T> target){
        return version > target.getVersion();
    }

}
