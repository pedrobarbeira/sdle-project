package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ICRDTRepository;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpListsRemove extends CRDTOp<ShoppingList> {
    public CRDTOpListsRemove(String targetId, CRDT<ShoppingList> value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_LISTS_REMOVE);
    }

    @JsonCreator
    public CRDTOpListsRemove(
            @JsonProperty("targetId") String targetId,
            @JsonProperty("value")CRDT<ShoppingList> value,
            @JsonProperty("version")int version,
            @JsonProperty("timeStamp")Date timeStamp,
            @JsonProperty("type")String type) {
        super(targetId, value, version, timeStamp, type);
    }

    @Override
    public void apply(CRDT<ShoppingList> target, ICRDTRepository<ShoppingList> repository) {
        String id = this.targetId;
        repository.remove(id);
    }
}