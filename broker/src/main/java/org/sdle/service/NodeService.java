package org.sdle.service;

import org.sdle.config.BrokerConfig;
import org.sdle.config.NodeConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NodeService {
    private final HashMap<String, NodeConfig> prefixMap;
    private final HashMap<String, List<String>> replicaMap;

    private final Queue<String> prefixQueue;

    public NodeService(BrokerConfig config) {
        this.prefixMap = config.prefixMap;
        this.replicaMap = config.replicaMap;
        this.prefixQueue = new LinkedList<>();
        prefixQueue.addAll(prefixMap.keySet());
    }

    public String getNextPrefix(){
        String prefix = prefixQueue.remove();
        prefixQueue.add(prefix);
        return prefix;
    }

    public String getPrefixMainAddress(String prefix){
        NodeConfig config = prefixMap.get(prefix);
        return config.address;
    }

    public List<String> getPrefixReplicasAddress(String prefix){
        return replicaMap.get(prefix);
    }

}
