package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.SharedListController;
import org.sdle.model.ShareOperationDataModel;

public class SharedListRequestHandler extends ApiComponent implements RequestHandler{

    private final SharedListController controller;

    public SharedListRequestHandler(SharedListController controller){
        this.controller = controller;
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
        ShareOperationDataModel dataModel = (ShareOperationDataModel) request.getBody();
        return controller.addSharedUser(dataModel, user);
    }

    private Response handleRemoveSharedUser(Request request){
        String user = request.headers.get(Headers.USER);
        ShareOperationDataModel dataModel = (ShareOperationDataModel) request.getBody();
        return controller.removeSharedUser(dataModel, user);
    }
}
