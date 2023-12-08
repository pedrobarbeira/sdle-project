package handler;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.IShoppingListController;
import org.sdle.handler.ShoppingListRequestHandler;

import java.util.HashMap;

public class ShoppingListRequestHandlerTest {
    private static HashMap<String, String> headers;

    @BeforeAll
    private static void loadHeaders(){
        headers = new HashMap<>(){{
            put("username", "test");
        }};
    }

    @Test
    void handleRequestErrorNotFoundTest(){
        IShoppingListController controller = mock(IShoppingListController.class);
        ShoppingListRequestHandler requestHandler = new ShoppingListRequestHandler(controller);

        Request request = new Request("", "Error", headers, new Object());
        Response response = requestHandler.handle(request);

        Assert.isTrue(response.getStatus() == 404);
        Assert.isTrue(response.getBody().equals("Not Found"));
    }

    @Test
    void handleRequestErrorNotAllowedTest(){
        IShoppingListController controller = mock(IShoppingListController.class);
        ShoppingListRequestHandler requestHandler = new ShoppingListRequestHandler(controller);

        Request request = new Request(Router.SHOPPINGLIST, "Error", headers, new Object());
        Response response = requestHandler.handle(request);

        Assert.isTrue(response.getStatus() == 405);
        Assert.isTrue(response.getBody().equals("Method not allowed"));
    }

    @Test
    void handleRequestPostPutBadRequestWrongBodyTest(){
        IShoppingListController controller = mock(IShoppingListController.class);
        ShoppingListRequestHandler requestHandler = new ShoppingListRequestHandler(controller);

        Request request = new Request("", Request.PUT, new Object(), new Object());
        Response response = requestHandler.handle(request);

        Assert.isTrue(response.getStatus() == 400);

    }

}
