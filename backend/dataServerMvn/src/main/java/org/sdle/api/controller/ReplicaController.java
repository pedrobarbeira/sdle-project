package org.sdle.api.controller;

import org.sdle.api.ApiComponent;
import org.sdle.api.Response;
import org.sdle.model.ShoppingList;
import org.sdle.model.domain.ItemOperationDataModel;
import org.sdle.model.domain.ListOperationDataModel;
import org.sdle.model.domain.SharedOperationDataModel;
import org.sdle.repository.Cache;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.repository.crdt.CRDT;
import org.sdle.repository.crdt.operation.CRDTOp;
import org.sdle.repository.crdt.operation.CRDTOpListsCreate;
import org.sdle.repository.crdt.operation.CRDTOpSharedAddUser;
import org.sdle.repository.crdt.operation.CRDTOpSharedRemoveUser;
import org.sdle.service.ReplicaService;

public class ReplicaController extends ApiComponent {

    private final ShoppingListRepository repository;
    private final ReplicaService<ShoppingList> replicaService;

    public ReplicaController(ShoppingListRepository repository, ReplicaService<ShoppingList> replicaService){
        this.repository = repository;
        this.replicaService = replicaService;
    }

    public Response getData(){
        Cache data = this.repository.getCache();
        return ok(data);
    }

    public void sendListsCreateCrdtOp(ListOperationDataModel dataModel){
        String id = dataModel.id;
        CRDT<ShoppingList> crdt = repository.getCRDT(id);
        int version = crdt.getVersion();
        CRDTOpListsCreate crdtOp = new CRDTOpListsCreate(id, crdt, version);
        replicaService.publish(crdtOp);
    }

    public void sendListsUpdateCrdtOp(ListOperationDataModel dataModel){

    }

    public void sendListsDeleteCrdtOp(ListOperationDataModel dataModel){

    }

    public void sendSharedAddUserCrdtOp(SharedOperationDataModel dataModel){
        String targetId = dataModel.targetId;
        String username = dataModel.username;
        CRDT<ShoppingList> crdt = repository.getCRDT(targetId);
        int version = crdt.getVersion();
        CRDTOpSharedAddUser crdtOp = new CRDTOpSharedAddUser(targetId, crdt, version);
        replicaService.publish(crdtOp);
    }

    public void sendSharedRemoveUserCrdtOp(SharedOperationDataModel dataModel){
        String targetId = dataModel.targetId;
        String username = dataModel.username;
        CRDT<ShoppingList> crdt = repository.getCRDT(targetId);
        int version = crdt.getVersion();
        CRDTOpSharedRemoveUser crdtOp = new CRDTOpSharedRemoveUser(targetId, crdt, version);
        replicaService.publish(crdtOp);
    }

    public void sendItemsCreateCrdtOp(ItemOperationDataModel dataModel){

    }

    public void sendItemsUpdateCrdtOp(ItemOperationDataModel dataModel){

    }

    public void sendItemsDeleteCrdtOp(ItemOperationDataModel dataModel){

    }
}
