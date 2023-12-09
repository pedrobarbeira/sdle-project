package org.sdle.config;

import java.util.HashMap;

public class ServerConfig {
    public int maxActiveThreads;
    public int timeOut;
    public String mainDataRoot;
    public String apiBase;
    public HashMap<String, NodeConfig> nodeMap;
    public HashMap<String, String> addressMap;
}
