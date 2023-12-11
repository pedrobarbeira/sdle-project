package org.sdle.api.controller;

import org.sdle.api.Response;
import org.sdle.model.domain.ListOperationDataModel;
import org.sdle.repository.ShoppingListRepository;

public class ShoppingListController extends ListController {

    public ShoppingListController(ShoppingListRepository repository) {
        super(repository);
    }

    public Response getAllShoppingLists(String user){
        return ok("Getting all shopping lists");
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