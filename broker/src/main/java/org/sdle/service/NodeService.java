package org.sdle.service;

import org.sdle.config.BrokerConfig;
import org.sdle.config.NodeConfig;

import java.util.HashMap;
import java.util.List;

public class NodeService {
    private final HashMap<String, NodeConfig> prefixMap;
    private final HashMap<String, List<String>> replicaMap;

    public NodeService(BrokerConfig config) {
        prefixMap = config.prefixMap;
        replicaMap = config.replicaMap;
    }
}
