package com.g6.CPEN431.A7;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HashRing {
    //Assumes there is at least 1 node after initialization.
    private List<Node> nodes;
    //HashRing only mutates isAlive map on initialization, after that Read-Only.
    private ConcurrentHashMap<String, Boolean> isAlive;

    // Constructor to create a new HashRing given a configuration file path.
    public HashRing(String configFilePath, ConcurrentHashMap<String, Boolean> isNodeAlive) {
        nodes = new ArrayList<>();
        isAlive = isNodeAlive;

        // Try to read the configuration file and create a Node object for each line
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");

                // Parse the hostname and port number from the line
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);

                // Divide the range of hash values evenly between the nodes in the ring
                int startRange = i * (Integer.MAX_VALUE / 25);
                int endRange = (i + 1) * (Integer.MAX_VALUE / 25) - 1;

                // Add a new node with the host, port, and range to the list of nodes in the ring
                nodes.add(new Node(host, port, startRange, endRange));
                //Initialize all nodes as alive at the beginning
                isAlive.put(host + port, true);
                i++;
            }
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.err.println("Error: file not found: " + configFilePath);
        } catch (IOException e) {
            // If there is an error reading the file, throw a RuntimeException with the file path and cause
            throw new RuntimeException("Error reading servers.txt file: " + configFilePath, e);
        }
    }

    // Method to get the list of nodes in the ring
    public List<Node> getNodes() {
        for(int i = 0; i < nodes.size(); i++) {
            if (!isAlive.get(nodes.get(i).getHost() + nodes.get(i).getPort())) {
                nodes.remove(i);
                i -= 1;
            }
        }
        return nodes;
    }

    // Method to get the node responsible for a given key
    public Node getNodeForKey(String key) {
        // Calculate the hash value of the key using the Murmur3 hash function
        int hash = HashUtils.hash(key.getBytes());

        for (int i = 0; i < nodes.size(); i++) {
            //Node i contains the hash value, now find the next node that is alive (including node i) to handle the request.
            if(nodes.get(i).inRange(hash)) {
                for(int j = i; j < nodes.size(); j++) {
                    Node nextAvailableNode = nodes.get(j);
                    String address = nextAvailableNode.getHost() + nextAvailableNode.getPort();
                    if(isAlive.get(address)) {
                        return nextAvailableNode;
                    } else {
                        //If node isn't alive, update the hash-range of its successor to include its range then delete the node from list.
                        nodes.get((j + 1) % nodes.size()).setStartRange(nextAvailableNode.getStartRange());
                        nodes.remove(j);
                        j -= 1;
                    }
                }

                break;
            }
        }

        return nodes.get(0);
    }
}
