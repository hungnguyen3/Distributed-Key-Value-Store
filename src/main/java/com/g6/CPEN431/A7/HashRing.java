package com.g6.CPEN431.A7;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashRing {
    private final List<Node> nodes;

    // Constructor to create a new HashRing given a configuration file path
    public HashRing(String configFilePath) {
        nodes = new ArrayList<>();

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
        return nodes;
    }

    // Method to get the node responsible for a given key
    public Node getNodeForKey(String key) {
        // Calculate the hash value of the key using the Murmur3 hash function
        int hash = HashUtils.hash(key.getBytes());

        // Find the first node in the ring whose range contains the hash value
        for (Node node : nodes) {
            if (node.inRange(hash)) {
                return node;
            }
        }

        // If the hash value is greater than the end range of the last node, wrap around to the first node
        return nodes.get(0);
    }
}
