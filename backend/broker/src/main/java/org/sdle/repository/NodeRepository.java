package org.sdle.repository;

import org.sdle.model.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class NodeRepository {
    Map<String, Integer> nodeMap = new HashMap<>();
    Queue<String> nodeQueue = new LinkedList<>();

    public String addNode(String id, int port) {
        nodeMap.put(id, port);
        if(!nodeQueue.contains(id))
            nodeQueue.add(id);
        return id;
    }

    public int getNodeMapping(String id) {
        return nodeMap.get(id);
    }

    public String popQueue() {
        return nodeQueue.poll();
    }

    public void enQueue(String id) {
        nodeQueue.add(id);
    }

    public Map<String, Integer> getNodeMap() {
        return this.nodeMap;
    }
}
