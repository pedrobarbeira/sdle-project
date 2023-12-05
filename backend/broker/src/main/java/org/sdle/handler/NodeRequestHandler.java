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
            default -> {
                return buildResponse(null);
            }
        }
    }
    private Response postPutShoppingList(Request request) {

        String username = AuthService.authenticateRequest(request);
        if(username == null) return buildResponse(401, "Unauthorized - bad credentials");

        ShoppingList shoppingList = mapper.convertValue(request.getBody(), ShoppingList.class);

        return buildResponse(controller.postPutShoppingList(shoppingList, username));
    }
    private Response getShoppingLists(Request request) {

        String username = AuthService.authenticateRequest(request);
        if(username == null) return buildResponse(401, "Unauthorized - bad credentials");

        Map<String, Object> shoppingLists = new HashMap<>();
        Request requestShoppingLists = new Request("api/shoppinglist", "GET", Map.of("username", username), Map.of());

        List<Callable<Map<String, Object>>> tasks = new ArrayList<>();

        for (Integer port : controller.getNodeMap().values()) {
            tasks.add(() ->
                controller.getShoppingLists(port, requestShoppingLists)
            );
        }

        List<Future<Map<String, Object>>> futures;
        try {
            futures = workers.invokeAll(tasks);

            for (Future<Map<String, Object>> future : futures) {
                try {
                    Map<String, Object> result = future.get();
                    shoppingLists.putAll(result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*
        for (Integer port : controller.getNodeMap().values()) {
            try {
                Socket socket = new Socket("localhost", port);

                //socket.setSoTimeout();
                UtilsTcp.sendTcpMessage(socket, mapper.writeValueAsString(requestShoppingLists));

                String message = UtilsTcp.receiveTcpMessage(socket);
                Response response = mapper.readValue(message, Response.class);
                if(response.getStatus() != 200) continue;

                shoppingLists.putAll(mapper.convertValue(response.getBody(), Map.class));
            } catch (IOException e) {
                System.err.println("Failed to retrieve shopping lists from node: " + port);
                System.err.println(e.getMessage());
            }
        }*/

        return new Response(200, shoppingLists);
    }

    private Response delShoppingList(Request request) {
        return null;
    }

}
