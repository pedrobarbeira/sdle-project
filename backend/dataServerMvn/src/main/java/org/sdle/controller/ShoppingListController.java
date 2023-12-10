package org.sdle.controller;

import org.sdle.api.ApiComponent;
import org.sdle.api.Response;
import org.sdle.model.ShoppingList;
import org.sdle.repository.IShoppingListRepository;

import java.util.List;
import java.util.Set;

public class ShoppingListController extends ApiComponent implements IShoppingListController {

    private final IShoppingListRepository repository;

    public ShoppingListController(IShoppingListRepository repository){
        this.repository = repository;
    }

    public ShoppingList shareShoppingList(String id, String username) {
        return repository.addAuthorizedUser(id, username);
    }

    public Response getShoppingList(String id){
        if (id == null) {
            return badRequest();
        }
        return ok(repository.getById(id));
    }

    public List<ShoppingList> getAllShoppingLists(){
        return repository.getAll();
    }

    public Response getAllShoppingListsFromUser(String username){
        return ok(repository.getAllFromUser(username));
    }

    public ShoppingList addShoppingList(ShoppingList shoppingList){
        return repository.put(shoppingList);
    }

    public List<ShoppingList> addShoppingLists(List<ShoppingList> shoppingLists){
        return repository.put(shoppingLists);
    }

    public ShoppingList updateShoppingList(ShoppingList shoppingList){
        return repository.update(shoppingList);
    }

    public Response deleteShoppingList(ShoppingList shoppingList, String username){
        if (shoppingList == null || username == null) {
            return badRequest();
        }

        Set<String> authorizedUsers = repository.getAuthorizedUsers(shoppingList.getId());
        if (!authorizedUsers.contains(username)){
            return unauthorized();
        }

        if(repository.delete(shoppingList.getId())){
            return ok(shoppingList);
        }
        return error();
    }
}
