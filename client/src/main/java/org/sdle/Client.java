package org.sdle;

import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

public class Client extends ApiComponent {
    public static final String AUTH = "api/auth";
    public static final String REPLICA = "api/replica";
    public static final String SHOPPINGLIST = "api/shoppinglist";
    private final HashMap<String, String> headers;
    private final ClientStub clientStub;

    public Client(ClientStub clientStub) {
        this.clientStub = clientStub;
        this.headers = new HashMap<>();
    }

    public boolean endSession(){
        Request request = new Request(AUTH, Request.POST, headers, null);
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
            put("username", username);
            put("password", encryptedPassword);
        }};
        Request request = new Request(AUTH, Request.GET, headers, body);
        Response response = clientStub.sendRequest(request);
        if(response.getStatus() == StatusCode.OK){
            String token = (String) response.getBody();
            headers.put(Headers.TOKEN, token);
            return true;
        }
        return false;
    }

    public List<String> getShoppingLists(){
        System.out.println("Fetching shopping lists");
        return List.of("Shopping List 1, Shopping List 2");
    }

    public String createShoppingList(String listName){
        System.out.println("Creating shopping list");
        return listName;
    }

    public String deleteShoppingList(String listName){
        System.out.println("Deleting shopping list");
        return listName;
    }

    public String addSharedUser(String listName, String target){
        System.out.printf("Adding user [%s] to list [%s}%n", target, listName);
        return "Successfully added user";
    }

    public String removeSharedUser(String listName, String target){
        System.out.printf("Removing user [%s] from list [%s}%n", target, listName);
        return "Successfully removed user";
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
}
