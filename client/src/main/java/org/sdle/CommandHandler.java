package org.sdle;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private final Client client;

    public CommandHandler(Client client) {
        this.client = client;
    }

    public void handle(String command){
        List<String> tokens = new ArrayList<>(List.of(command.split(" ")));
        String token = tokens.remove(0);

        switch (token) {
            case CommandTypes.AUTH -> handleAuthCommand(tokens);
            case CommandTypes.LISTS -> handleListsCommand(tokens);
            case CommandTypes.ITEMS -> handleItemsCommand(tokens);
            case CommandTypes.HELP -> handleHelpCommand(tokens);
            case CommandTypes.EXIT -> handleExitCommand(tokens);
            default -> handleError(command);
        }
    }

    private void handleAuthCommand(List<String> tokens){
        String token = tokens.get(0);
        switch(token){
            case AuthCommand.REGISTER -> handleAuthCommandRegister(tokens);
            case AuthCommand.LOGOUT -> handleAuthCommandLogout();
            default -> handleAuthCommandLogin(tokens);
        }
    }

    private void handleAuthCommandRegister(List<String> tokens){
        if(tokens.size() != CommandLength.AUTH_REGISTER){
            System.out.println("Invalid credentials");
            return;
        }
        String username = tokens.get(AuthCommand.USERNAME);
        String password = tokens.get(AuthCommand.PASSWORD);
        if(client.register(username, password)){
            System.out.println("Account successfully created");
            System.out.printf("Welcome, %s%n", username);
        }else{
            System.out.println("Could not create account");
        }
    }

    private void handleAuthCommandLogout(){
        System.out.println("Logging out");
        if(client.endSession()) {
            System.out.println("See you next time");
        }else{
            System.out.println("Couldn't logout");
        }
    }

    private void handleAuthCommandLogin(List<String> tokens){
        if(tokens.size() != CommandLength.AUTH_LOGIN){
            System.out.println("Invalid credentials");
            return;
        }
        String username = tokens.get(AuthCommand.USERNAME);
        String password = tokens.get(AuthCommand.PASSWORD);
        if(client.authenticate(username, password)){
            System.out.printf("Welcome, %s%n", username);
        }else{
            System.out.println("Invalid credentials");
        }
    }

    private void handleListsCommand(List<String> tokens){
        String token = tokens.get(0);
        switch(token){
            case ListsCommand.SHOW -> handleListsShowCommand(tokens);
            case ListsCommand.CREATE -> handleListsCreateCommand(tokens);
            case ListsCommand.REMOVE -> handleListsDeleteCommand(tokens);
            default -> handleListsShareCommand(tokens);
        }
    }

    private void handleListsShowCommand(List<String> tokens){
        if(tokens.size() > CommandLength.LISTS_OPERATION){
            handleListsCommandError(tokens);
            return;
        }
        List<String> shoppingLists = client.getShoppingLists();
        for(String list : shoppingLists){
            System.out.println(list);
        }
    }

    private void handleListsCreateCommand(List<String> tokens){
        if(tokens.size() != CommandLength.LISTS_OPERATION){
            handleListsCommandError(tokens);
            return;
        }
        String listName = tokens.get(ListsCommand.LIST_NAME);
        String shoppingList = client.createShoppingList(listName);
        if(shoppingList != null) {
            System.out.println(shoppingList);
        }
    }

    private void handleListsDeleteCommand(List<String> tokens){
        if(tokens.size() != CommandLength.LISTS_OPERATION){
            handleListsCommandError(tokens);
            return;
        }
        String listName = tokens.get(ListsCommand.LIST_NAME);
        String shoppingList = client.deleteShoppingList(listName);
        if(shoppingList != null) {
            System.out.println(shoppingList);
        }
    }

    private void handleListsShareCommand(List<String> tokens){
        if(tokens.size() != CommandLength.LISTS_SHARE){
            handleListsCommandError(tokens);
            return;
        }
        String command = tokens.get(ListsCommand.SHARE_COMMAND);
        if(!command.equals(ListsCommand.SHARE)){
            handleListsCommandError(tokens);
            return;
        }
        String option = tokens.get(ListsCommand.SHARE_OPTION);
        String target = tokens.get(ListsCommand.SHARE_LIST_NAME);
        String username = tokens.get(ListsCommand.SHARE_TARGET);
        String message = "";
        switch(option){
            case CommandOptions.ADD -> message = client.addSharedUser(target, username);
            case CommandOptions.REMOVE -> message = client.removeSharedUser(target, username);
            default -> handleListsCommandError(tokens);
        }
        System.out.println(message);
    }

    private void handleListsCommandError(List<String> tokens){
        String error = rebuildCommand(CommandTypes.LISTS, tokens);
        handleError(error);
    }

    private void handleItemsCommand(List<String> tokens){
        if(tokens.size() < CommandLength.ITEMS_MIN){
            handleItemsCommandError(tokens);
            return;
        }
        String token = tokens.get(1);
        switch(token){
            case ItemsCommand.SHOW -> handleItemsShowCommand(tokens);
            case ItemsCommand.CREATE -> handleItemsCreateCommand(tokens);
            case ItemsCommand.REMOVE -> handleItemsRemoveCommand(tokens);
            default -> handleItemsOperationCommand(tokens);
        }
    }

    private void handleItemsShowCommand(List<String> tokens){
        if(tokens.size() > ItemsCommand.SHOW_LENGTH){
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        List<String> items = client.getItems(listName);
        for(String item : items){
            System.out.println(item);
        }
    }

    private void handleItemsCreateCommand(List<String> tokens) {
        if (tokens.size() > CommandLength.ITEMS_CREATE_MAX) {
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        String itemName = getItemsCreateName(tokens);
        int itemQuantity = 0;
        try {
            if (tokens.size() == CommandLength.ITEMS_CREATE_MAX) {
                String quantity = getItemsCreateQuantity(tokens);
                itemQuantity = Integer.parseInt(quantity);
            }
            String result = client.createIem(listName, itemName, itemQuantity);
            System.out.println(result);
        } catch (NumberFormatException e) {
            handleItemsCommandError(tokens);
        }
    }

    private void handleItemsOperationCommand(List<String> tokens){
        if(tokens.size() < CommandLength.ITEMS_OPERATION_MIN || tokens.size() > CommandLength.ITEMS_OPERATION_MAX){
            handleItemsCommandError(tokens);
            return;
        }
        String operation = getItemsOperationName(tokens);
        switch(operation){
            case CommandOptions.ADD -> handleItemsCommandOperationsAdd(tokens);
            case CommandOptions.REMOVE -> handleItemsCommandOperationsRemove(tokens);
            case ItemsCommand.CHECK -> handleItemsCommandCheck(tokens);
            case ItemsCommand.UNCHECK -> handleItemsCommandUncheck(tokens);
            default -> handleItemsCommandError(tokens);
        }
    }

    private void handleItemsCommandOperationsAdd(List<String> tokens){
        if(tokens.size() != CommandLength.ITEMS_OPERATION_MAX){
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        String itemName = getItemsOperationName(tokens);
        String quantity = getItemsOperationQuantity(tokens);
        try {
            int itemQuantity = Integer.parseInt(quantity);
            String result = client.addQuantityToItem(listName, itemName, itemQuantity);
            System.out.println(result);
        }catch(NumberFormatException e){
            handleItemsCommandError(tokens);
        }
    }

    private void handleItemsCommandOperationsRemove(List<String> tokens) {
        if (tokens.size() != CommandLength.ITEMS_OPERATION_MAX) {
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        String itemName = getItemsOperationName(tokens);
        String quantity = getItemsOperationQuantity(tokens);
        try {
            int itemQuantity = Integer.parseInt(quantity);
            String result = client.removeQuantityFromItem(listName, itemName, itemQuantity);
            System.out.println(result);
        } catch (NumberFormatException e) {
            handleItemsCommandError(tokens);
        }
    }

    private void handleItemsCommandCheck(List<String> tokens){
        if(tokens.size() != CommandLength.ITEMS_OPERATION_CHECK){
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        String itemName = getItemsOperationName(tokens);
        String result = client.checkItem(listName, itemName);
        System.out.println(result);
    }

    private void handleItemsCommandUncheck(List<String> tokens) {
        if (tokens.size() > CommandLength.ITEMS_OPERATION_MAX) {
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        String itemName = getItemsOperationName(tokens);
        int itemQuantity = 0;
        try {
            if (tokens.size() == CommandLength.ITEMS_OPERATION_MAX) {
                String quantity = getItemsOperationQuantity(tokens);
                itemQuantity = Integer.parseInt(quantity);
            }
            String result = client.uncheckItem(listName, itemName, itemQuantity);
            System.out.println(result);
        } catch (NumberFormatException e) {
            handleItemsCommandError(tokens);
        }
    }

    private void handleItemsRemoveCommand(List<String> tokens) {
        if (tokens.size() > CommandLength.ITEMS_CREATE_MAX) {
            handleItemsCommandError(tokens);
            return;
        }
        String listName = getItemsListName(tokens);
        String itemName = getItemsCreateName(tokens);
        int itemQuantity = 0;
        String result = client.removeItem(listName, itemName);
        System.out.println(result);
    }

    private void handleItemsCommandError(List<String> tokens){
        String error = rebuildCommand(CommandTypes.ITEMS, tokens);
        handleError(error);
    }

    private String getItemsListName(List<String> tokens){
        return tokens.get(ItemsCommand.LIST_NAME);
    }

    private String getItemsCreateName(List<String> tokens){
        return tokens.get(ItemsCommand.CREATE_NAME);
    }

    private String getItemsCreateQuantity(List<String> tokens){
        return tokens.get(ItemsCommand.CREATE_QUANTITY);
    }

    private String getItemsOperationName(List<String> tokens){
        return tokens.get(ItemsCommand.OPERATION_NAME);
    }

    private String getItemsOperationQuantity(List<String> tokens){
        return tokens.get(ItemsCommand.OPERATION_QUANTITY);
    }

    private void handleHelpCommand(List<String> tokens) {
        if (!tokens.isEmpty()) {
            handleError(CommandTypes.HELP);
        }
        System.out.println("""
                # Auth
                auth register <username> <password>
                auth <username> <password>
                auth logout

                # ShoppingList
                lists ls
                lists create <list-name>
                lists shared <list-name> add <user-name>
                lists shared <list-name> rm <user-name>
                lists delete <list-name>

                # ShoppingItem
                items <list-name> ls
                items <list-name> create <item-name>
                items <list-name> create <item-name> <quantity>
                items <list-name> <item-name> add <quantity>
                items <list-name> <item-name> rm <quantity>
                items <list-name> <item-name> check
                items <list-name> <item-name> uncheck
                items <list-name> <item-name> uncheck <quantity>
                items <list-name> <item-name> rm
                # Misc
                help
                exit
                """);
    }

    private void handleExitCommand(List<String> tokens){
        if(!tokens.isEmpty()){
            handleError(CommandTypes.EXIT);
        }
        System.out.println("Closing application");
        System.exit(0);
    }

    private String rebuildCommand(String commandType, List<String> tokens){
        tokens.add(0, commandType);
        return String.join(" ", tokens);
    }

    private void handleError(String command){
        System.out.printf("Invalid command [%s]%n", command);
    }



    static class CommandTypes{
        public static final String AUTH = "auth";
        public static final String LISTS = "lists";
        public static final String ITEMS = "items";
        public static final String HELP = "help";
        public static final String EXIT = "exit";
    }

    static class AuthCommand {
        public static final String REGISTER = "register";
        public static final String LOGOUT = "logout";
        public static final int USERNAME = 0;
        public static final int PASSWORD = 1;
    }

    static class ListsCommand {
        public static final String SHOW = "ls";
        public static final String CREATE = "create";
        public static final String SHARE = "shared";
        public static final String REMOVE = "rm";
        public static final int LIST_NAME = 1;
        public static final int SHARE_COMMAND = 0;
        public static final int SHARE_LIST_NAME = 1;
        public static final int SHARE_OPTION = 2;
        public static final int SHARE_TARGET = 3;
    }

    static class ItemsCommand {
        public static final String SHOW = "ls";
        public static final String CREATE = "create";
        public static final String CHECK = "check";
        public static final String UNCHECK = "uncheck";
        public static final String REMOVE = "rm";
        public static final int SHOW_LENGTH = 2;
        public static final int LIST_NAME = 0;
        public static final int CREATE_NAME = 1;
        public static final int CREATE_QUANTITY = 2;
        public static final int OPERATION_NAME = 2;
        public static final int OPERATION_QUANTITY = 3;
    }

    static class CommandOptions{
        public static final String ADD = "add";
        public static final String REMOVE = "rm";
    }

    static class CommandLength{
        public static final int AUTH_REGISTER =3 ;
        public static final int AUTH_LOGIN = 2;
        public static final int LISTS_OPERATION = 2;
        public static final int LISTS_SHARE = 4;
        public static final int ITEMS_MIN = 2;
        public static final int ITEMS_CREATE_MAX = 4;
        public static final int ITEMS_OPERATION_MIN = 3;
        public static final int ITEMS_OPERATION_MAX = 4;
        public static final int ITEMS_OPERATION_CHECK = 3;
    }
}
