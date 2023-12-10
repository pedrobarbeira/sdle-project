package org.sdle.api.controller;

import org.sdle.api.ApiComponent;
import org.sdle.repository.ShoppingListRepository;

public abstract class ListController extends ApiComponent{
    protected final ShoppingListRepository repository;

    protected ListController(ShoppingListRepository repository) {
        this.repository = repository;
    }
}