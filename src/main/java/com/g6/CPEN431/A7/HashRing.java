package com.g6.CPEN431.A7;

import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HashRing {
    //Assumes there is at least 1 node after initialization.
    private ArrayList<Node> nodes;


    private LinkedHashMap<Integer, Node> nodeCache;

    private Epidemic epidemic;
    private String myAddress;
    private int myPort;
    private int myID;

    // Constructor to create a new HashRing given a configuration file path.
    public HashRing(String configFilePath, String myAddress, int myPort) {
        this.myAddress = myAddress;
        this.myPort = myPort;
        nodes = new ArrayList<>();

        nodeCache = new LinkedHashMap<Integer, Node>(200, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Node> eldest) {
                return size() > 200;
            }
        };

        int myID = 0;

        // Try to read the configuration file and create a Node object for each line
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");

                // Parse the hostname and port number from the line
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                if(host.equals(myAddress) && port == myPort){
                    myID = i;
                }

                // Divide the range of hash values evenly between the nodes in the ring
                int startRange = i * (Integer.MAX_VALUE / 20);
                int endRange = (i + 1) * (Integer.MAX_VALUE / 20) - 1;

                int epidemicPort = 20000 + i;

                // Add a new node with the host, port, and range to the list of nodes in the ring
                nodes.add(new Node(host, port, startRange, endRange, epidemicPort, i));
                //Initialize all nodes as alive at the beginning
                i++;
            }
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.err.println("Error: file not found: " + configFilePath);
        } catch (IOException e) {
            // If there is an error reading the file, throw a RuntimeException with the file path and cause
            throw new RuntimeException("Error reading servers.txt file: " + configFilePath, e);
        }

        //try to start the epidemic protocol and print error message if it fails
        epidemic = new Epidemic((ArrayList<Node>)nodes.clone(), myID, 500, 20000 + myID, 20);
        this.myID = myID;
        try {
            epidemic.startEpidemic();
        } catch (Exception e){
            System.out.println("Could not start epidemic protocol on this node");
        }
    }

    // Method to get the node responsible for a given key
    public Node getNodeForKey(byte[] key_byte_array) {
        // Calculate the hash value of the key using the Murmur3 hash function
        System.out.println("I am Node: " + myAddress + myPort + " ID:" + myID);
        int hash = HashUtils.hash(key_byte_array);
        // Check the cache for the node responsible for the key
        Node cachedNode = nodeCache.get(hash);
        if (cachedNode != null && epidemic.isAlive(cachedNode.getNodeID())) {
            // System.out.println("cache hit: " + cachedNode.getHost() + ":" + cachedNode.getPort());
            return cachedNode;
        }

        // Iterate through all nodes in the ring to find the one responsible for the hash value
        for (Node node : nodes) {
            if (node.inRange(hash)) {
                if (epidemic.isAlive(node.getNodeID())) {
                    // Found the correct node, add to cache and return
                    nodeCache.put(hash, node);
                    // System.out.println("Found the correct node: " + node.getHost() + ":" + node.getPort());
                    return node;
                } else {
                    // Node is not alive, update the hash ring and try again
                    updateHashRingUponDeadNode(node);
                    System.out.println("DEAD NODE#########################################################");
                    return getNodeForKey(key_byte_array);
                }
            }
        }

        // If no node was found for the hash value, wrap around to the first node
        Node firstNode = nodes.get(0);
        nodeCache.put(hash, firstNode);
        System.out.println("No node found for hash, using first node: " + firstNode.getHost() + ":" + firstNode.getPort());

        if(!epidemic.isAlive(firstNode.getNodeID())) {
            System.out.println("SENDING MESSAGE TO BAD NODE: " + firstNode.getHost() + ":" + firstNode.getPort());
        }
        return firstNode;
    }

    public void updateHashRingUponDeadNode(Node deadNode) {
        System.out.println("updating hash ring upon dead node");
        // Find the index of the dead node in the list of nodes
        int deadNodeIndex = nodes.indexOf(deadNode);

        // Remove the dead node from the list of nodes
        nodes.remove(deadNode);

        // 1s - 1e - 2s - 2e - 3s - 3e - 4s - 4e
        // Reassign the range
        if(deadNodeIndex >= 1) {
            Node leftOfDeadNode = nodes.get(deadNodeIndex - 1);
            leftOfDeadNode.setEndRange(nodes.get(deadNodeIndex).getEndRange());
        } else if (deadNodeIndex == 0) {
            Node leftOfDeadNode = nodes.get(nodes.size() - 1);
            leftOfDeadNode.setEndRange(nodes.get(deadNodeIndex).getEndRange());
        }

        // Remove the dead node
        nodes.remove(deadNode);

        // Clear the node cache
        nodeCache.clear();
    }

    public void clearNodeCache() {
        nodeCache.clear();
    }
}
