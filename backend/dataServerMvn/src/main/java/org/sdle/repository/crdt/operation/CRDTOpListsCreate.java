package org.sdle.repository.crdt.operation;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpListsCreate extends CRDTOp<ShoppingList> {
    public CRDTOpListsCreate(String targetId, Object value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_LIST_CREATE);
    }

    @Override
    public void apply(CRDT<ShoppingList> target) {

    }
}