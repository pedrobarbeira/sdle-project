package org.sdle.api.controller;

import org.sdle.api.Response;
import org.sdle.model.ShoppingList;
import org.sdle.model.domain.ListOperationDataModel;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.repository.crdt.CRDT;

import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingListController extends ListController {

    public ShoppingListController(ShoppingListRepository repository) {
        super(repository);
    }

    public Response getAllShoppingLists(String user){
        List<CRDT<ShoppingList>> shoppingLists = repository.getAllCrdtFromUser(user);
        List<ListOperationDataModel> data = new ArrayList<>();
        for(CRDT<ShoppingList> crdt : shoppingLists){
            ShoppingList shoppingList = crdt.getValue();
            String id = shoppingList.getId();
            String name = shoppingList.getName();
            int itemNo = shoppingList.getItems().size();
            int sharedNo = shoppingList.getAuthorizedUsers().size();
            Date timeStamp = crdt.getTimeStamp();
            int version = crdt.getVersion();
            ListOperationDataModel dataModel = new ListOperationDataModel(id, name, itemNo, sharedNo, timeStamp, version);
            data.add(dataModel);
        }
        return ok(data);
    }

    public Response createShoppingList(String user, ListOperationDataModel dataModel){
        String id = dataModel.id;
        String name = dataModel.name;
        ShoppingList shoppingList = new ShoppingList(id, name);
        shoppingList.addAuthorizedUser(user);
        CRDT<ShoppingList> crdt = repository.put(shoppingList);
        return ok(crdt);
    }

    public Response updateShoppingList(String user, ListOperationDataModel dataModel){
        String id = dataModel.id;
        CRDT<ShoppingList> crdt = repository.getCRDT(id);
        ShoppingList shoppingList = crdt.getValue();
        String name = dataModel.name;;
        shoppingList.setName(name);
        crdt.setValue(shoppingList);
        crdt = repository.putCRDT(crdt);
        return ok(crdt);
    }

    public Response deleteShoppingList(String user, ListOperationDataModel dataModel){
        String id = dataModel.id;
        CRDT<ShoppingList> crdt = repository.delete(id);
        return ok(crdt);
    }
}