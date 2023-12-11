package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ICRDTRepository;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;

public class CRDTOpListsUpdate extends CRDTOp<ShoppingList> {
    public CRDTOpListsUpdate(String targetId, CRDT<ShoppingList> value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_LISTS_UPDATE);
    }

    @JsonCreator
    public CRDTOpListsUpdate(
            @JsonProperty("targetId") String targetId,
            @JsonProperty("value")CRDT<ShoppingList> value,
            @JsonProperty("version")int version,
            @JsonProperty("timeStamp")Date timeStamp,
            @JsonProperty("type")String type) {
        super(targetId, value, version, timeStamp, type);
    }

    @Override
    public void apply(CRDT<ShoppingList> target, ICRDTRepository<ShoppingList> repository) {
        int version = value.getVersion();
        int targetVersion = target.getVersion();
        ShoppingList shoppingList = value.getValue();
        ShoppingList targetList = target.getValue();
        if(version > targetVersion){
            String name = shoppingList.getName();
            targetList.setName(name);
        }else if(version == targetVersion){
            Date timeStamp = value.getTimeStamp();
            Date targetStamp = target.getTimeStamp();
            int comparison = targetStamp.compareTo(timeStamp);
            if(comparison < 0){
                String name = shoppingList.getName();
                targetList.setName(name);
            }
        }
        repository.putCRDT(target);
    }
}