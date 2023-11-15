package org.sdle;

import org.sdle.controller.NodeController;
import org.sdle.controller.UserController;
import org.sdle.handler.NodeRequestHandler;
import org.sdle.handler.UserRequestHandler;
import org.sdle.repository.NodeRepository;
import org.sdle.repository.UserRepository;

public class ObjectFactory {

    public static UserRequestHandler initializeUserRequestHandler(){
        UserRepository repository = new UserRepository();
        repository.loadFromMemory();
        UserController controller = new UserController(repository);
        return new UserRequestHandler(controller);
    }

    public static NodeRequestHandler initializeNodeRequestHandler() {
        NodeRepository repository = new NodeRepository();
        NodeController controller = new NodeController(repository);
        return new NodeRequestHandler(controller);
    }
}
