package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        try {
            ServerConfig serverConfig = ObjectFactory.getServerConfig();
            for (NodeConfig nodeConfig : serverConfig.nodeMap.values()) {
                Node node = new Node(nodeConfig);
                node.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }
}