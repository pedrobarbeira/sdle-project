package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ObjectFactory {
    private static final String CONFIG_FILE = "app-settings.json";
    private static ClientConfig clientConfig;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ClientConfig getClientConfig() throws IOException {
        if(clientConfig == null){
            ClassLoader loader = Main.class.getClassLoader();
            String path = Objects.requireNonNull(loader.getResource(CONFIG_FILE)).getPath();
            File file = new File(path);
            clientConfig = mapper.readValue(file, ClientConfig.class);
        }
        return clientConfig;
    }

    public static ObjectMapper getMapper(){
        return mapper;
    }
}
