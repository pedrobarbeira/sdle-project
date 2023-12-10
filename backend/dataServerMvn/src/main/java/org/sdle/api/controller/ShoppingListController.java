package org.sdle.api.controller;

import org.sdle.api.Response;
import org.sdle.model.ShoppingList;
import org.sdle.model.domain.ListOperationDataModel;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.repository.crdt.CRDT;

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
            ListOperationDataModel dataModel = new ListOperationDataModel(id, name, itemNo, sharedNo, timeStamp);
            data.add(dataModel);
        }
        return ok(data);
    }

    public Response createShoppingList(String user, ListOperationDataModel dataModel){
        return ok("Creating shopping list");
    }

    public Response updateShoppingList(String user, ListOperationDataModel dataModel){
        return ok("Updating shopping list");
    }

    public Response deleteShoppingList(String user, ListOperationDataModel dataModel){
        return ok("Deleting shopping list");
    }
}