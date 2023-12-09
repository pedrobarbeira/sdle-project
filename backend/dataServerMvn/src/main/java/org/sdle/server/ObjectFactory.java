package org.sdle.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.Main;
import org.sdle.config.ServerConfig;
import org.sdle.controller.ShoppingListController;
import org.sdle.repository.ShoppingListRepository;
import org.sdle.api.handler.ShoppingListRequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObjectFactory {
    public static final String CONFIG_FILE = "appsettings.json";
    private static ServerConfig serverConfig;
    private static ExecutorService executorService;

    public static ServerConfig getServerConfig() throws IOException {
        if(serverConfig == null){
            InputStream stream = Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            serverConfig = new ObjectMapper().readValue(stream, ServerConfig.class);
        }
        return serverConfig;
    }

    public static ExecutorService getExecutorService(){
        if(executorService == null){
            executorService = Executors.newFixedThreadPool(serverConfig.maxActiveThreads);
        }
        return executorService;
    }
}
