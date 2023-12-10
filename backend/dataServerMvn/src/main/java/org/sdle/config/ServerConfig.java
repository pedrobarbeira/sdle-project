package org.sdle.config;

import java.util.HashMap;

public class ServerConfig {
    public int maxActiveThreads;
    public int timeOut;
    public String mainNodeId;
    public String dataRoot;
    public String apiBase;
    public HashMap<String, NodeConfig> nodeMap;
    public HashMap<String, String> addressMap;
}
