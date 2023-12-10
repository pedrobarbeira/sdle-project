package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.ShoppingItemController;

public class ShoppingItemRequestHandler extends ApiComponent implements RequestHandler{
    private final ShoppingItemController shoppingItemController;

    public ShoppingItemRequestHandler(ShoppingItemController shoppingItemController){
        this.shoppingItemController = shoppingItemController;
    }
    @Override
    public Response handle(Request request) {
        return null;
    }
}
