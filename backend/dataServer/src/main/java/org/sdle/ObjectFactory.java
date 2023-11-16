package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.configuration.ServerConfig;
import org.sdle.controller.ShoppingListController;
import org.sdle.handler.ShoppingRequestHandler;
import org.sdle.service.ShoppingListRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ObjectFactory {
    public static final String CONFIG_FILE = "appsettings.json";
    private static ServerConfig serverConfig;
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper(){
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static ServerConfig loadServerConfig() throws IOException {
        InputStream stream = ObjectFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        serverConfig = getObjectMapper().readValue(stream, ServerConfig.class);
        return serverConfig;
    }

    public static ShoppingRequestHandler initializeShoppingRequestHandler(){
        ShoppingListRepository repository = new ShoppingListRepository(serverConfig.getDataRoot());
        ShoppingListController controller = new ShoppingListController(repository);
        return new ShoppingRequestHandler(controller);
    }
}
