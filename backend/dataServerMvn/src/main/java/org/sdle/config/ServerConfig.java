package org.sdle.config;

import java.util.HashMap;

public class ServerConfig {
    public int maxActiveThreads;
    public int timeOut;
    public HashMap<String, NodeConfig> nodeMap;
}
