package org.sdle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.controller.NodeController;
import org.sdle.controller.UserController;
import org.sdle.handler.NodeRequestHandler;
import org.sdle.handler.UserRequestHandler;

import org.sdle.repository.NodeRepository;
import org.sdle.repository.UserRepository;
import org.sdle.utils.UtilsHash;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class ObjectFactory {

    public static UserRequestHandler initializeUserRequestHandler(ExecutorService workers){
        UserRepository repository = new UserRepository();
        repository.loadFromMemory();
        UserController controller = new UserController(repository);
        return new UserRequestHandler(controller, workers);
    }

    public static NodeRequestHandler initializeNodeRequestHandler(ExecutorService workers) throws IOException {
        NodeRepository repository = new NodeRepository();

        String nodesPath = "config.json";

        InputStream stream = ClassLoader.getSystemResourceAsStream(nodesPath);
        if(stream != null) {
            List<Map<String, Object>> list = new ObjectMapper().readValue(stream, new TypeReference<>() {});

            for (Map<String, Object> item : list) {
                if (item.containsKey("port")) {
                    int port = (int) item.get("port");
                    repository.addNode(UtilsHash.generateMD5(String.valueOf(port)), port);
                }
            }

            stream.close();

        } else {
            throw new FileNotFoundException("File " + nodesPath + " was not found\n");
        }

        NodeController controller = new NodeController(repository);
        return new NodeRequestHandler(controller, workers);
    }
}
