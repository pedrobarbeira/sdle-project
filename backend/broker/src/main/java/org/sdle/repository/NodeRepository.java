package org.sdle.repository;

import org.sdle.model.Node;

import java.util.*;

public class NodeRepository {
    Map<String, Node> nodeMap = new TreeMap<>();
    private static final Object countLock = new Object();

    public boolean addNode(Node node) {
        synchronized (countLock) {
            if (nodeMap.containsKey(node.getId())) return false;

            nodeMap.put(node.getId(), node);
        }

        return true;
    }

    public boolean removeNode(String id) {
        if (!nodeMap.containsKey(id)) return false;

        nodeMap.remove(id);

        return true;
    }

    public Node getNextNode(String id) {
        List<String> keyList = new ArrayList<>(nodeMap.keySet());

        if (keyList.isEmpty()) {
            return null;
        }

        Collections.sort(keyList);

        int idIndex = keyList.indexOf(id);
        String nextKey = keyList.get((idIndex + 1) % keyList.size());

        return nodeMap.get(nextKey);
    }
}
