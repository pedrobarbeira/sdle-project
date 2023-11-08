package org.sdle.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingList;
import org.sdle.model.Token;
import org.sdle.service.TokenService;

import java.util.HashMap;
import java.util.Objects;

public class ShoppingListRequestHandler extends AbstractRequestHandler {

    ShoppingListController controller;

    public ShoppingListRequestHandler(ShoppingListController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {
        String username = this.authenticate(request);

        if(username == null) return buildResponse(401, "Authentication failed");

        if(Objects.equals(request.getRoute(), Router.SHOPPINGLIST)) {
            switch (request.getMethod()) {
                case Request.POST, Request.PUT -> {
                    return postPutShoppingList((String) request.getBody(), username);
                }
                case Request.GET -> {
                    return getShoppingList((String) request.getBody(), username);
                }
                case Request.DELETE -> {
                    return delShoppingList((String) request.getBody(), username);
                }
                default -> {
                    return buildResponse(405, "Method not allowed");
                }
            }
        } else if(Objects.equals(request.getRoute(), Router.SHOPPINGLIST_SHARE)) {
            return shareShoppingList((String) request.getBody(), username);
        }

        return buildResponse(null);
    }

    private Response getShoppingList(String body, String username) {
        if(Objects.equals(body, "{}")) return buildResponse(controller.getAllShoppingListsFromUser(username));

        String id = this.parse(body, String.class);

        if(id == null) return buildResponse(400, "Bad request - shoppingList id not found");

        return buildResponse(controller.getShoppingList(id));
    }

    private Response postPutShoppingList(String body, String username) {
        ShoppingList shoppingList = this.parse(body, ShoppingList.class);

        if(shoppingList == null) return buildResponse(400, "Bad request - shoppingList not found");

        if(shoppingList.getAuthorizedUsers().isEmpty()) {
            shoppingList.addAuthorizedUser(username);
        } else if(!shoppingList.getAuthorizedUsers().contains(username))
            return buildResponse(403, "Forbidden");

        return buildResponse(controller.addShoppingList(shoppingList));
    }

    private Response delShoppingList(String body, String username) {
        ShoppingList shoppingList = this.parse(body, ShoppingList.class);

        if(shoppingList == null) return buildResponse(400, "Bad request - shoppingList not found");

        if(!shoppingList.getAuthorizedUsers().contains(username)) {
            return buildResponse(403, "Forbidden");
        }

        return buildResponse(controller.deleteShoppingList(shoppingList.getId()));
    }

    private Response shareShoppingList(String body, String username) {
        HashMap<String, String> bodyItems = this.parse(body, HashMap.class);

        if(bodyItems == null) return buildResponse(400, "Bad request - shoppingListId and/or sharingWith not found");

        String shoppingListId = bodyItems.get("shoppingListId");
        String sharingWith = bodyItems.get("sharingWith");

        if(shoppingListId == null || sharingWith == null) return buildResponse(400, "Bad request - shoppingListId and/or sharingWith not found");

        ShoppingList shoppingList = controller.getShoppingList(shoppingListId);

        if(!shoppingList.getAuthorizedUsers().contains(username)) {
            return buildResponse(403, "Forbidden");
        }

        return buildResponse(controller.shareShoppingList(shoppingList.getId(), sharingWith));
    }

    private String authenticate(Request request) {
        try {
            Token token = new ObjectMapper().readValue((String) request.getHeaders(), Token.class);

            return TokenService.getUsernameFromToken(token);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private <T> T parse(Object body, Class<T> expectedClass) {
        try {
            return new ObjectMapper().readValue((String) body, expectedClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
