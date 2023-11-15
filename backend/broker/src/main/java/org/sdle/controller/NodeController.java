package org.sdle.controller;

import org.sdle.model.Node;
import org.sdle.repository.NodeRepository;

public class NodeController {
    private final NodeRepository repository;

    public NodeController(NodeRepository repository) {
        this.repository = repository;
    }

    public boolean addNode(Node node) {
        return repository.addNode(node);
    }
}
