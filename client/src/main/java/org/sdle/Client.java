package org.sdle;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.model.ItemOperationDataModel;
import org.sdle.model.ShareOperationDataModel;
import org.sdle.services.SessionService;

import java.io.*;
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
    private final ClientStub clientStub;

    private final SessionService sessionService;

    public Client(ClientStub clientStub, SessionService sessionService) {
        this.clientStub = clientStub;
        this.sessionService = sessionService;
    }

    public void endSession(){
        sessionService.clearHeaders();

        Request request = new Request(API_AUTH, Request.PUT, sessionService.headers, username);
        Response response = clientStub.sendRequest(request);
    }

    public boolean authenticate(String username, String password){
        String encryptedPassword = encrypt(password);
        HashMap<String, String> body = new HashMap<>(){{
            put(Constants.USERNAME, username);
            put(Constants.PASSWORD, encryptedPassword);
        }};
        Request request = new Request(API_AUTH, Request.GET, sessionService.headers, body);
        return openSession(username, request);
    }

    public boolean register(String username, String password){
        String encryptedPassword = encrypt(password);
        HashMap<String, String> body = new HashMap<>(){{
            put(Constants.USERNAME, username);
            put(Constants.PASSWORD, encryptedPassword);
        }};
        Request request = new Request(API_AUTH, Request.POST, sessionService.headers, body);
        return openSession(username, request);
    }

    private boolean openSession(String username, Request request){
        Response response = clientStub.sendRequest(request);
        if(response.getStatus() == StatusCode.OK){
            String token = (String) response.getBody();

            String key = encrypt(username);

            this.username = username;

            sessionService.persistHeaders(username, token, key);
            return true;
        }
        return false;
    }

    public String continueSession() {
        try {
            HashMap<String, String> map = sessionService.parseHeadersFile() ;
            if(map.containsKey(ApiComponent.Headers.TOKEN) && map.containsKey(ApiComponent.Headers.USER) && map.containsKey(ApiComponent.Headers.KEY)) {
                String mapKey = map.get(ApiComponent.Headers.KEY);
                String username = map.get(ApiComponent.Headers.USER);

                String key = encrypt(username);

                if(key.equals(mapKey)) {
                    sessionService.persistHeaders(username, map.get(ApiComponent.Headers.TOKEN), key);

                    return username;
                }
            }

            sessionService.clearHeaders();
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public List<String> getShoppingLists() {
        if(isLoggedIn()) {
            Request request = new Request(API_SHOPPINGLIST, Request.GET, sessionService.headers, null);
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
            Request request = new Request(API_SHOPPINGLIST, Request.POST, sessionService.headers, listName);
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
            Request request = new Request(API_SHOPPINGLIST, Request.POST, sessionService.headers, listName);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String deleteShoppingList(String targetId){
        if(isLoggedIn()) {
            Request request = new Request(API_SHOPPINGLIST, Request.DELETE, sessionService.headers, targetId);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String addSharedUser(String targetId, String username){
        if(isLoggedIn()) {
            ShareOperationDataModel body = new ShareOperationDataModel(targetId, username);

            Request request = new Request(API_SHARED, Request.PUT, sessionService.headers, body);

            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String removeSharedUser(String targetId, String username){
        if(isLoggedIn()) {
            ShareOperationDataModel body = new ShareOperationDataModel(targetId, username);
            Request request = new Request(API_SHARED, Request.DELETE, sessionService.headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public List<String> getItems(String targetId){
        if(isLoggedIn()){
            ItemOperationDataModel body = new ItemOperationDataModel(targetId);
            Request request = new Request(API_ITEMS, Request.GET, sessionService.headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (List<String>) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String createIem(String targetId, String itemName, int quantity){
        if(isLoggedIn()) {
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, quantity);
            Request request = new Request(API_SHARED, Request.POST, sessionService.headers, body);

            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String addQuantityToItem(String targetId, String itemName, int quantity){
        if(isLoggedIn()) {
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName, quantity);
            Request request = new Request(API_SHARED, Request.PUT, sessionService.headers, body);

            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }

    public String removeQuantityFromItem(String targetId, String  itemName, int quantity){
        int negQuantity = - quantity;
        return addQuantityToItem(targetId, itemName, negQuantity);
    }

    public String checkItem(String targetId, String itemName){
        //TODO change logic to simplify
        if(isLoggedIn()) {
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName);
            Request request = new Request(API_SHARED, Request.PUT, sessionService.headers, body);
            Response response = clientStub.sendRequest(request);
            if (response.getStatus() == StatusCode.OK) {
                return (String) response.getBody();
            }
            String message = (String) response.getBody();
            System.out.println(message);
        }
        return null;
    }


    public String removeItem(String targetId, String itemName){
        if(isLoggedIn()) {
            ItemOperationDataModel body = new ItemOperationDataModel(targetId, itemName);
            Request request = new Request(API_SHARED, Request.PUT, sessionService.headers, body);
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
        if(sessionService.headers.containsKey(Headers.TOKEN)){
            return true;
        }
        System.out.println("You must be logged in to issue this command");
        return false;
    }

    static class Constants{
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";

    }
}
