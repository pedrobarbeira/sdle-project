package org.sdle.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.NodeController;
import org.sdle.model.Node;

import java.util.Objects;

public class NodeRequestHandler extends AbstractRequestHandler {

    private final NodeController controller;

    public NodeRequestHandler(NodeController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {

        switch(request.getRoute()) {
            case Router.REGISTER_NODE_ROUTE -> {
                return registerNode(request);
            }
        }

        return buildResponse(null);
    }

    private Response registerNode(Request request) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!Objects.equals(request.getMethod(), Request.POST)) {
            return buildResponse(405, "Method not allowed");
        }

        Node node = parse(request.getBody(), Node.class);

        if(node == null) return buildResponse(400, "Bad request - node not found in request body");

        if(controller.addNode(node)) return buildResponse(200, null);

        return buildResponse(500, "Failed to add node");
    }

    private <T> T parse(Object body, Class<T> expectedClass) {
        try {
            return new ObjectMapper().readValue((java.lang.String) body, expectedClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
