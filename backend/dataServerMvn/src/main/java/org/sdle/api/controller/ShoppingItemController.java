package org.sdle.api.controller;

import org.sdle.api.Response;
import org.sdle.model.ShoppingItem;
import org.sdle.model.ShoppingList;
import org.sdle.model.domain.ItemOperationDataModel;
import org.sdle.repository.ShoppingListRepository;

import java.util.HashMap;
import java.util.Set;

public class ShoppingItemController extends ListController {

    public ShoppingItemController(ShoppingListRepository repository) {
        super(repository);
    }

    public Response getShoppingItem(String user, ItemOperationDataModel dataModel){
        String listId = dataModel.targetId;
        ShoppingList shoppingList = repository.getById(listId);
        Set<String> authorizedUsers = shoppingList.getAuthorizedUsers();
        if(!authorizedUsers.contains(user)){
            unauthorized();
        }
        HashMap<String, ShoppingItem> itemMap = shoppingList.getItems();
        String itemId = dataModel.itemId;
        ShoppingItem item = itemMap.get(itemId);
        ItemOperationDataModel returnModel = new ItemOperationDataModel();
        returnModel.itemId = item.getId();
        returnModel.targetId = listId;
        returnModel.itemName = item.getName();
        returnModel.quantity = item.getQuantity();
        return ok(returnModel);
    }

    public Response createShoppingItem(String user, ItemOperationDataModel dataModel){
        return ok("Getting shopping item");
    }

    public Response updateShoppingItem(String user, ItemOperationDataModel dataModel){
        return ok("Getting shopping item");
    }

    public Response removeShoppingItem(String user, ItemOperationDataModel dataModel){
        return ok("Getting shopping item");
    }
}
