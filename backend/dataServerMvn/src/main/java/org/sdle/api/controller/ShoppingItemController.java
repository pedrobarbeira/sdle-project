package org.sdle.api.controller;

import org.sdle.api.Response;
import org.sdle.model.domain.ItemOperationDataModel;
import org.sdle.repository.ShoppingListRepository;

public class ShoppingItemController extends ListController {

    public ShoppingItemController(ShoppingListRepository repository) {
        super(repository);
    }

    public Response getShoppingItem(String user, ItemOperationDataModel dataModel){
        return ok("Getting shopping item");
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
