package org.sdle.repository.crdt.operation;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpSharedAddUser extends CRDTOp<ShoppingList>{

    public CRDTOpSharedAddUser(String targetId, Object value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_SHARED_ADD);
    }

    @Override
    public void apply(CRDT<ShoppingList> target) {
        if(validOp(target)){
            ShoppingList targetList = target.getValue();
            String user = (String) this.value;
            targetList.getAuthorizedUsers().add(user);
        }
    }
}
