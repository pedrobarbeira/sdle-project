package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpSharedRemoveUser extends CRDTOp<ShoppingList>{

    public CRDTOpSharedRemoveUser(String targetId, Object value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_SHARED_RM);
    }

    @JsonCreator
    public CRDTOpSharedRemoveUser(
            @JsonProperty("targetId") String targetId,
            @JsonProperty("value")Object value,
            @JsonProperty("version")int version,
            @JsonProperty("timeStamp")Date timeStamp,
            @JsonProperty("type")String type) {
        super(targetId, value, version, timeStamp, type);
    }

    @Override
    public void apply(CRDT<ShoppingList> target) {
        if(validOp(target)){
            ShoppingList targetList = target.getValue();
            String user = (String) this.value;
            targetList.getAuthorizedUsers().remove(user);
        }
    }
}
