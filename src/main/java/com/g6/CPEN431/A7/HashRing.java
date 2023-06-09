package com.g6.CPEN431.A7;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class HashRing {
    private final CopyOnWriteArrayList<Node> nodes;
    private LinkedHashMap<Integer, Node> nodeCache;
    private Epidemic epidemic;
    private int myID;
    private int initialNumNodes;
    private StorageLayer kvStorage;
    private Cache kvCache;

    public HashRing(String configFilePath, String myAddress, int myPort, StorageLayer kvStorage, Cache kvCache) {
        this.kvStorage = kvStorage;
        this.kvCache = kvCache;
        this.nodes = new CopyOnWriteArrayList<>();
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

        System.out.println("Number of nodes in the HashRing is: " + hashRingSize);

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
        this.epidemic = new Epidemic(new ArrayList<>(this.nodes), myID, 200, 20000 + myID, 20);
        this.myID = myID;
        this.initialNumNodes = hashRingSize;

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

    public Node getPrimaryNodeForKey(byte[] key_byte_array) {
        int hash = HashUtils.hash(key_byte_array);
        Node cachedNode = nodeCache.get(hash);
        if (cachedNode != null && epidemic.isAlive(cachedNode.getNodeID())) {
            return cachedNode;
        }

        Node responsibleNode = null;
        for (Node node : nodes) {
            if (node.inRange(hash)) {
                responsibleNode = node;
                break;
            }
        }

        // Check if the responsible node is alive
        if (responsibleNode != null && epidemic.isAlive(responsibleNode.getNodeID())) {
            nodeCache.put(hash, responsibleNode);
            return responsibleNode;
        } else { // Handle the case when the responsible node is not alive or not found
            updateHashRingUponLatestEpidemicState();
            System.out.println("ERROR: No node found for hash value: " + hash + "Retrying...");
            return getPrimaryNodeForKey(key_byte_array);
        }
    }
  
    public Node getNextReplica() {
      int currentNodeID = this.myID;
      int hashRingSize = this.initialNumNodes;

      for(int i = currentNodeID - 1; i >= currentNodeID - hashRingSize; i--) {
          int predecessorID = (i + hashRingSize) % hashRingSize;
          if (epidemic.isAlive(predecessorID)) {
              return nodes.get(predecessorID);
          } 
      }

      System.out.println("ERROR: No replica found for this key. Try again until find one ...");
      return getNextReplica();
    }

    public int getAlivePredecessor(int currentNodeID) {
        int hashRingSize = this.initialNumNodes;

        for(int i = currentNodeID - 1; i >= currentNodeID - hashRingSize; i--) {
            int predecessorID = (i + hashRingSize) % hashRingSize;
            if (epidemic.isAlive(predecessorID)) {
                return predecessorID;
            }
        }

        System.out.println("ERROR: No replica found for this key, retrying...");
        return getAlivePredecessor(currentNodeID);
    }

    public int getAliveSuccessor(int currentNodeID) {
        int hashRingSize = this.initialNumNodes;

        for(int i = currentNodeID + 1; i <= currentNodeID + hashRingSize; i++) {
            int successorID = (i + hashRingSize) % hashRingSize;
            if (epidemic.isAlive(successorID)) {
                return successorID;
            }
        }

        System.out.println("ERROR: No replica found for this key");
        return -1;
    }

    private long lastDataClearedAt = 0;
    private static final long DATA_CLEAR_INTERVAL = TimeUnit.SECONDS.toMillis(15);
    public ArrayList<TransferRequest> updateHashRingUponLatestEpidemicState() {
        ArrayList<TransferRequest> transferRequests = new ArrayList<>();
        HashSet<Node> aliveBeforeUpdate = new HashSet<>();
        Set<Integer> myRangesBeforeUpdate = new HashSet<>(nodes.get(myID).getRangeSet());
        HashSet<Node> rejoiningNodes = new HashSet<>();
        
        // Step 1: Clear all the range lists of the nodes
        int howManyDeadNodes = 0;
        for (Node node : nodes) {
            if(!epidemic.isAlive(node.getNodeID())){
                howManyDeadNodes++;
            }
            if(node.getRangeSet().size() > 0){
                aliveBeforeUpdate.add(node);
            }
            node.clearRangeSet();
        }

        long currentTime = System.currentTimeMillis();
        // If there are a lot of dead nodes, this proves that THIS NODE was dead and just came back alive
        // We need to clear the outdated data
        if (howManyDeadNodes > this.initialNumNodes - 3 && currentTime - lastDataClearedAt >= DATA_CLEAR_INTERVAL) {
            System.out.println("I just came back alive.");
            
            // Clear the outdated data
            System.out.println("Clearing outdated data...");
            this.nodeCache.clear();
            this.kvStorage.clear();
            this.kvCache.clear();

            // Update the last data cleared timestamp
            lastDataClearedAt = currentTime;
        }
        
        // Step 2: Reinitialize the range lists based on the epidemic
        int lastAliveIndex = myID;
        int counter = 0;
        int index2 = myID;
        while (true) {
            if(epidemic.isAlive(index2)){
                nodes.get(index2).addRange(index2);
                lastAliveIndex = index2;
                
                // Handle the case when a node is rejoining
                if(!aliveBeforeUpdate.contains(nodes.get(index2))){
                    rejoiningNodes.add(nodes.get(index2));
                }
            } else {
                nodes.get(lastAliveIndex).addRange(index2);
            }

            if (index2 < nodes.size() - 1) {
                index2 = index2 + 1;
            } else if (index2 == nodes.size() - 1) {
                index2 = 0;
            }

            counter = counter + 1;
            if (counter >= nodes.size()) {
                break;
            }
        }

        // Step 3: Create transfer requests for the rejoining nodes
        for (Node node : rejoiningNodes) {
            for (int range : node.getRangeSet()) {
                if (myRangesBeforeUpdate.contains(range) && myID != node.getNodeID()) {
                    transferRequests.add(new TransferRequest(node, range));
                }
            }
        }
  
        return transferRequests;
    }

    public ArrayList<TransferRequest> updateReplicaUponLatestEpidemicState() {
        ArrayList<TransferRequest> transferRequestsForDrops = new ArrayList<>();
        HashSet<Integer> deadBeforeUpdate = new HashSet<>();
        HashSet<Integer> deadAfterUpdate = new HashSet<>();
        HashSet<Integer> deadSinceLastUpdateIndices = new HashSet<>();

        //Step 1 Find out dead nodes before update
        for(Node node : nodes) {
            if(!epidemic.isAlive(node.getNodeID())) {
                deadBeforeUpdate.add(node.getNodeID());
            }
        }
      
        ArrayList<TransferRequest> transferRequestsForRejoins = updateHashRingUponLatestEpidemicState();

        // Step 2: Find out dead nodes after update
        for(Node node : nodes) {
            if(!epidemic.isAlive(node.getNodeID())) {
                deadAfterUpdate.add(node.getNodeID());
            }
        }

        // Step 3: Find out the nodes that are dead after update but alive before update
        for(Integer deadAfterUpdateNodeID : deadAfterUpdate) {
            if(!deadBeforeUpdate.contains(deadAfterUpdateNodeID)) {
                deadSinceLastUpdateIndices.add(deadAfterUpdateNodeID);
            }
        }
      
        //Step 4: Find out of current node is affected by node leaving/rejoining
        int firstPredecessorID = getAlivePredecessor(myID);
        int secondPredecessorID = getAlivePredecessor(firstPredecessorID);
        int thirdPredecessorID = getAlivePredecessor(secondPredecessorID);

        for(int deadNodeIndex : deadSinceLastUpdateIndices) {
            Boolean shouldForwardReplica = (myID > thirdPredecessorID && deadNodeIndex < myID && deadNodeIndex > thirdPredecessorID) ||
                    (myID < thirdPredecessorID && (deadNodeIndex < myID || deadNodeIndex > thirdPredecessorID));
            if(shouldForwardReplica) {
                for(int i : nodes.get(myID).getRangeSet()) {
                    TransferRequest transferRequest = new TransferRequest(nodes.get(firstPredecessorID), i);
                    TransferRequest transferRequest2 = new TransferRequest(nodes.get(secondPredecessorID), i);
                    TransferRequest transferRequest3 = new TransferRequest(nodes.get(thirdPredecessorID), i);
                    transferRequestsForDrops.add(transferRequest);
                    transferRequestsForDrops.add(transferRequest2);
                    transferRequestsForDrops.add(transferRequest3);
                }
            }

            Boolean shouldReplicateDeadNodeRange = getAlivePredecessor(deadNodeIndex) == myID;
            if(shouldReplicateDeadNodeRange) {
                for(int i : nodes.get(deadNodeIndex).getRangeSet()) {
                    TransferRequest transferRequest = new TransferRequest(nodes.get(firstPredecessorID), i);
                    TransferRequest transferRequest2 = new TransferRequest(nodes.get(secondPredecessorID), i);
                    TransferRequest transferRequest3 = new TransferRequest(nodes.get(thirdPredecessorID), i);
                    transferRequestsForDrops.add(transferRequest);
                    transferRequestsForDrops.add(transferRequest2);
                    transferRequestsForDrops.add(transferRequest3);
                }
            }
        }

        ArrayList<TransferRequest> transferRequests = new ArrayList<>(transferRequestsForRejoins);
        transferRequests.addAll(transferRequestsForDrops);

        return transferRequests;
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
                updateHashRingUponLatestEpidemicState();
            }
        }
        return count;
    }
}
