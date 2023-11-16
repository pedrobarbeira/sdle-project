package org.sdle.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.NodeController;
import org.sdle.model.Node;
import org.sdle.model.ShoppingList;
import org.sdle.service.AuthService;
import zmq.socket.reqrep.Req;

import java.util.Map;
import java.util.Objects;

public class NodeRequestHandler extends AbstractRequestHandler {

    private final NodeController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    public NodeRequestHandler(NodeController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {

        switch(request.getRoute()) {
            case Router.REGISTER_NODE_ROUTE -> {
                return registerNode(request);
            }
            case Router.SHOPPINGLIST -> {
                switch(request.getMethod()) {
                    case Request.GET -> {
                        return getShoppingLists(request);
                    }
                    case Request.POST, Request.PUT -> {
                        return postPutShoppingList(request);
                    }
                    case Request.DELETE -> {
                        return delShoppingList(request);
                    }
                }
            }
        }

        return buildResponse(null);
    }

    private Response delShoppingList(Request request) {
        return null;
    }
    private Response postPutShoppingList(Request request) {
        if(!Objects.equals(request.getMethod(), Request.POST) && !Objects.equals(request.getMethod(), Request.PUT)) {
            return buildResponse(405, "Method not allowed");
        }

        String username = AuthService.authenticateRequest(request);
        if(username == null) return buildResponse(401, "Unauthorized - bad credentials");

        ShoppingList shoppingList = mapper.convertValue(request.getBody(), ShoppingList.class);

        return buildResponse(controller.postPutShoppingList(shoppingList, username));
    }

    private Response getShoppingLists(Request request) {
        if(!Objects.equals(request.getMethod(), Request.GET)) {
            return buildResponse(405, "Method not allowed");
        }

        String username = AuthService.authenticateRequest(request);
        if(username == null) return buildResponse(401, "Unauthorized - bad credentials");

        return new Response(200, controller.getShoppingLists(username));
    }

    private Response registerNode(Request request) {
        if(!Objects.equals(request.getMethod(), Request.POST)) {
            return buildResponse(405, "Method not allowed");
        }

        Map<?, ?> body = mapper.convertValue(request.getBody(), Map.class);
        Integer port = (Integer) body.get("port");
        if(port == null) return buildResponse(400, "Bad request - port not found in request body");

        String nodeID = controller.addNode(port);

        if(nodeID == null) return buildResponse(null);

        return buildResponse(200, Map.of("id", nodeID));
    }
}
