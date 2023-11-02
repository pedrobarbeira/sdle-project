package org.sdle;

import org.sdle.controller.ShoppingListController;
import org.sdle.handler.ShoppingRequestHandler;
import org.sdle.service.ShoppingListRepository;

public class ObjectFactory {

    public static ShoppingRequestHandler initializeShoppingRequestHandler(){
        ShoppingListRepository repository = new ShoppingListRepository();
        ShoppingListController controller = new ShoppingListController(repository);
        return new ShoppingRequestHandler(controller);
    }
}
