package org.sdle.api;

import org.sdle.controller.ShoppingListController;
import org.sdle.controller.UserController;
import org.sdle.model.ShoppingList;
import org.sdle.model.User;

public class ServerStub {
    ShoppingListController shoppingListController;
    UserController userController;

    public ServerStub(ShoppingListController shoppingListController,
                      UserController userController){
        this.shoppingListController = shoppingListController;
        this.userController = userController;
    }

    public User login(){
        return userController.login();
    }

    public void logout(){
        userController.logout();
    }

    public ShoppingList getShoppingList(){
        return shoppingListController.getShoppingList();
    }

    public ShoppingList addShoppingList(){
        return shoppingListController.addShoppingList();
    }

    public ShoppingList updateShoppingList(){
        return shoppingListController.updateShoppingList();
    }

    public void deleteShoppingList(){
        shoppingListController.deleteShoppingList();;
    }
}
