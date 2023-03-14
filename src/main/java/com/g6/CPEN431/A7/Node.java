package com.g6.CPEN431.A7;

import java.util.HashSet;
import java.util.Set;

// Class representing a node in the hash ring
public class Node {
    private final String host;      // Host name or IP address of the node
    private final int port;         // Port number of the node
    private final int nodeID;       // id of the node, this should also be the order of the node in the nodeList created in hashRing
    private final int hashRingSize; // Number of nodes in the hashRing
    private final int epidemicPort; // Port to be used for the epidemic protocol, so traffic does not get confused
    private Set<Integer> rangeSet;  // Set of ranges this node is responsible for

    // Constructor to create a new Node with the specified host, port, and range
    public Node(String host, int port, int range, int epidemicPort, int nodeID, int hashRingSize) {
        this.host = host;
        this.port = port;
        Set<Integer> newSet = new HashSet<>();
        newSet.add(range);
        this.rangeSet = newSet;
        this.epidemicPort = epidemicPort;
        this.nodeID = nodeID;
        this.hashRingSize = hashRingSize;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getEpidemicPort() {
        return epidemicPort;
    }

    public int getNodeID() {
        return nodeID;
    }

    public int getHashRingSize() {
        return hashRingSize;
    }

    // Getter method to retrieve the set of ranges for this node.
    public Set<Integer> getRangeSet() {
        return rangeSet;
    }

    // Method to check whether a given hashed value is in the range of hash values that the node is responsible for
    public boolean inRange(int hashedValue) {
        for(int range : rangeSet) {
            if(hashedValue % hashRingSize == range) {
                return true;
            }
        }
        return false;
    }

    // Add all the ranges into this node's rangeSet
    public void addRanges(Set<Integer> ranges) {
        this.rangeSet.addAll(ranges);
    }

    // Add a single entry to the node's range set.
    public void addRange(int range) {
        this.rangeSet.add(range);
    }

    // Remove a single entry from the node's range set.
    public void removeRange(int range) {
        this.rangeSet.remove(range);
    }

    // Clear all entries from the node's range set.
    public void clearRangeSet() {
        rangeSet.clear();
    }
}
