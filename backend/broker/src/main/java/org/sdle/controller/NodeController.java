package org.sdle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.model.ShoppingList;
import org.sdle.repository.NodeRepository;
import org.sdle.utils.UtilsIDGenerator;
import org.sdle.utils.UtilsTcp;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NodeController {
    private final NodeRepository repository;

    public NodeController(NodeRepository repository) {
        this.repository = repository;
    }

    public String getNextNodeId() {
        String id = repository.popQueue();
        repository.enQueue(id);
        return id;
    }
    public String addNode(Integer nodePort) {
        String id = UtilsIDGenerator.generateSequentialID();

        if(id == null) return null;

        return repository.addNode(id, nodePort);
    }
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

    public ShoppingList postPutShoppingList(ShoppingList shoppingList, String username) {
        if(shoppingList.getId().split("_").length == 1) {
            shoppingList.setId(this.getNextNodeId() + "_" + shoppingList.getId());
        }

        ObjectMapper mapper = new ObjectMapper();
        String nodeID = shoppingList.getId().split("_")[0];
        int nodePort = repository.getNodeMapping(nodeID);
        Request request = new Request("api/shoppinglist", "POST", Map.of("username", username), shoppingList);

        try {
            Socket socket = new Socket("localhost", nodePort);

            //socket.setSoTimeout();

            UtilsTcp.sendTcpMessage(socket, mapper.writeValueAsString(request));

            String message = UtilsTcp.receiveTcpMessage(socket);
            Response response = mapper.readValue(message, Response.class);

            ShoppingList resShoppingList = mapper.convertValue(response.getBody(), ShoppingList.class);

            if(response.getStatus() != 200 || resShoppingList == null){
                System.err.println("Failed to post/put shopping list to node: " + nodePort);
                System.err.println(response.getBody());
                return null;
            }

            return resShoppingList;
        } catch (IOException e) {
            System.err.println("Failed to post/put shopping list to node: " + nodePort);
            System.err.println(e.getMessage());
        }

        return null;
    }
}
