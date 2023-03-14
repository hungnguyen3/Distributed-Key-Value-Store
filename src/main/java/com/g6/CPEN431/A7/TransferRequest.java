package com.g6.CPEN431.A7;

public class TransferRequest {
    private Node destinationNode;
    private int range;

    public TransferRequest(Node destinationNode, int range) {
        this.destinationNode = destinationNode;
        this.range = range;
    }

    public Node getDestinationNode() {
        return destinationNode;
    }

    public int getRange() {
        return range;
    }
}
