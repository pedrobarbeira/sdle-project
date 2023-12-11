package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpListsUpdate extends CRDTOp<ShoppingList> {
    public CRDTOpListsUpdate(String targetId, Object value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_LISTS_UPDATE);
    }

    @JsonCreator
    public CRDTOpListsUpdate(
            @JsonProperty("targetId") String targetId,
            @JsonProperty("value")Object value,
            @JsonProperty("version")int version,
            @JsonProperty("timeStamp")Date timeStamp,
            @JsonProperty("type")String type) {
        super(targetId, value, version, timeStamp, type);
    }

    @Override
    public void apply(CRDT<ShoppingList> target) {
        System.out.println("Updating list");
    }
}