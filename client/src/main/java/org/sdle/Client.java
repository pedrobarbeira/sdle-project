package org.sdle;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.model.ListShareDataModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client extends ApiComponent {
    public static final String AUTH = "api/auth";
    public static final String REPLICA = "api/replica";
    public static final String SHOPPINGLIST = "api/shoppinglist";
    public static final String SHARE = "share";
    private String username;
    private final ShoppingListRepository repository;
    private final HashMap<String, String> headers;
    private final ClientStub clientStub;

    public Client(ShoppingListRepository repository, ClientStub clientStub) {
        this.repository = repository;
        this.clientStub = clientStub;
        this.headers = new HashMap<>();
    }

    public boolean endSession(){
        Request request = new Request(AUTH, Request.PUT, headers, username);
        Response response = clientStub.sendRequest(request);
        if(response.getStatus() == StatusCode.OK){
            headers.remove(Headers.TOKEN);
            return true;
        }
        return false;
    }

    public boolean authenticate(String username, String password){
        String encryptedPassword = encrypt(password);
        HashMap<String, String> body = new HashMap<>(){{
            put(Constants.USERNAME, username);
            put(Constants.PASSWORD, encryptedPassword);
        }};
        Request request = new Request(AUTH, Request.GET, headers, body);
        return openSession(username, request);
    }

    public boolean register(String username, String password){
        String encryptedPassword = encrypt(password);
        HashMap<String, String> body = new HashMap<>(){{
            put(Constants.USERNAME, username);
            put(Constants.PASSWORD, encryptedPassword);
        }};
        Request request = new Request(AUTH, Request.POST, headers, body);
        return openSession(username, request);
    }

    private boolean openSession(String username, Request request){
        Response response = clientStub.sendRequest(request);
        if(response.getStatus() == StatusCode.OK){
            String token = (String) response.getBody();
            headers.put(Headers.TOKEN, token);
            headers.put(Headers.USER, username);
            this.username = username;
            return true;
        }
        return false;
    }

    public List<String> getShoppingLists() {
        Request request = new Request(SHOPPINGLIST, Request.GET, headers, null);
        Response response = clientStub.sendRequest(request);
        if (response.getStatus() == StatusCode.OK) {
            return (List<String>) response.getBody();
        }
        String message = (String) response.getBody();
        System.out.println(message);
        return new ArrayList<>();
    }

    public String createShoppingList(String listName){
        Request request = new Request(SHOPPINGLIST, Request.POST, headers, listName);
        Response response = clientStub.sendRequest(request);
        if (response.getStatus() == StatusCode.OK) {
            return (String) response.getBody();
        }
        String message = (String) response.getBody();
        System.out.println(message);
        return null;
    }

    public String deleteShoppingList(String listName){
        Request request = new Request(SHOPPINGLIST, Request.DELETE, headers, listName);
        Response response = clientStub.sendRequest(request);
        if (response.getStatus() == StatusCode.OK) {
            return (String) response.getBody();
        }
        String message = (String) response.getBody();
        System.out.println(message);
        return null;
    }

    public String addSharedUser(String target, String username){
        String route = String.format("%s/%s", SHOPPINGLIST, SHARE);
        ListShareDataModel body = new ListShareDataModel(target, username);
        Request request = new Request(route, Request.POST, headers, body);
        Response response = clientStub.sendRequest(request);
        if (response.getStatus() == StatusCode.OK) {
            return (String) response.getBody();
        }
        String message = (String) response.getBody();
        System.out.println(message);
        return null;
    }

    public String removeSharedUser(String target, String username){
        String route = String.format("%s/%s", SHOPPINGLIST, SHARE);
        ListShareDataModel body = new ListShareDataModel(target, username);
        Request request = new Request(route, Request.DELETE, headers, body);
        Response response = clientStub.sendRequest(request);
        if (response.getStatus() == StatusCode.OK) {
            return (String) response.getBody();
        }
        String message = (String) response.getBody();
        System.out.println(message);
        return null;
    }

    public List<String> getItems(String listName){
        System.out.printf("Getting items from list [%s]%n", listName);
        return List.of("Item 1", "Item 2");
    }

    public String createIem(String listName, String itemName, int itemQuantity){
        System.out.printf("Creating item in list [%s] with name [%s} and quantity [%d}%n", listName, itemName, itemQuantity);
        return "Successfully created item";
    }

    public String addQuantityToItem(String listName, String itemName, int itemQuantity){
        System.out.printf("Adding [%d] to item [%s] in list [%s]%n", itemQuantity, itemName, listName);
        return "Successfully created item";
    }

    public String removeQuantityFromItem(String listName, String  itemName, int itemQuantity){
        System.out.printf("Removing [%d] from item [%s] in list [%s]%n", itemQuantity, itemName, listName);
        return "Successfully created item";
    }

    public String checkItem(String listName, String itemName){
        System.out.printf("Creating item in list [%s] with name [%s]%n", listName, itemName);
        return "Successfully created item";
    }

    public String uncheckItem(String listName, String itemName, int itemQuantity){
        System.out.printf("Unchecking item in list [%s] with name [%s] and quantity [%d]%n", listName, itemName, itemQuantity);
        return "Successfully unchecked item";
    }

    private String encrypt(String toEncrypt){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(toEncrypt.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static class Constants{
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }
}
