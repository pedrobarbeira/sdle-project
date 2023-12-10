package org.sdle.api.controller;

import org.sdle.api.Response;
import org.sdle.model.domain.SharedOperationDataModel;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;

import java.util.Set;

public class SharedListController extends ListController {

    public SharedListController(ShoppingListRepository repository) {
        super(repository);
    }

    public Response addSharedUser(SharedOperationDataModel dataModel, String user){
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

    public Response removeSharedUser(SharedOperationDataModel dataModel, String user){
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
