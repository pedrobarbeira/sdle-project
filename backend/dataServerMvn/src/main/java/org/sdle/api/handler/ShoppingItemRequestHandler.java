package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.ReplicaController;
import org.sdle.api.controller.ShoppingItemController;
import org.sdle.model.domain.ItemOperationDataModel;

public class ShoppingItemRequestHandler extends ApiComponent implements RequestHandler{
    private final ShoppingItemController controller;
    private final ReplicaController replicaController;

    public ShoppingItemRequestHandler(ShoppingItemController controller, ReplicaController replicaController){
        this.controller = controller;
        this.replicaController = replicaController;
    }
    @Override
    public Response handle(Request request) {
        Response response = notFound();
        switch (request.method) {
            case Request.GET -> response = handleGetShoppingItem(request);
            case Request.POST -> response = handleCreateShoppingItem(request);
            case Request.PUT -> response = handleUpdateShoppingItemQuantity(request);
            case Request.DELETE -> response = handleDeleteShoppingItem(request);
            default -> {}
        }
        return response;
    }

    public Response handleGetShoppingItem(Request request){
        String user = request.headers.get(Headers.USER);
        ItemOperationDataModel dataModel = (ItemOperationDataModel) request.body;
        return controller.getShoppingItem(user, dataModel);
    }

    public Response handleCreateShoppingItem(Request request){
        String user = request.headers.get(Headers.USER);
        ItemOperationDataModel dataModel = (ItemOperationDataModel) request.body;
        Response response = controller.createShoppingItem(user, dataModel);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendItemsCreateCrdtOp(dataModel);
        }
        return response;
    }

    public Response handleUpdateShoppingItemQuantity(Request request){
        String user = request.headers.get(Headers.USER);
        ItemOperationDataModel dataModel = (ItemOperationDataModel) request.body;
        Response response = controller.updateShoppingItem(user, dataModel);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendItemsUpdateCrdtOp(dataModel);
        }
        return response;
    }

    public Response handleDeleteShoppingItem(Request request){
        String user = request.headers.get(Headers.USER);
        ItemOperationDataModel dataModel = (ItemOperationDataModel) request.body;
        Response response = controller.removeShoppingItem(user, dataModel);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendItemsDeleteCrdtOp(dataModel);
        }
        return response;
    }
}
