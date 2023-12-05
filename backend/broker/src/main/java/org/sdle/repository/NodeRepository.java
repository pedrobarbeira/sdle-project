package org.sdle.repository;

import java.util.*;

public class NodeRepository {
    Map<String, Integer> nodeMap = new HashMap<>();
    TreeMap<String, Integer> nodeTree = new TreeMap<>();
    //Queue<String> nodeQueue = new LinkedList<>();

    public void addNode(String id, int port) {
        nodeMap.put(id, port);
        nodeTree.put(id, port);

        /*if(!nodeQueue.contains(id))
            nodeQueue.add(id);*/
    }

    public int getNodeMapping(String id) {
        return nodeMap.get(id);
    }
    /*
    public String popQueue() {
        return nodeQueue.poll();
    }

    public void enQueue(String id) {
        nodeQueue.add(id);
    }*/

    public Map<String, Integer> getNodeMap() {
        return this.nodeMap;
    }

    public String getNextNodeId(String id) {
        String nodeId = this.nodeTree.ceilingKey(id);

        if(nodeId == null) {
            try{
                return this.nodeTree.firstKey();
            } catch (NoSuchElementException e) {
                System.err.println("Node tree is empty");
                return null;
            }
        }

        return nodeId;
    }
}
