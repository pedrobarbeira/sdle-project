package org.sdle.model;

import java.util.HashMap;
import java.util.Set;

public class ShoppingListDataModel {
    public String id;
    public String name;
    public HashMap<String, ShoppingItemDataModel> items;
    public Set<String> authorizedUsers;
}
