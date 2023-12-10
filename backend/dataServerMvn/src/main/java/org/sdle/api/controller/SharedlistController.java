package org.sdle.api.controller;

import org.sdle.api.ApiComponent;
import org.sdle.repository.ShoppingListRepository;

public class SharedlistController extends ApiComponent {
    private final ShoppingListRepository repository;

    public SharedlistController(ShoppingListRepository repository) {
        this.repository = repository;
    }
}
