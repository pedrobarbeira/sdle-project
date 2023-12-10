package org.sdle.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.ShoppingListController;
import org.sdle.model.ShoppingListDataModel;

public class ShoppingListRequestHandler extends ApiComponent implements RequestHandler {

    ShoppingListController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    public ShoppingListRequestHandler(ShoppingListController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {
        Response response = notFound();
        switch (request.method) {
            case Request.GET -> response = handleGetShoppingLists(request);
            case Request.POST -> response = handleCreateShoppingList(request);
            case Request.PUT -> response = handleUpdateShoppingList(request);
            case Request.DELETE -> response = handleDeleteShoppingList(request);
            default -> {}
        }
        return response;
    }

    private Response handleGetShoppingLists(Request request){
        String user = request.headers.get(Headers.USER);
        return controller.getAllShoppingLists(user);
    }

    private Response handleCreateShoppingList(Request request){
        String user = request.headers.get(Headers.USER);
        ShoppingListDataModel dataModel = (ShoppingListDataModel) request.body;
        return controller.createShoppingList(user, dataModel);
    }

    private Response handleUpdateShoppingList(Request request){
        String user = request.headers.get(Headers.USER);
        ShoppingListDataModel dataModel = (ShoppingListDataModel) request.body;
        return controller.updateShoppingList(user, dataModel);
    }

    private Response handleDeleteShoppingList(Request request){
        String user = request.headers.get(Headers.USER);
        ShoppingListDataModel dataModel = (ShoppingListDataModel) request.body;
        return controller.deleteShoppingList(user, dataModel);
    }

}
