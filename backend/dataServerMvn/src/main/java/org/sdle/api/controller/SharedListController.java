package org.sdle.api.controller;

import org.sdle.api.ApiComponent;
import org.sdle.api.Response;
import org.sdle.model.ShareOperationDataModel;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;

import java.util.Set;

public class SharedListController extends ApiComponent {
    private final ShoppingListRepository repository;

    public SharedListController(ShoppingListRepository repository) {
        this.repository = repository;
    }

    public Response addSharedUser(ShareOperationDataModel dataModel, String user){
        String id = dataModel.targetId;
        ShoppingList shoppingList = repository.getById(id);
        Set<String> authorizedUsers = shoppingList.getAuthorizedUsers();
        if(!authorizedUsers.contains(user)){
            return unauthorized();
        }
        String newUser = dataModel.username;
        authorizedUsers.add(newUser);
        shoppingList.setAuthorizedUsers(authorizedUsers);
        repository.put(shoppingList);
        return ok(shoppingList);
    }

    public Response removeSharedUser(ShareOperationDataModel dataModel, String user){
        String id = dataModel.targetId;
        ShoppingList shoppingList = repository.getById(id);
        Set<String> authorizedUsers = shoppingList.getAuthorizedUsers();
        if(!authorizedUsers.contains(user)){
            return unauthorized();
        }
        String newUser = dataModel.username;
        authorizedUsers.remove(newUser);
        shoppingList.setAuthorizedUsers(authorizedUsers);
        repository.put(shoppingList);
        return ok(shoppingList);
    }
}
