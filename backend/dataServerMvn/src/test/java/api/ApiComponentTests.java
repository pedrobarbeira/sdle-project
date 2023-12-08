package api;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sdle.api.ApiComponent;
import org.sdle.api.Response;
import org.sdle.controller.IShoppingListController;
import org.sdle.api.handler.ShoppingListRequestHandler;

import static org.mockito.Mockito.*;

public class ApiComponentTests {

    private static ApiComponent requestHandler;

    @BeforeAll
    static void setup(){
        IShoppingListController controller = mock(IShoppingListController.class);
        requestHandler = new ShoppingListRequestHandler(controller);
    }

    @Test
    void buildEmptyResponseTest(){
        Response response = requestHandler.buildResponse(ApiComponent.StatusCode.OK, null);
        Assert.isTrue(response != null);
    }

    @Test
    void build200ResponseTest(){
        Object object = new Object();
        Response response = requestHandler.ok(object);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.OK);
        Assert.isTrue(response.getBody() == object);
    }

    @Test
    void build400ResponseTest(){
        Response response = requestHandler.badRequest();
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.BAD_REQUEST);
        Assert.isTrue(response.getBody().equals(ApiComponent.Message.BAD_REQUEST));
    }

    @Test
    void build401ResponseTest(){
        Response response = requestHandler.unauthorized();
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.UNAUTHORIZED);
        Assert.isTrue(response.getBody().equals(ApiComponent.Message.UNAUTHORIZED));
    }

    @Test
    void build404ResponseTest(){
        Response response = requestHandler.notFound();
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.NOT_FOUND);
        Assert.isTrue(response.getBody().equals(ApiComponent.Message.NOT_FOUND));
    }

    @Test
    void build405ResponseTest(){
        Response response = requestHandler.notAllowed();
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.NOT_ALLOWED);
        Assert.isTrue(response.getBody().equals(ApiComponent.Message.NOT_ALLOWED));
    }

    @Test
    void build500ResponseTest(){
        Response response = requestHandler.error();
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.ERROR);
        Assert.isTrue(response.getBody().equals(ApiComponent.Message.ERROR));
    }
}
