package org.sdle.api.controller;

import org.sdle.api.ApiComponent;
import org.sdle.repository.ShoppingListRepository;

public class ShoppingItemController extends ApiComponent {

    private final ShoppingListRepository repository;

    public ShoppingItemController(ShoppingListRepository repository) {
        this.repository = repository;
    }
}
