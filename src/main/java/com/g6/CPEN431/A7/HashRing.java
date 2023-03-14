package com.g6.CPEN431.A7;

import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HashRing {
    // Assumes there is at least 1 node after initialization.
    // DO NOT ADD OR REMOVE FROM nodes
    private final ArrayList<Node> nodes;

    // DO NOT USE THIS TO DETERMINE IF A NODE IS ALIVE
    private ArrayList<Node> deadNodes;

    private LinkedHashMap<Integer, Node> nodeCache;

    private Epidemic epidemic;
    private String myAddress;
    private int myPort;
    private int myID;

    /**
     * Constructor to create a new HashRing.
     * @param configFilePath path to servers.txt that contains all the nodes addresses and ports
     * @param myAddress address of this node (mainly for debugging purposes)
     * @param myPort port of this node (mainly for debugging purposes)
     */
    public HashRing(String configFilePath, String myAddress, int myPort) {
        this.myAddress = myAddress;
        this.myPort = myPort;
        this.nodes = new ArrayList<>();
        this.deadNodes = new ArrayList<>();

        this.nodeCache = new LinkedHashMap<Integer, Node>(200, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Node> eldest) {
                return size() > 200;
            }
        };

        // Get how many nodes are running
        int hashRingSize = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            while ((br.readLine()) != null) {
                hashRingSize++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: file not found: " + configFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Error reading servers.txt file: " + configFilePath, e);
        }

        System.out.println("Number of nodes in the HashRing: " + hashRingSize);

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

                int epidemicPort = 20000 + i;

                // Add a new node to the list of nodes in the ring
                nodes.add(new Node(host, port, i, epidemicPort, i, hashRingSize));
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
        this.epidemic = new Epidemic((ArrayList<Node>)nodes.clone(), myID, 500, 20000 + myID, 20);
        this.myID = myID;
        try {
            this.epidemic.startEpidemic();
        } catch (Exception e){
            System.out.println("Could not start epidemic protocol on this node");
        }
    }

    /**
     * Method to get the node responsible for a given key
     */
    public Node getNodeForKey(byte[] key_byte_array) {
        // Calculate the hash value of the key using the Murmur3 hash function
        // System.out.println("I am Node: " + myAddress + myPort + " ID:" + myID);
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
                    // System.out.println("DEAD NODE#########################################################");
                    return getNodeForKey(key_byte_array);
                }
            }
        }

        // If no node was found for the hash value, wrap around to the first node
        Node firstNode = nodes.get(0);
        nodeCache.put(hash, firstNode);
        // System.out.println("No node found for hash, using first node: " + firstNode.getHost() + ":" + firstNode.getPort());

        if(!epidemic.isAlive(firstNode.getNodeID())) {
            // System.out.println("SENDING MESSAGE TO BAD NODE: " + firstNode.getHost() + ":" + firstNode.getPort());
        }
        return firstNode;
    }

    /**
     * Upon encountering a deadNode (call it Node B), add it to deadNodes list
     * Walk to ring to find an alive node to the left of the deadNode (call it Node A)
     * Transfer the rangeList of node B to node A
     */
    public void updateHashRingUponDeadNode(Node deadNode) {
        // System.out.println("updating hash ring upon dead node");
        // Find the index of the dead node in the list of nodes
        int deadNodeIndex = nodes.indexOf(deadNode);

        // Add the deadNode to deadNodes array
        deadNodes.add(deadNode);
        System.out.println("Node with ID " + deadNode.getNodeID() + " just dropped");

        // Walk the ring to find a node that is not dead to the left of the dead node
        int counter = 0;
        int index = deadNodeIndex;
        while(true) {
            counter++;

            if (index >= 1) {
                index = index - 1; // move to the left by 1
            } else if (index == 0) {
                index = nodes.size() - 1; // move to the end of the list if reaches index 0
            }

            Node takeOverNode = nodes.get(index);
            // Reassign the rangeList
            if(!takeOverNode.getRangeList().isEmpty()) { // Only transfer the ranges to node that is alive
                takeOverNode.addRanges(deadNode.getRangeList());

                // Clear the rangeList of the dead node => node not responsible for any range
                deadNode.clearRangeList();
                break;
            }

            if (counter >= nodes.size()) {
                System.out.println("Walked the entire ring and ended up not finding any alive nodes!!");
            }
        }

        // Clear the node cache
        nodeCache.clear();
    }

    /**
     * Check all the nodes that are previously dead for rejoins
     * Upon rejoins, perform corresponding redistribution for range lists
     * After redistribution, remove the newly rejoined nodes from 'deadNodes'
     */
    public void checkAndHandleRejoins() {
        List<Node> rejoinedNodes = new ArrayList<>();

        for (Node deadNode : deadNodes) {
            if (epidemic.isAlive(deadNode.getNodeID())) { // Check if the dead node has rejoined
                int deadNodeIndex = nodes.indexOf(deadNode);
                Node nodeTookOverRanges = null;

                // Walk the ring to find a node that is not dead to the left of the dead node
                // The node that we find should have taken over the dead node's rangeList
                int counter = 0;
                int index = deadNodeIndex;
                while(true) {
                    counter++;

                    if (index >= 1) {
                        index = index - 1; // move to the left by 1
                    } else if (index == 0) {
                        index = nodes.size() - 1; // move to the end of the list if reaches index 0
                    }

                    Node node = nodes.get(index);
                    // Found the node that took over the deadNode's rangeList
                    if (!nodes.get(index).getRangeList().isEmpty()) {
                        nodeTookOverRanges = node;
                        break;
                    }

                    if (counter >= nodes.size()) {
                        System.out.println("Walked the entire ring and ended up not finding the node that took over the deadNode's rangeList!!");
                    }
                }

                if (nodeTookOverRanges != null) {
                    // Transfer any ranges that the rejoined node is responsible for from nodeToTakeOverRanges to the rejoined node
                    for (int range : nodeTookOverRanges.getRangeList()) {
                        if (deadNode.inRange(range)) {
                            deadNode.addRange(range);
                            nodeTookOverRanges.removeRange(range);
                        }
                    }

                    rejoinedNodes.add(deadNode);
                    System.out.println("Node with ID " + deadNode.getNodeID() + " just rejoined");
                }
            }
        }

        // Remove the newly rejoined nodes from the deadNodes list
        deadNodes.removeAll(rejoinedNodes);
    }

    public void clearNodeCache() {
        nodeCache.clear();
    }

    public int getMembershipCount() {
        int count = 0;
        for (Node node : nodes) {
            if (epidemic.isAlive(node.getNodeID())) {
                count++;
            } else {
                if (!deadNodes.contains(node)) {
                    updateHashRingUponDeadNode(node);
                }
            }
        }
        return count;
    }
}
