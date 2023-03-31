package com.g6.CPEN431.A7;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private final String host;      // Host name or IP address of the node
    private final int port;         // Port number of the node
    private final int nodeID;       // id of the node, this should also be the order of the node in the nodeList created in hashRing
    private final int hashRingSize; // Number of nodes in the hashRing
    private final int epidemicPort; // Port to be used for the epidemic protocol, so traffic does not get confused
    private Set<Integer> rangeSet;  // Set of ranges this node is responsible for

    public Node(String host, int port, int range, int epidemicPort, int nodeID, int hashRingSize) {
        this.host = host;
        this.port = port;
        Set<Integer> newSet = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
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

    public Set<Integer> getRangeSet() {
        return rangeSet;
    }

    public boolean inRange(int hashedValue) {
        return rangeSet.contains(hashedValue % hashRingSize);
    }

    public void addRanges(Set<Integer> ranges) {
        this.rangeSet.addAll(ranges);
    }

    public void addRange(int range) {
        this.rangeSet.add(range);
    }

    public void removeRange(int range) {
        this.rangeSet.remove(range);
    }

    public void clearRangeSet() {
        rangeSet.clear();
    }
}
