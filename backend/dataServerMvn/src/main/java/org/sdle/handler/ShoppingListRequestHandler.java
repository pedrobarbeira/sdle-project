package org.sdle.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShoppingListRequestHandler extends AbstractRequestHandler {

    ShoppingListController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    public ShoppingListRequestHandler(ShoppingListController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {
        String username = (String) mapper.convertValue(request.getHeaders(), HashMap.class).get("username");

        if(username == null) return buildResponse(500);

        if(Objects.equals(request.getRoute(), Router.SHOPPINGLIST)) {
            switch (request.getMethod()) {
                case Request.POST, Request.PUT -> {
                    return postPutShoppingList(request.getBody(), username);
                }
                case Request.GET -> {
                    return getShoppingList(request.getBody(), username);
                }
                case Request.DELETE -> {
                    return delShoppingList(request.getBody(), username);
                }
                default -> {
                    return buildResponse(405, "Method not allowed");
                }
            }
        } else if(Objects.equals(request.getRoute(), Router.SHOPPINGLIST_SHARE)) {
            return shareShoppingList(request.getBody(), username);
        }

        return buildResponse(null);
    }

    private Response getShoppingList(Object body, String username) {
        Map<?, ?> mappedBody = mapper.convertValue(body, Map.class);

        if(mappedBody.isEmpty()) return buildResponse(controller.getAllShoppingListsFromUser(username));

        String id = (String) mappedBody.get("shoppinglistID");

        if(id == null) return buildResponse(400, "Bad request - shoppingList id not found");

        return buildResponse(controller.getShoppingList(id));
    }

    private Response postPutShoppingList(Object body, String username) {
        ShoppingList shoppingList = mapper.convertValue(body, ShoppingList.class);

        if(shoppingList == null) return buildResponse(400, "Bad request - shoppingList not found");

        ShoppingList storedShoppingList = controller.getShoppingList(shoppingList.getId());

        if(storedShoppingList == null) {
            if(shoppingList.getAuthorizedUsers().isEmpty()) {
                shoppingList.addAuthorizedUser(username);
            } else if(!shoppingList.getAuthorizedUsers().contains(username)) {
                System.out.println("here1");
                return buildResponse(403, "Forbidden");
            }
        } else {
            try {
                System.out.println(mapper.writeValueAsString(storedShoppingList));
                System.out.println(storedShoppingList.getAuthorizedUsers());
                System.out.println(username);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if(!storedShoppingList.getAuthorizedUsers().contains(username)) {
                return buildResponse(403, "Forbidden");
            }
        }

        return buildResponse(controller.addShoppingList(shoppingList));
    }

    private Response delShoppingList(Object body, String username) {
        ShoppingList shoppingList = mapper.convertValue(body, ShoppingList.class);

        if(shoppingList == null) return buildResponse(400, "Bad request - shoppingList not found");

        if(!controller.getShoppingList(shoppingList.getId()).getAuthorizedUsers().contains(username))
            return buildResponse(403, "Forbidden");

        return buildResponse(controller.deleteShoppingList(shoppingList.getId()));
    }

    private Response shareShoppingList(Object body, String username) {
        HashMap<?, ?> bodyItems = mapper.convertValue(body, HashMap.class);

        if(bodyItems == null) return buildResponse(400, "Bad request - shoppingListId and/or sharingWith not found");

        String shoppingListId = (String) bodyItems.get("shoppingListId");
        String sharingWith = (String) bodyItems.get("sharingWith");

        if(shoppingListId == null || sharingWith == null) return buildResponse(400, "Bad request - shoppingListId and/or sharingWith not found");

        ShoppingList shoppingList = controller.getShoppingList(shoppingListId);

        if(!shoppingList.getAuthorizedUsers().contains(username)) {
            return buildResponse(403, "Forbidden");
        }

        return buildResponse(controller.shareShoppingList(shoppingList.getId(), sharingWith));
    }
}
