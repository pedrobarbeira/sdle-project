package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.*;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ICRDTRepository;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpItemsCreate extends CRDTOp<ShoppingList> {

    public CRDTOpItemsCreate(String targetId, CRDT<ShoppingList> value, int version) {
        this(targetId, value, version, Date.from(Instant.now()), OP_ITEMS_CREATE);
    }

    @JsonCreator
    public CRDTOpItemsCreate(
            @JsonProperty("targetId") String targetId,
            @JsonProperty("value")CRDT<ShoppingList> value,
            @JsonProperty("version")int version,
            @JsonProperty("timeStamp")Date timeStamp,
            @JsonProperty("type")String type) {
        super(targetId, value, version, timeStamp, type);
    }

    @Override
    public void apply(CRDT<ShoppingList> target, ICRDTRepository<ShoppingList> repository) {
        System.out.println("Creating item");
    }
}