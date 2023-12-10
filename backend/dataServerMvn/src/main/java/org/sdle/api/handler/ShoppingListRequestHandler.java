package org.sdle.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.api.controller.IShoppingListController;
import org.sdle.model.ShoppingList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShoppingListRequestHandler extends ApiComponent implements RequestHandler {

    IShoppingListController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    public ShoppingListRequestHandler(IShoppingListController controller) {
        this.controller = controller;
    }

    @Override
    public Response handle(Request request) {
        String username = (String) mapper.convertValue(request.getHeaders(), HashMap.class).get("username");

        if (username == null) {
            return error();
        }

        if (Objects.equals(request.getRoute(), Router.API_SHOPPINGLIST)) {
            switch (request.getMethod()) {
                case Request.GET -> {
                    return getShoppingList(request.getBody(), username);
                }
                case Request.POST, Request.PUT -> {
                    return postPutShoppingList(request.getBody(), username);
                }
                case Request.DELETE -> {
                    return delShoppingList(request.getBody(), username);
                }
                default -> {
                    return notAllowed();
                }
            }
        } else if (Objects.equals(request.getRoute(), Router.API_SHARED)) {
            return shareShoppingList(request.getBody(), username);
        }

        return notFound();
    }

    private Response getShoppingList(Object body, String username) {
        Map<?, ?> mappedBody;
        try {
            mappedBody = (Map<?, ?>) body;
            if(mappedBody == null){
                return badRequest();
            }

            if (mappedBody.isEmpty()) {
                return controller.getAllShoppingListsFromUser(username);
            }

            String id = (String) mappedBody.get(Constants.SHOPPING_LIST_ID);
            return controller.getShoppingList(id);
        }catch(ClassCastException e){
            return error();
        }
    }

    private Response postPutShoppingList(Object body, String username) {
        ShoppingList shoppingList = mapper.convertValue(body, ShoppingList.class);

        if (shoppingList == null) {
            return badRequest();
        }

        ShoppingList storedShoppingList = (ShoppingList)controller.getShoppingList(shoppingList.getId()).getBody();

        if (storedShoppingList == null) {
            if (shoppingList.getAuthorizedUsers().isEmpty()) {
                shoppingList.addAuthorizedUser(username);
            } else if (!shoppingList.getAuthorizedUsers().contains(username)) {
                return unauthorized();
            }
        } else if (!storedShoppingList.getAuthorizedUsers().contains(username)) {
            return unauthorized();
        }

        return ok(controller.addShoppingList(shoppingList));
    }

    private Response delShoppingList(Object body, String username) {
        ShoppingList shoppingList = mapper.convertValue(body, ShoppingList.class);

        return controller.deleteShoppingList(shoppingList, username);
    }

    private Response shareShoppingList(Object body, String username) {
        HashMap<?, ?> bodyItems = mapper.convertValue(body, HashMap.class);

        if (bodyItems == null) return badRequest();

        String shoppingListId = (String) bodyItems.get("shoppingListId");
        String sharingWith = (String) bodyItems.get("sharingWith");

        if (shoppingListId == null || sharingWith == null)
            return badRequest();

        ShoppingList shoppingList = (ShoppingList) controller.getShoppingList(shoppingListId).getBody();

        if (!shoppingList.getAuthorizedUsers().contains(username)) {
            return unauthorized();
        }

        return ok(controller.shareShoppingList(shoppingList.getId(), sharingWith));
    }

    public static class Constants{
        public static final String SHOPPING_LIST_ID = "shoppinglistID";
    }

}
