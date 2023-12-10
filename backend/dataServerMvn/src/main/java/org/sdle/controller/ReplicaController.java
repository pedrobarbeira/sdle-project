package org.sdle.controller;

import org.sdle.api.ApiComponent;
import org.sdle.api.Response;
import org.sdle.repository.Cache;
import org.sdle.repository.ShoppingListRepository;

public class ReplicaController extends ApiComponent {

    private final ShoppingListRepository repository;

    public ReplicaController(ShoppingListRepository repository){
        this.repository = repository;
    }

    public Response getData(){
        Cache data = this.repository.getCache();
        return ok(data);
    }
}
