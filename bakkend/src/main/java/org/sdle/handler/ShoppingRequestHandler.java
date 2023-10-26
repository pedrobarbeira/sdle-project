package org.sdle.handler;

import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingList;

import java.util.List;

public class ShoppingRequestHandler extends AbstractRequestHandler {

    private final ShoppingListController controller;

    public ShoppingRequestHandler(ShoppingListController controller){
        this.controller = controller;
    }

    @Override
    public Response handle(Request request){
        Object responseBody = null;
        String method = request.getMethod();
        if(method.equals(Request.GET) || method.equals(Request.DELETE)){
            if(request.getBody() == null){
                responseBody = controller.getAllShoppingLists();
            }else{
                String id = (String) request.getBody();
                switch(method){
                    case Request.GET -> responseBody = controller.getShoppingList(id);
                    case Request.DELETE -> responseBody = controller.deleteShoppingList(id);
                }
            }
        }else{
            List<ShoppingList> items = (List<ShoppingList>) request.getBody();
            if(items.size() == 1){
                switch(method){
                    case Request.POST -> responseBody = controller.addShoppingList(items.get(0));
                    case Request.PUT -> responseBody = controller.updateShoppingList(items.get(0));
                }
            }else{
                responseBody = controller.addShoppingLists(items);
            }
        }
        return buildResponse(responseBody);
    }
}
