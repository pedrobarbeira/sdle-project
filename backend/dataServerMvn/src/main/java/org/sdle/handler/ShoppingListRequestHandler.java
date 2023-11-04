package org.sdle.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingList;
import org.sdle.model.Token;
import org.sdle.service.TokenService;

import java.util.Objects;

public class ShoppingListRequestHandler extends AbstractRequestHandler {

    ShoppingListController controller;

    public ShoppingListRequestHandler(ShoppingListController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {
        System.out.println(request.getBody());
        if(Objects.equals(request.getRoute(), Router.SHOPPINGLIST)) {
            switch (request.getMethod()) {
                case Request.POST, Request.PUT -> {
                    return postPutShoppingList(request);
                }
                case Request.GET -> {
                    return getShoppingList(request);
                }
                case Request.DELETE -> {
                    return delShoppingList(request);
                }
                default -> {
                    return buildResponse(null);
                }
            }
        } else if(Objects.equals(request.getRoute(), Router.SHOPPINGLIST_SHARE)) {
            return shareShoppingList(request);
        }

        return buildResponse(null);
    }

    private Response getShoppingList(Request request) {
        String username;
        try {
            Token token = new ObjectMapper().readValue((String) request.getBody(), Token.class);
            username = TokenService.getUsernameFromToken(token);

            if(Objects.equals(username, null)) {
                return buildResponse(null);
            }

        } catch (JsonProcessingException e) {
            System.err.println("Json Processing exception: " + e.getMessage());
            return buildResponse(null);
        }

        if(request.getRoute().split("/").length == 3) {
            String id = request.getRoute().split("/")[2];
            return buildResponse(controller.getShoppingList(id));
        }

        return buildResponse(controller.getAllShoppingListsFromUser(username));
    }

    private Response postPutShoppingList(Request request) {
        String username;
        ShoppingList shoppingList;

        try {
            JsonNode jsonNode = new ObjectMapper().readTree((String) request.getBody());
            Token token = new ObjectMapper().treeToValue(jsonNode.get("token"), Token.class);
            username = TokenService.getUsernameFromToken(token);

            if(Objects.equals(username, null)) {
                return buildResponse(null);
            }

            shoppingList = new ObjectMapper().treeToValue(jsonNode.get("shoppingList"), ShoppingList.class);
        } catch (JsonProcessingException e) {
            System.err.println("Json Processing exception: " + e.getMessage());
            return buildResponse(null);
        }

        if(shoppingList.getAuthorizedUsers().isEmpty()) {
            shoppingList.addAuthorizedUser(username);
        } else if(!shoppingList.getAuthorizedUsers().contains(username))
            return buildResponse(null);


        return buildResponse(controller.addShoppingList(shoppingList));
    }

    private Response delShoppingList(Request request) {
        String username;
        ShoppingList shoppingList;

        try {
            JsonNode jsonNode = new ObjectMapper().readTree((String) request.getBody());
            Token token = new ObjectMapper().treeToValue(jsonNode.get("token"), Token.class);
            username = TokenService.getUsernameFromToken(token);

            if(Objects.equals(username, null)) {
                return buildResponse(null);
            }

            shoppingList = new ObjectMapper().treeToValue(jsonNode.get("shoppingList"), ShoppingList.class);
        } catch (JsonProcessingException e) {
            System.err.println("Json Processing exception: " + e.getMessage());
            return buildResponse(null);
        }

        if(!shoppingList.getAuthorizedUsers().contains(username)) {
            System.out.println("unauthorized");
            return buildResponse(null);
        }

        return buildResponse(controller.deleteShoppingList(shoppingList.getId()));
    }

    private Response shareShoppingList(Request request) {
        String username;
        ShoppingList shoppingList;
        String sharingWithUsername;

        try {
            JsonNode jsonNode = new ObjectMapper().readTree((String) request.getBody());
            Token token = new ObjectMapper().treeToValue(jsonNode.get("token"), Token.class);
            username = TokenService.getUsernameFromToken(token);

            sharingWithUsername = new ObjectMapper().treeToValue(jsonNode.get("sharingWith"), String.class);

            if(Objects.equals(username, null)) {
                return buildResponse(null);
            }

            shoppingList = new ObjectMapper().treeToValue(jsonNode.get("shoppingList"), ShoppingList.class);
        } catch (JsonProcessingException e) {
            System.err.println("Json Processing exception: " + e.getMessage());
            return buildResponse(null);
        }

        if(!shoppingList.getAuthorizedUsers().contains(username)) {
            System.out.println("unauthorized sharing");
            return buildResponse(null);
        }

        return buildResponse(controller.shareShoppingList(shoppingList.getId(), sharingWithUsername));
    }
}
