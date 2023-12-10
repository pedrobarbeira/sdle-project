package org.sdle;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.model.ItemOperationDataModel;
import org.sdle.model.ShareOperationDataModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client extends ApiComponent {
    public static final String API_AUTH = "api/auth";
    public static final String API_SHOPPINGLIST = "api/shoppinglist";
    public static final String API_SHARED = "api/shared";
    public static final String API_ITEMS = "api/items";
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
        Request request = new Request(API_AUTH, Request.PUT, headers, username);
        Response response = clientStub.sendRequest(request);
        if(response.getStatus() == StatusCode.OK){
            headers.remove(Headers.TOKEN);
            headers.remove(Headers.KEY);
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
        Request request = new Request(API_AUTH, Request.GET, headers, body);
        return openSession(username, request);
    }

    public boolean register(String username, String password){
        String encryptedPassword = encrypt(password);
        HashMap<String, String> body = new HashMap<>(){{
            put(Constants.USERNAME, username);
            put(Constants.PASSWORD, encryptedPassword);
        }};
        Request request = new Request(API_AUTH, Request.POST, headers, body);
        return openSession(username, request);
    }

    private boolean openSession(String username, Request request){
        Response response = clientStub.sendRequest(request);
        if(response.getStatus() == StatusCode.OK){
            String token = (String) response.getBody();
            headers.put(Headers.TOKEN, token);
            String key = encrypt(username);
            headers.put(Headers.KEY, key);
            this.username = username;
            return true;
        }
        return false;
    }

    public List<String> getShoppingLists() {
        if(isLoggedIn()) {
            Request request = new Request(API_SHOPPINGLIST, Request.GET, headers, null);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (List<String>) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return new ArrayList<>();
    }

    public String createShoppingList(String listName){
        if(isLoggedIn()) {
            Request request = new Request(API_SHOPPINGLIST, Request.POST, headers, listName);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String updateShoppingList(String listName, String newName){
        if(isLoggedIn()) {
            Request request = new Request(API_SHOPPINGLIST, Request.POST, headers, listName);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String deleteShoppingList(String target){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            Request request = new Request(API_SHOPPINGLIST, Request.DELETE, headers, targetId);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String addSharedUser(String target, String username){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ShareOperationDataModel body = new ShareOperationDataModel(targetId, username);
            Request request = new Request(API_SHARED, Request.POST, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String removeSharedUser(String target, String username){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ShareOperationDataModel body = new ShareOperationDataModel(targetId, username);
            Request request = new Request(API_SHARED, Request.DELETE, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public List<String> getItems(String target){
        if(isLoggedIn()){
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId);
            Request request = new Request(API_ITEMS, Request.GET, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (List<String>) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String createIem(String target, String itemName, int quantity){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, ItemOperations.CREATE, quantity);
            Request request = new Request(API_SHARED, Request.POST, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String addQuantityToItem(String target, String itemName, int quantity){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, ItemOperations.ADD, quantity);
            Request request = new Request(API_SHARED, Request.PUT, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String removeQuantityFromItem(String target, String  itemName, int quantity){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, ItemOperations.RM, quantity);
            Request request = new Request(API_SHARED, Request.PUT, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String checkItem(String target, String itemName){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, ItemOperations.CHECK);
            Request request = new Request(API_SHARED, Request.PUT, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String uncheckItem(String target, String itemName, int quantity){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, ItemOperations.UNCHECK, quantity);
            Request request = new Request(API_SHARED, Request.PUT, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String removeItem(String target, String itemName){
        if(isLoggedIn()) {
            String targetId = repository.getIdFromName(target);
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, ItemOperations.DELETE);
            Request request = new Request(API_SHARED, Request.PUT, headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
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

    private boolean isLoggedIn(){
        if(headers.containsKey(Headers.TOKEN)){
            return true;
        }
        System.out.println("You must be logged in to issue this command");
        return false;
    }

    static class Constants{
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";

    }

    static class ItemOperations{
        public static final String CREATE = "create";
        public static final String ADD = "add";
        public static final String RM = "rm";
        public static final String CHECK = "check";
        public static final String UNCHECK = "uncheck";
        public static final String DELETE = "delete";
    }
}
