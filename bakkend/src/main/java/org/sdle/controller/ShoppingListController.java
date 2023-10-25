package org.sdle.controller;

import org.sdle.model.ShoppingList;
import org.sdle.service.ShoppingListRepository;

public class ShoppingListController {
    private ShoppingListRepository repository;

    public ShoppingListController(ShoppingListRepository repository){
        this.repository = repository;
    }

    public ShoppingList getShoppingList(){
        return null;
    }

    public ShoppingList addShoppingList(){
        return null;
    }

    public ShoppingList updateShoppingList(){
        return null;
    }

    public void deleteShoppingList(){

    }
}
