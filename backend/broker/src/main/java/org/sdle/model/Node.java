package org.sdle.model;

public class Node {
    String id;
    int port;

    public Node() {
    }
    public Node(String id, int port) {
        this.id = id;
        this.port = port;
    }
    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }
}
