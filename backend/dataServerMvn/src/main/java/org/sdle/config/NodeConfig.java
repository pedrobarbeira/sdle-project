package org.sdle.config;

import java.util.List;

public class NodeConfig {
    public int port;
    public int threadNum;
    public String nodeId;
    public List<String> replicatedOn;
    public List<String> replicates;
}
