package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.config.ServerConfig;
import org.sdle.controller.ShoppingListController;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.api.handler.ShoppingListRequestHandler;

import java.io.IOException;
import java.io.InputStream;

public class ObjectFactory {
    public static final String CONFIG_FILE = "appsettings.json";
    private static ServerConfig serverConfig;

    public static ServerConfig getServerConfig() throws IOException {
        if(serverConfig == null){
            InputStream stream = Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            serverConfig = new ObjectMapper().readValue(stream, ServerConfig.class);
        }
        return serverConfig;
    }
    public static  ShoppingListRequestHandler initializeShoppingListRequestHandler(String dataRoot) {
        ShoppingListRepository repository = new ShoppingListRepository(dataRoot);
        ShoppingListController controller = new ShoppingListController(repository);
        return new ShoppingListRequestHandler(controller);
    }
}
