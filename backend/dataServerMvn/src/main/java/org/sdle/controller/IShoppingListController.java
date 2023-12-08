package org.sdle.controller;

import org.sdle.model.ShoppingList;

import java.util.List;

public interface IShoppingListController {
    ShoppingList shareShoppingList(String id, String username);
    ShoppingList getShoppingList(String id);
    List<ShoppingList> getAllShoppingLists();
    List<ShoppingList> getAllShoppingListsFromUser(String username);
    ShoppingList addShoppingList(ShoppingList shoppingList);
    List<ShoppingList> addShoppingLists(List<ShoppingList> shoppingLists);
    ShoppingList updateShoppingList(ShoppingList shoppingList);
    boolean deleteShoppingList(String id);
}
