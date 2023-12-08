package controller;

import handler.ShoppingListRequestHandlerTest;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;
import org.sdle.controller.ShoppingListController;
import org.sdle.model.ShoppingItem;
import org.sdle.model.ShoppingList;
import org.sdle.repository.IShoppingListRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ShoppingListControllerTests {
    private static IShoppingListRepository mockRepository;
    private static ShoppingListController controller;

    @BeforeAll
    static void setup(){
        mockRepository = mock(IShoppingListRepository.class);
        controller = new ShoppingListController(mockRepository);
    }

    @Test
    void deleteShoppingListBadRequestTest(){
        Response response = controller.deleteShoppingList(null, null);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.BAD_REQUEST);
    }

    @Test
    void deleteShoppingListUnauthorizedTest(){
        ShoppingList shoppingList = buildDefaultShoppingList();
        when(mockRepository.getAuthorizedUsers(any()))
                .thenReturn(shoppingList.getAuthorizedUsers());

        Response response = controller.deleteShoppingList(shoppingList, Constants.NOT_USER);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.UNAUTHORIZED);
    }

    @Test
    void deleteShoppingListErrorTest(){
        ShoppingList shoppingList = buildDefaultShoppingList();
        when(mockRepository.getAuthorizedUsers(any()))
                .thenReturn(shoppingList.getAuthorizedUsers());
        when(mockRepository.delete(any()))
                .thenReturn(false);

        Response response = controller.deleteShoppingList(shoppingList, Constants.USER);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.ERROR);
    }


    @Test
    void deleteShoppingListOkTest(){
        ShoppingList shoppingList = buildDefaultShoppingList();
        when(mockRepository.getAuthorizedUsers(any()))
                .thenReturn(shoppingList.getAuthorizedUsers());
        when(mockRepository.delete(any()))
                .thenReturn(true);

        Response response = controller.deleteShoppingList(shoppingList, Constants.USER);
        Assert.isTrue(response.getStatus() == ApiComponent.StatusCode.OK);
    }

    private ShoppingList buildDefaultShoppingList(){
        return buildMockShoppingList(
                Constants.ID,
                Constants.ID,
                Constants.ID,
                new HashMap<>(),
                new HashSet<>(){{
                    add(Constants.USER);
                }}
        );
    }
    private ShoppingList buildMockShoppingList(
            String id,
            String primaryNodeId,
            String name,
            HashMap<String, ShoppingItem> items,
            Set<String> authorizedUsers){
        return new ShoppingList(id, primaryNodeId, name, items, authorizedUsers);
    }

    static class Constants{
        public static final String USER = "username";
        public static final String NOT_USER = "not-user";
        public static final String ID = "ID";
    }
}
