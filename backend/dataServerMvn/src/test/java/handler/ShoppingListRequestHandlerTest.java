package handler;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.IShoppingListController;
import org.sdle.api.handler.ShoppingListRequestHandler;

import java.util.HashMap;

public class ShoppingListRequestHandlerTest {
    private static HashMap<String, String> defaultHeaders;
    private static IShoppingListController mockController;
    private static ShoppingListRequestHandler requestHandler;

    @BeforeAll
    static void setup(){
        mockController = mock(IShoppingListController.class);
        requestHandler = new ShoppingListRequestHandler(mockController);
        defaultHeaders = new HashMap<>(){{
            put(Constants.USER, Constants.USER);
        }};
    }

    @Test
    void handleRequestErrorTest(){
        HashMap<String, String> headers = new HashMap<>();
        Request request = new Request("", "", headers, new Object());

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.ERROR);
    }

    @Test
    void handleRequestNotFoundTest(){
        Request request = new Request("", "Error", defaultHeaders, new Object());

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.NOT_FOUND);
    }

    @Test
    void handleRequestNotAllowedTest(){
        Request request = new Request(Router.SHOPPINGLIST, "Error", defaultHeaders, new Object());

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.NOT_ALLOWED);
    }

    @Test
    void handleGetError(){
        Request request = new Request(Router.SHOPPINGLIST, Request.GET, defaultHeaders, new Object());

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.ERROR);
    }

    @Test
    void handleGetBadRequest(){
        Request request = new Request(Router.SHOPPINGLIST, Request.GET, defaultHeaders, null);

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.BAD_REQUEST);
    }

    @Test
    void handleGetAllFromUserRequest(){
        when(mockController.getAllShoppingListsFromUser(any()))
                .thenReturn(requestHandler.ok(new Object()));
        Request request = new Request(Router.SHOPPINGLIST, Request.GET, defaultHeaders, new HashMap<>());

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.OK);
    }

    @Test
    void handleGetShoppingListRequest(){
        when(mockController.getShoppingList(any()))
                .thenReturn(requestHandler.ok(new Object()));
        HashMap<String, String> body = new HashMap<>(){{
            put(ShoppingListRequestHandler.Constants.SHOPPING_LIST_ID, ShoppingListRequestHandler.Constants.SHOPPING_LIST_ID);
        }};
        Request request = new Request(Router.SHOPPINGLIST, Request.GET, defaultHeaders, body);

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.OK);
    }

    @Test
    void handleDeleteRequestTest(){
        when(mockController.deleteShoppingList(any(), any()))
                .thenReturn(requestHandler.ok(null));
        Request request = new Request(Router.SHOPPINGLIST, Request.DELETE, defaultHeaders, null);

        Response response = requestHandler.handle(request);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.OK);
        Assert.isTrue(response.getBody() == null);
    }

    static class Constants{
        public static final String USER = "username";
    }
}
