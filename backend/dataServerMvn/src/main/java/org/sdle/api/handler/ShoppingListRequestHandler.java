package org.sdle.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.ReplicaController;
import org.sdle.api.controller.ShoppingListController;
import org.sdle.model.domain.ListOperationDataModel;

public class ShoppingListRequestHandler extends ApiComponent implements RequestHandler {

    private final ShoppingListController controller;
    private final ReplicaController replicaController;
    private final ObjectMapper mapper = new ObjectMapper();

    public ShoppingListRequestHandler(ShoppingListController controller, ReplicaController replicaController) {
        this.controller = controller;
        this.replicaController = replicaController;
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
        ListOperationDataModel dataModel = (ListOperationDataModel) request.body;
        Response response =  controller.createShoppingList(user, dataModel);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendListsCreateCrdtOp(dataModel);
        }
        return response;
    }

    private Response handleUpdateShoppingList(Request request){
        String user = request.headers.get(Headers.USER);
        ListOperationDataModel dataModel = (ListOperationDataModel) request.body;
        Response response = controller.updateShoppingList(user, dataModel);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendListsUpdateCrdtOp(dataModel);
        }
        return response;
    }

    private Response handleDeleteShoppingList(Request request){
        String user = request.headers.get(Headers.USER);
        ListOperationDataModel dataModel = (ListOperationDataModel) request.body;
        Response response = controller.deleteShoppingList(user, dataModel);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendListsDeleteCrdtOp(dataModel);
        }
        return response;
    }

}
