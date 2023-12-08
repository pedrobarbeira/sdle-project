package org.sdle.repository;

import org.sdle.model.ShoppingList;
import org.sdle.repository.crdt.CRDT;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface IShoppingListRepository {
    ShoppingList getById(String id);
    List<ShoppingList> getAll();
    List<ShoppingList> getAllFromUser(String username);
    ShoppingList put(ShoppingList item);
    List<ShoppingList> put(List<ShoppingList> items);
    ShoppingList update(ShoppingList item);
    boolean delete(String id);
    ShoppingList addAuthorizedUser(String id, String username);
}
