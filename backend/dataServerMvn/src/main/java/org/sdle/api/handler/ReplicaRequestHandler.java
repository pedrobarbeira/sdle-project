package org.sdle.api.handler;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.controller.ReplicaController;

public class ReplicaRequestHandler extends ApiComponent implements RequestHandler{
    private final ReplicaController controller;

    public ReplicaRequestHandler(ReplicaController controller){
        this.controller = controller;
    }
    @Override
    public Response handle(Request request) {
        return request.getMethod().equals(Request.GET) ? controller.getData() : error();
    }
}
