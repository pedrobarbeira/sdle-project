package org.sdle.repository.crdt.operation;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpSharedRemoveUser extends CRDTOp<ShoppingList>{

    public CRDTOpSharedRemoveUser(String targetId, Object value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_SHARED_RM);
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
