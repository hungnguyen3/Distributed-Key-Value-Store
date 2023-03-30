package com.g6.CPEN431.A7;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HashRing {
    // Assumes there is at least 1 node after initialization.
    // DO NOT ADD OR REMOVE FROM nodes
    private final ArrayList<Node> nodes;

    // DO NOT USE THIS TO DETERMINE IF A NODE IS ALIVE
    private Set<Node> deadNodes;
    private int previousDeadNodesSize = 0;

    private LinkedHashMap<Integer, Node> nodeCache;

    private ReplicationService replicationService;

    public ReplicationService getReplicationService() {
        return replicationService;
    }

    private Epidemic epidemic;
    private int myID;
    private int initialNumNodes;

    /**
     * Constructor to create a new HashRing.
     * @param configFilePath path to servers.txt that contains all the nodes addresses and ports
     * @param myAddress address of this node (mainly for debugging purposes)
     * @param myPort port of this node (mainly for debugging purposes)
     */
    public HashRing(String configFilePath, String myAddress, int myPort) {
        this.nodes = new ArrayList<>();
        this.deadNodes = new HashSet<>();

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
        this.epidemic = new Epidemic((ArrayList<Node>)nodes.clone(), myID, 200, 20000 + myID, 20);
        this.myID = myID;
        this.initialNumNodes = hashRingSize;

        this.replicationService = new ReplicationService(hashRingSize, this.nodes, this.myID, this.epidemic);

        try {
            this.epidemic.startEpidemic();
        } catch (Exception e){
            System.out.println("Could not start epidemic protocol on this node");
        }
    }

    // When performing get or remove, we need to search the primary node first, then all the replication nodes
    public Node getRedirectNode(byte[] key_byte_array, int startingPoint, boolean strictMode) {
        if (strictMode) {
            return getPrimaryNodeForKey(key_byte_array);
        } else {
            return this.getNextReplica();
        }
    }

    /**
     * Method to get the node responsible for a given key
     */
    public Node getPrimaryNodeForKey(byte[] key_byte_array) {
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
                    return getPrimaryNodeForKey(key_byte_array);
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
     * Method to get the next replica for this key
     * TODO: review this method
     */
    public Node getNextReplica() {
      int currentNodeID = this.myID;
      int hashRingSize = this.initialNumNodes;

      for(int i = currentNodeID - 1; i >= currentNodeID - hashRingSize; i--) {
          int predecessorID = (i + hashRingSize) % hashRingSize;
          if (epidemic.isAlive(predecessorID)) {
              return nodes.get(predecessorID);
          } else {
              updateHashRingUponDeadNode(nodes.get(predecessorID));
          }
      }
      return null;
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
        // System.out.println("Node with ID " + deadNode.getNodeID() + " just dropped");

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
            if(!takeOverNode.getRangeSet().isEmpty()) { // Only transfer the ranges to node that is alive
                takeOverNode.addRanges(deadNode.getRangeSet());

                // Clear the rangeList of the dead node => node not responsible for any range
                deadNode.clearRangeSet();
                break;
            }

            if (counter >= nodes.size()) {
                // System.out.println("Walked the entire ring and ended up not finding any alive nodes!!");
            }
        }
        if (previousDeadNodesSize != deadNodes.size()) {
            System.out.println("After node drop:");
            previousDeadNodesSize = deadNodes.size();
            printAllNodeRangeSets();
        }
        // Clear the node cache
        nodeCache.clear();
    }

    /**
     * Check all the nodes that are previously dead for rejoins
     * Upon rejoins, perform corresponding redistribution for range lists
     * After redistribution, remove the newly rejoined nodes from 'deadNodes'
     * Returns a list of transfer requests that must be performed by this node
     */
    public ArrayList<TransferRequest> checkAndHandleRejoins() {
        List<Node> rejoinedNodes = new ArrayList<>();
        ArrayList<TransferRequest> transferRequests = new ArrayList<>();

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
                    if (!nodes.get(index).getRangeSet().isEmpty()) {
                        nodeTookOverRanges = node;
                        break;
                    }

                    if (counter >= nodes.size()) {
                        System.out.println("Walked the entire ring and ended up not finding the node that took over the deadNode's rangeList!!");
                        break;
                    }
                }


                if (nodeTookOverRanges != null) {
                    // Transfer any ranges that the rejoined node is responsible for from nodeToTakeOverRanges to the rejoined node
                    ArrayList<Integer> rangesToRemove = new ArrayList<>();
                    for (int range : nodeTookOverRanges.getRangeSet()) {
                        if (shouldTransfer(deadNode.getNodeID(), getNextLivingSuccessorID(deadNode), range)) {
                            // System.out.println("Transferring Range " + range + " to node with id " + deadNode.getNodeID());
                            deadNode.addRange(range);
                            rangesToRemove.add(range);

                            if(myID == nodeTookOverRanges.getNodeID()){
                                transferRequests.add(new TransferRequest(deadNode, range));
                            }
                        }
                    }
                    for(int range : rangesToRemove){
                        nodeTookOverRanges.removeRange(range);
                    }

                    rejoinedNodes.add(deadNode);
                    // System.out.println("Node with ID " + deadNode.getNodeID() + " just rejoined");
                }
            }
        }

        // Remove the newly rejoined nodes from the deadNodes list
        deadNodes.removeAll(rejoinedNodes);
       
        if(rejoinedNodes.size() > 0) {
            System.out.println("After Rejoins:");
            printAllNodeRangeSets();
        }
        return transferRequests;
    }

    public void printAllNodeRangeSets() {
        for (Node node : nodes) {
            System.out.println("Node ID: " + node.getNodeID() + ", Range Set: " + node.getRangeSet());
        }
    }

    //gets the next living successor by checking the next nodeID until
    //if no living successor is found returns INTEGER_MAX_VALUE
    private int getNextLivingSuccessorID(Node node){
        int currentNodeID = node.getNodeID();
        for(int i = currentNodeID + 1; i <= currentNodeID + initialNumNodes - 1; i++) {
            int successorID = i % initialNumNodes;
            if (epidemic.isAlive(successorID)) {
                return successorID;
            }
        }
        return node.getNodeID();
    }


    public void clearNodeCache() {
        nodeCache.clear();
    }

    public int getMembershipCount() {
        if(nodes.size() == 1) {
            return 1;
        }

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

    private boolean shouldTransfer(int deadNodeID, int successorNodeID, int range) {
        if(deadNodeID < successorNodeID){
            return (range >= deadNodeID);
        } else if (deadNodeID > successorNodeID){
            return (range >= deadNodeID || range < successorNodeID);
        }
        return true;
    }
}
