package com.g6.CPEN431.A7;

import java.util.ArrayList;

// Class representing a node in the hash ring
public class Node {
    private final String host;      // Host name or IP address of the node
    private final int port;         // Port number of the node
    private final int nodeID; // id of the node, this should also be the order of the node in the nodeList created in hashRing
    private final int hashRingSize; // Number of nodes in the hashRing
    private ArrayList<Integer> rangeList; // List of ranges this node is responsible for.
    private int epidemicPort; // Port to be used for the epidemic protocol, so traffic does not get confused

    // Constructor to create a new Node with the specified host, port, and range
    public Node(String host, int port, int range, int epidemicPort, int nodeID, int hashRingSize) {
        this.host = host;
        this.port = port;
        ArrayList<Integer> newList = new ArrayList<>();
        newList.add(range);
        this.rangeList = newList;
        this.epidemicPort = epidemicPort;
        this.nodeID = nodeID;
        this.hashRingSize = hashRingSize;
    }

    // Method to check whether a given hashed value is in the range of hash values that the node is responsible for
    public boolean inRange(int hashedValue) {
        for(int i = 0; i < rangeList.size(); i++) {
            if(hashedValue % hashRingSize  == rangeList.get(i)) {
                return true;
            }
        }
        return false;
    }

    // Getter method to retrieve the host name or IP address of the node
    public String getHost() {
        return host;
    }

    // Getter method to retrieve the port number of the node
    public int getPort() {
        return port;
    }

    // Getter method to retrieve the list of ranges for this node.
    public ArrayList<Integer> getRangeList() {return rangeList;}

    // Add all the modulos into this node's moduloList
    public void addRanges(ArrayList<Integer> modulos) {
        for(int i = 0; i < modulos.size(); i++) {
            this.rangeList.add(modulos.get(i));
        }
    }

    // Add a single entry to the node's modulo list.
    public void addRange(int modulo) {
        this.rangeList.add(modulo);
    }

    // Remove a single entry from the node's modulo list.
    public void removeRange(int modulo) {
        for(int i = 0; i < rangeList.size(); i++) {
            if(rangeList.get(i) == modulo) {
                rangeList.remove(i);
            }
        }
    }

    // Clear all entries from the node's modulo list.
    public void clearRangeList() {
        rangeList.clear();
    }

    public int getEpidemicPort() {return epidemicPort;}
    public int getNodeID() {return nodeID;}

    public int getHashRingSize() {return hashRingSize;}
}
