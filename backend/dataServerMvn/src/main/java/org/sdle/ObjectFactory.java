package org.sdle;

import org.sdle.controller.ShoppingListController;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.api.handler.ShoppingListRequestHandler;

public class ObjectFactory {
    public static  ShoppingListRequestHandler initializeShoppingListRequestHandler(String nodeId) {
        ShoppingListRepository repository = new ShoppingListRepository(nodeId);
        ShoppingListController controller = new ShoppingListController(repository);
        return new ShoppingListRequestHandler(controller);
    }
}
