package org.sdle.repository;

import org.sdle.model.ShoppingList;

import java.util.List;
import java.util.Set;

public interface IShoppingListRepository {
    ShoppingList getById(String id);
    List<ShoppingList> getAll();
    List<ShoppingList> getAllFromUser(String username);
    ShoppingList put(ShoppingList item);
    List<ShoppingList> put(List<ShoppingList> items);
    ShoppingList update(ShoppingList item);
    boolean delete(String id);
    ShoppingList addAuthorizedUser(String id, String username);
    Set<String> getAuthorizedUsers(String id);
}
