package org.sdle.controller;

import org.sdle.api.Response;
import org.sdle.model.ShoppingList;

import java.util.List;

public interface IShoppingListController {
    ShoppingList shareShoppingList(String id, String username);
    Response getShoppingList(String id);
    List<ShoppingList> getAllShoppingLists();
    Response getAllShoppingListsFromUser(String username);
    ShoppingList addShoppingList(ShoppingList shoppingList);
    List<ShoppingList> addShoppingLists(List<ShoppingList> shoppingLists);
    ShoppingList updateShoppingList(ShoppingList shoppingList);
    Response deleteShoppingList(ShoppingList shoppingList, String username);
}
