package org.sdle.api.controller;

import org.sdle.api.ApiComponent;
import org.sdle.api.Response;
import org.sdle.model.ShoppingListDataModel;
import org.sdle.repository.IShoppingListRepository;

public class ShoppingListController extends ApiComponent {
    private final IShoppingListRepository repository;

    public ShoppingListController(IShoppingListRepository repository) {
        this.repository = repository;
    }

    public Response getAllShoppingLists(String user){
        return ok("Getting all shopping lists");
    }

    public Response createShoppingList(String user, ShoppingListDataModel dataModel){
        return ok("Creating shopping list");
    }

    public Response updateShoppingList(String user, ShoppingListDataModel dataModel){
        return ok("Updating shopping list");
    }

    public Response deleteShoppingList(String user, ShoppingListDataModel dataModel){
        return ok("Deleting shopping list");
    }
}