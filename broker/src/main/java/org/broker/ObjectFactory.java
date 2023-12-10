package org.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.broker.config.BrokerConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObjectFactory {
    private static final String CONFIG_FILE = "app-settings.json";
    private static BrokerConfig brokerConfig;
    private static ExecutorService executorService;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static BrokerConfig getBrokerConfig() throws IOException {
        if(brokerConfig == null){
            ClassLoader loader = Main.class.getClassLoader();
            String path = Objects.requireNonNull(loader.getResource(CONFIG_FILE)).getPath();
            File file = new File(path);
            brokerConfig = mapper.readValue(file, BrokerConfig.class);
        }
        return brokerConfig;
    }

    public static ExecutorService getExecutorService(){
        if(executorService == null){
            executorService = Executors.newFixedThreadPool(brokerConfig.maxActiveThreads);
        }
        return executorService;
    }
}
