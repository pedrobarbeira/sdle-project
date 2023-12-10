package org.sdle.config;

import java.util.HashMap;
import java.util.List;

public class BrokerConfig {
    public int maxActiveThreads;
    public int port;
    public String apiBase;
    public String dataRoot;
    public HashMap<String, NodeConfig> prefixMap;
    public HashMap<String, List<String>> replicaMap;
}
