package org.sdle;

import org.sdle.config.NodeConfig;
import org.sdle.config.ServerConfig;
import org.sdle.server.Node;
import org.sdle.server.ObjectFactory;

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