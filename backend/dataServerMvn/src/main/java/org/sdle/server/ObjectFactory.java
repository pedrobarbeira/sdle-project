package org.sdle.server;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.sdle.Main;
import org.sdle.config.ServerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class    ObjectFactory {
    public static String CONFIG_FILE;
    private static ServerConfig serverConfig;
    private static ExecutorService executorService;

    public static void setServerConfigFile(String configFile){
        CONFIG_FILE = configFile;
    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    public static ServerConfig getServerConfig() throws IOException {
        if(serverConfig == null){
            InputStream stream = Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            serverConfig = getMapper().readValue(stream, ServerConfig.class);
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
