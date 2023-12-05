package org.sdle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.model.ShoppingList;
import org.sdle.repository.NodeRepository;
import org.sdle.utils.UtilsTcp;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;

public class NodeController {
    private final NodeRepository repository;

    public NodeController(NodeRepository repository) {
        this.repository = repository;
    }

    public Map<String, Integer> getNodeMap() {
        return this.repository.getNodeMap();
    }

    /*
    public Map<String, Object> getShoppingLists(String username) {
        Map<String, Object> shoppingLists = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Request request = new Request("api/shoppinglist", "GET", Map.of("username", username), Map.of());

        for (Integer port : repository.getNodeMap().values()) {
            try {
                Socket socket = new Socket("localhost", port);

                //socket.setSoTimeout();
                UtilsTcp.sendTcpMessage(socket, mapper.writeValueAsString(request));

                String message = UtilsTcp.receiveTcpMessage(socket);
                Response response = mapper.readValue(message, Response.class);
                if(response.getStatus() != 200) continue;

                shoppingLists.putAll(mapper.convertValue(response.getBody(), Map.class));
            } catch (IOException e) {
                System.err.println("Failed to retrieve shopping lists from node: " + port);
                System.err.println(e.getMessage());
            }
        }

        return shoppingLists;
    }
    */

    public ShoppingList postPutShoppingList(ShoppingList shoppingList, String username) {
        if(shoppingList.getPrimaryNodeId() == null) {
            String id = this.repository.getNextNodeId(shoppingList.getId());

            if(id == null) return null;

            shoppingList.setPrimaryNodeId(id);
        }

        ObjectMapper mapper = new ObjectMapper();

        int nodePort = repository.getNodeMapping(shoppingList.getPrimaryNodeId());
        Request request = new Request("api/shoppinglist", "POST", Map.of("username", username), shoppingList);

        try {
            Socket socket = new Socket("localhost", nodePort);

            //socket.setSoTimeout();

            UtilsTcp.sendTcpMessage(socket, mapper.writeValueAsString(request));

            String message = UtilsTcp.receiveTcpMessage(socket);
            Response response = mapper.readValue(message, Response.class);

            if(response.getStatus() != 200) {
                System.err.println("Failed to post/put shopping list to node: " + nodePort);
                System.err.println(response.getBody());
                return null;
            }

            return mapper.convertValue(response.getBody(), ShoppingList.class);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to post/put shopping list to node: " + nodePort);
            System.err.println(e.getMessage());
        }

        return null;
    }

    public Map getShoppingLists(Integer port, Request request) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Socket socket = new Socket("localhost", port);
            UtilsTcp.sendTcpMessage(socket, mapper.writeValueAsString(request));
            String message = UtilsTcp.receiveTcpMessage(socket);
            Response response = mapper.readValue(message, Response.class);
            if (response.getStatus() == 200) {
                return mapper.convertValue(response.getBody(), Map.class);
            }
        } catch (IOException e) {
            System.err.println("Failed to retrieve shopping lists from node: " + port);
            System.err.println(e.getMessage());
        }
        return Collections.emptyMap();
    }
}
