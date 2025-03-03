package com.yule.open.database.data;

import java.util.List;

public class AnalyseResult {
    private List<Node> node;
    private List<List<Integer>> graph;

    public AnalyseResult(List<Node> node, List<List<Integer>> graph) {
        this.node = node;
        this.graph = graph;
    }

    public List<Node> getNode() {
        return node;
    }

    public List<List<Integer>> getGraph() {
        return graph;
    }
}
