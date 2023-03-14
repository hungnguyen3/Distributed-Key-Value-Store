package com.g6.CPEN431.A7;

import java.net.DatagramPacket;

public class TransferRequest {
    public Node destinationNode;
    public int modulo;

    public TransferRequest(Node destinationNode, int modulo) {
        this.destinationNode = destinationNode;
        this.modulo = modulo;
    }
}


