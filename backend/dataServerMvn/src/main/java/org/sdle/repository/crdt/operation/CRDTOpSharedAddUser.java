package org.sdle.repository.crdt.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ICRDTRepository;
import org.sdle.repository.crdt.CRDT;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

public class CRDTOpSharedAddUser extends CRDTOp<ShoppingList>{

    public CRDTOpSharedAddUser(String targetId, CRDT<ShoppingList> value, int version) {
        super(targetId, value, version, Date.from(Instant.now()), OP_SHARED_ADD);
    }

    @JsonCreator
    public CRDTOpSharedAddUser(
            @JsonProperty("targetId") String targetId,
            @JsonProperty("value")CRDT<ShoppingList> value,
            @JsonProperty("version")int version,
            @JsonProperty("timeStamp")Date timeStamp,
            @JsonProperty("type")String type) {
        super(targetId, value, version, timeStamp, type);
    }

    @Override
    public void apply(CRDT<ShoppingList> target, ICRDTRepository<ShoppingList> repository) {
        Set<String> authorizedUsers = value.getValue().getAuthorizedUsers();
        Set<String> targetUsers = target.getValue().getAuthorizedUsers();
        targetUsers.addAll(authorizedUsers);;
        repository.putCRDT(target);
    }
}
