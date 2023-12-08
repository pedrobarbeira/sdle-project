package repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sdle.model.ShoppingList;
import org.sdle.repository.ShoppingListRepository;
import java.util.*;

public class ShoppingListRepositoryTests {

    @Test
    public void createRepositoryTest() {
        ShoppingListRepository repository = new ShoppingListRepository(Constants.DATA_ROOT);
        Assert.isInstanceOf(ShoppingListRepository.class, repository);
    }


    @Test
    public void getByIdTestNotExistsTest() {
        ShoppingListRepository repository = new ShoppingListRepository(Constants.DATA_ROOT);

        ShoppingList result = repository.getById(Constants.NOT_ID);
        Assert.isNull(result);
    }

    @Test
    public void getByIdTestInCacheTest() throws JsonProcessingException {
        ShoppingList testList = new ShoppingList();
        HashMap<String, ShoppingList> testCache = new HashMap<>() {{
            put(Constants.ID, testList);
        }};
        ShoppingListRepository repository = new ShoppingListRepository(Constants.DATA_ROOT, testCache);

        ShoppingList result = repository.getById(Constants.ID);
        Assert.isInstanceOf(ShoppingList.class, result);
        Assert.isTrue(result == testList);
    }

    @Test
    public void getByIdTestInMemory() throws JsonProcessingException {
        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        ShoppingList result = repository.getById("test-id-2");

        Assert.isInstanceOf(ShoppingList.class, result);
        Assert.notNull(result.getId());
        Assert.notNull(result.getName());
        Assert.notNull(result.getItems());
    }

    @Test
    public void getAllTest() {
        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        List<ShoppingList> result = repository.getAll();
        Assert.notNull(result);
        Assert.isTrue(result.size() == 3);
    }

    @Test
    void putShoppingListTest() {
        ShoppingList shoppingList = new ShoppingList(
                Constants.PUT_ID,
                Constants.PRIMARY_NODE_ID,
                Constants.PUT_ID,
                new HashMap<>(), new HashSet<>());

        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        ShoppingList result = repository.put(shoppingList);
        Assert.notNull(result);
    }

    @Test
    void putShoppingListsTest() {
        List<ShoppingList> shoppingLists = new ArrayList<>();
        shoppingLists.add(new ShoppingList(
                Constants.PUT_ID,
                Constants.PRIMARY_NODE_ID,
                Constants.PUT_ID,
                new HashMap<>(), new HashSet<>())
        );

        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new LinkedHashMap<>(),
                getClass().getClassLoader()
        );

        List<ShoppingList> result = repository.put(shoppingLists);
        Assert.notNull(result);
        Assert.isTrue(result.size() == 1);
    }

    @Test
    void deleteShoppingListFailureTest() {
        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        boolean result = repository.delete(Constants.NOT_ID);
        Assert.isTrue(!result);
    }

    @Test
    void deleteShoppingListSuccessTest() {
        ShoppingList shoppingList = new ShoppingList(
                Constants.PUT_ID,
                Constants.PRIMARY_NODE_ID,
                Constants.PUT_ID,
                new HashMap<>(), new HashSet<>());
        HashMap<String, ShoppingList> cache = new HashMap<>() {{
            put(Constants.PUT_ID, shoppingList);
        }};
        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                cache, getClass().getClassLoader()
        );

        boolean result = repository.delete(Constants.PUT_ID);
        Assert.isTrue(result);
    }

    @Test
    void addAuthorizedUserTest() {
        ShoppingList shoppingList =new ShoppingList(
                Constants.PUT_ID,
                Constants.PRIMARY_NODE_ID,
                Constants.PUT_ID,
                new HashMap<>(), new HashSet<>());

        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(){{
                    put(Constants.PUT_ID, shoppingList);
                }},
                getClass().getClassLoader()
        );

        ShoppingList result = repository.addAuthorizedUser(Constants.PUT_ID, Constants.ID);

        Assert.notNull(result);
        Assert.isTrue(result.getAuthorizedUsers().size() == 1);
    }

    @Test
    void updateNotExistsTest(){
        ShoppingList shoppingList = new ShoppingList(
                Constants.NOT_ID,
                Constants.PRIMARY_NODE_ID,
                Constants.NOT_ID,
                new HashMap<>(), new HashSet<>());

        ShoppingListRepository repository = new ShoppingListRepository(Constants.DATA_ROOT);

        ShoppingList result = repository.update(shoppingList);
        Assert.isNull(result);
    }

    @Test
    void updateExistsInCacheTest(){
        ShoppingList shoppingList =new ShoppingList(
                Constants.PUT_ID,
                Constants.PRIMARY_NODE_ID,
                Constants.PUT_ID,
                new HashMap<>(), new HashSet<>());

        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(){{
                    put(Constants.PUT_ID, shoppingList);
                }},
                getClass().getClassLoader()
        );

        shoppingList.addAuthorizedUser(Constants.ID);

        ShoppingList result = repository.update(shoppingList);

        Assert.notNull(result);
        Assert.isTrue(result.getAuthorizedUsers().size() == 1);
    }

    @Test
    void updateExistsInMemoryTest(){
        ShoppingList shoppingList =new ShoppingList(
                Constants.ID,
                Constants.PRIMARY_NODE_ID,
                Constants.ID,
                new HashMap<>(), new HashSet<>());

        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        shoppingList.addAuthorizedUser(Constants.ID);

        ShoppingList result = repository.update(shoppingList);
        Assert.notNull(result);
        Assert.isTrue(result.getAuthorizedUsers().size() == 1);
    }

    @Test
    void getAllFromUserThrowsTest(){
        ShoppingListRepository repository = new ShoppingListRepository(Constants.NOT_ID);
        Assertions.assertThrows(IllegalStateException.class, () ->{
            repository.getAllFromUser(Constants.ID);
        });
    }

    @Test
    void getAllFromUserFindsNothingTest(){
        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        List<ShoppingList> result = repository.getAllFromUser(Constants.NOT_ID);
        Assert.isTrue(result.isEmpty());
    }

    @Test
    void getAllFromUserFindsItemsTest(){
        ShoppingListRepository repository = new ShoppingListRepository(
                Constants.DATA_ROOT,
                new HashMap<>(),
                getClass().getClassLoader()
        );

        List<ShoppingList> result = repository.getAllFromUser(Constants.ID);
        Assert.isTrue(result.size() == 2);
    }

    class Constants{
        public static final String DATA_ROOT = "node-id";
        public static final String ID = "test-id-1";
        public static final String NOT_ID = "test-not-id";
        public static final String PRIMARY_NODE_ID = "primary-node-id";
        public static final String DELETE_ID = "test-delete-id";
        public static final String PUT_ID = "test-put-id";
    }
}
