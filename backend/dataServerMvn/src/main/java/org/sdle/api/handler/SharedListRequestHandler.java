package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.SharedlistController;

public class SharedListRequestHandler extends ApiComponent implements RequestHandler{

    private final SharedlistController sharedListController;

    public SharedListRequestHandler(SharedlistController sharedListController){
        this.sharedListController = sharedListController;
    }

    @Override
    public Response handle(Request request) {
        return null;
    }
}
