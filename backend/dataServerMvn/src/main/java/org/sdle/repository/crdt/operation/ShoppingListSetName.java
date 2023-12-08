package org.sdle.repository.crdt.operation;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.time.LocalDateTime;

public class ShoppingListSetName extends CRDTOp<ShoppingList>{

    ShoppingListSetName(String targetId, String value, String version, LocalDateTime timeStamp){
        super(targetId, value, version, timeStamp);
    }

    @Override
    public void apply(CRDT<ShoppingList> target) {
        LocalDateTime targetTimeStamp = target.getTimeStamp();
        int comparisonResult = targetTimeStamp.compareTo(this.timeStamp);
        if(target.getVersion().equals(this.version) || comparisonResult < 0){
            target.getValue().setName((String) this.value);
            target.setTimeStamp(LocalDateTime.now());
        }
    }
}
