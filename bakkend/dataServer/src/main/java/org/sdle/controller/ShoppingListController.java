package org.sdle.controller;

import org.sdle.model.ShoppingList;
import org.sdle.service.ShoppingListRepository;

import java.util.List;

public class ShoppingListController {
    private final ShoppingListRepository repository;

    public ShoppingListController(ShoppingListRepository repository){
        this.repository = repository;
    }

    public ShoppingList getShoppingList(String id){
        return repository.getById(id);
    }

    public List<ShoppingList> getAllShoppingLists(){
        return repository.getAll();
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

    public boolean deleteShoppingList(String id){
        return repository.delete(id);
    }
}
