package org.example.lib;

public class BoardNodePair {
    private Board b;
    private Node n;

    public BoardNodePair(Board b, Node n) {
        this.b = b;
        this.n = n;
    }

    public Board getBoard() {
        return b;
    }

    public Node getNode() {
        return this.n;
    }
}
