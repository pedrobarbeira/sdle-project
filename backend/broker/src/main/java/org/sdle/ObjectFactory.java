package org.sdle;

import org.sdle.controller.UserController;
import org.sdle.handler.UserRequestHandler;
import org.sdle.repository.UserRepository;

public class ObjectFactory {

    public static UserRequestHandler initializeUserRequestHandler(){
        UserRepository repository = new UserRepository();
        repository.loadFromMemory();
        UserController controller = new UserController(repository);
        return new UserRequestHandler(controller);
    }
}
