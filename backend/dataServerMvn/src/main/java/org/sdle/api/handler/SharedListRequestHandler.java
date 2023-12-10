package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.ReplicaController;
import org.sdle.api.controller.SharedListController;
import org.sdle.model.domain.SharedOperationDataModel;

public class SharedListRequestHandler extends ApiComponent implements RequestHandler{

    private final SharedListController controller;
    private final ReplicaController replicaController;


    public SharedListRequestHandler(SharedListController controller, ReplicaController replicaController){
        this.controller = controller;
        this.replicaController = replicaController;
    }

    @Override
    public Response handle(Request request) {
        Response response = notFound();
        switch (request.method) {
            case Request.PUT -> response = handleAddSharedUser(request);
            case Request.DELETE -> response = handleRemoveSharedUser(request);
            default -> {}
        }
        return response;
    }

    private Response handleAddSharedUser(Request request){
        String user = request.headers.get(Headers.USER);
        SharedOperationDataModel dataModel = (SharedOperationDataModel) request.getBody();
        Response response = controller.addSharedUser(dataModel, user);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendSharedAddUserCrdtOp(dataModel);
        }
        return response;
    }

    private Response handleRemoveSharedUser(Request request){
        String user = request.headers.get(Headers.USER);
        SharedOperationDataModel dataModel = (SharedOperationDataModel) request.getBody();
        Response response = controller.removeSharedUser(dataModel, user);
        if(response.getStatus() == StatusCode.OK){
            replicaController.sendSharedRemoveUserCrdtOp(dataModel);
        }
        return response;
    }
}
