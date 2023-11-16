package org.sdle;

import org.sdle.controller.ShoppingListController;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.handler.ShoppingListRequestHandler;

public class ObjectFactory {
    public static  ShoppingListRequestHandler initializeShoppingListRequestHandler() {
        ShoppingListRepository repository = new ShoppingListRepository();
        ShoppingListController controller = new ShoppingListController(repository);
        return new ShoppingListRequestHandler(controller);
    }
}
