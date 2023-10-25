package org.sdle;

import org.sdle.model.ShoppingItem;
import org.sdle.model.ShoppingList;
import org.sdle.service.ShoppingListRepository;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ShoppingListRepository repo = new ShoppingListRepository();
        ShoppingList list = repo.getById("57040887-b2df-49ea-947e-361edb0965c0");
        Map<String, ShoppingItem> map = list.getItems();
        for(String key :map.keySet()){
            ShoppingItem item = map.get(key);
            String msg = String.format("name: %s | quantity: %d", item.getName(), item.getQuantity());
            System.out.println(msg);
        }
    }
}