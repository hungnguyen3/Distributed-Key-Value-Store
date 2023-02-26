package com.g6.CPEN431.A7;

// Class representing a node in the hash ring
public class Node {
    private final String host;      // Host name or IP address of the node
    private final int port;         // Port number of the node
    private int startRange;   // Starting range of hash values that the node is responsible for
    private int endRange;     // Ending range of hash values that the node is responsible for

    int epidemicPort; // Port to be used for the epidemic protocol, so traffic does not get confused
    private int nodeID; // id of the node, this should also be the order of the node in the nodeList created in hashRing

    // Constructor to create a new Node with the specified host, port, and range
    public Node(String host, int port, int startRange, int endRange, int epidemicPort, int nodeID) {
        this.host = host;
        this.port = port;
        this.startRange = startRange;
        this.endRange = endRange;
        this.epidemicPort = epidemicPort;
        this.nodeID = nodeID;
    }

    // Method to check whether a given hashed value is in the range of hash values that the node is responsible for
    public boolean inRange(int hashedValue) {
        if (startRange <= endRange) {
            // Normal range (i.e., the node range does not wrap around the maximum hash value)
            return hashedValue >= startRange && hashedValue <= endRange;
        } else {
            // Wrap-around range (i.e., the node range spans the maximum hash value)
            return hashedValue >= startRange || hashedValue <= endRange;
        }
    }

    // Getter method to retrieve the host name or IP address of the node
    public String getHost() {
        return host;
    }

    // Getter method to retrieve the port number of the node
    public int getPort() {
        return port;
    }

    // Getter method to retrieve the starting range of hash values that the node is responsible for
    public int getStartRange() {
        return startRange;
    }

    // Getter method to retrieve the ending range of hash values that the node is responsible for
    public int getEndRange() {
        return endRange;
    }

    public void setStartRange(int startRange) {
        startRange = startRange;
    };

    public void setEndRange(int endRange) {
        endRange = endRange;
    }
}
