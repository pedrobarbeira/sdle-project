package org.sdle.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.NodeController;
import org.sdle.model.Node;
import org.sdle.model.ShoppingList;
import org.sdle.service.AuthService;
import org.sdle.utils.UtilsTcp;
import zmq.socket.reqrep.Req;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class NodeRequestHandler extends AbstractRequestHandler {

    private final NodeController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService workers;
    public NodeRequestHandler(NodeController controller, ExecutorService workers) {
        this.controller = controller;
        this.workers = workers;
    }

    @Override
    public Response handle(Request request) {
        String username = AuthService.authenticateRequest(request);
        if(username == null) {
            return buildResponse(401, "Unauthorized - bad credentials");
        }
        switch(request.getMethod()) {
            case Request.GET -> {
                return getShoppingLists(request, username);
            }
            case Request.POST, Request.PUT -> {
                return postPutShoppingList(request, username);
            }
            case Request.DELETE -> {
                return delShoppingList(request, username);
            }
            default -> {
                return buildResponse(null);
            }
        }
    }
    private Response postPutShoppingList(Request request, String username) {

        ShoppingList shoppingList = mapper.convertValue(request.getBody(), ShoppingList.class);

        return buildResponse(controller.postPutShoppingList(shoppingList, username));
    }

    private Response getShoppingLists(Request request, String username) {
        List<ShoppingList> shoppingLists = new ArrayList<>();
        Request requestShoppingLists = new Request("api/shoppinglist", "GET", Map.of("username", username), Map.of());

        List<Callable<List<ShoppingList>>> tasks = new ArrayList<>();

        for (Integer port : controller.getNodeMap().values()) {
            tasks.add(() ->
                controller.getShoppingLists(port, requestShoppingLists)
            );
        }

        try {
            for (Future<List<ShoppingList>> future : workers.invokeAll(tasks)) {
                List<ShoppingList> result = future.get();
                shoppingLists.addAll(result);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return new Response(200, shoppingLists);
    }

    private Response delShoppingList(Request request, String username) {
        return null;
    }

}
