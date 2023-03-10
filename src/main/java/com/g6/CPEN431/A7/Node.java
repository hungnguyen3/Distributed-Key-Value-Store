package com.g6.CPEN431.A7;

import java.util.ArrayList;

// Class representing a node in the hash ring
public class Node {
    private final String host;      // Host name or IP address of the node
    private final int port;         // Port number of the node
    private ArrayList<Integer> moduloList; // List of modulo's this node is responsible for.

    private int epidemicPort; // Port to be used for the epidemic protocol, so traffic does not get confused

    private int nodeID; // id of the node, this should also be the order of the node in the nodeList created in hashRing

    private Boolean isAwake;

    // Constructor to create a new Node with the specified host, port, and range
    public Node(String host, int port, int modulo, int epidemicPort, int nodeID) {
        this.host = host;
        this.port = port;
        ArrayList<Integer> newList = new ArrayList<>();
        newList.add(modulo);
        this.moduloList = newList;
        this.epidemicPort = epidemicPort;
        this.nodeID = nodeID;
    }

    // Method to check whether a given hashed value is in the range of hash values that the node is responsible for
    public boolean inRange(int hashedValue) {
        for(int i = 0; i < moduloList.size(); i++) {
            if(hashedValue % 20  == moduloList.get(i)) {
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

    // Getter method to retrieve the list of modulos for this node.
    public ArrayList<Integer> getModuloList() {return moduloList;}

    // Replace the node's modulo list with the new list.
    public void replaceModuloList(ArrayList<Integer> moduloList) {
        for(int i = 0; i < moduloList.size(); i++) {
            this.moduloList.add(moduloList.get(i));
        }
    }

    // Add a single entry to the node's modulo list.
    public void addModulo(int modulo) {
        this.moduloList.add(modulo);
    }

    // Remove a single entry from the node's modulo list.
    public void removeModulo(int modulo) {
        for(int i = 0; i < moduloList.size(); i++) {
            if(moduloList.get(i) == modulo) {
                moduloList.remove(i);
            }
        }
    }

    // Clear all entries from the node's modulo list.
    public void clearModuloList() {
        moduloList.clear();
    }

    public int getEpidemicPort() {return epidemicPort;}
    public int getNodeID() {return nodeID;}

}
