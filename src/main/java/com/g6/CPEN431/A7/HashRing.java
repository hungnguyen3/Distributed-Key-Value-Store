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
    private ArrayList<Node> nodes;
    //HashRing only mutates isAlive map on initialization, after that Read-Only.
    private Epidemic epidemic;

    // Constructor to create a new HashRing given a configuration file path.
    public HashRing(String configFilePath, String myAddress, int myPort) {
        nodes = new ArrayList<>();
        int myID = 0;
        // Try to read the configuration file and create a Node object for each line
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");

                // Parse the hostname and port number from the line
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                if(host.equals(myAddress) && port == myPort){
                    myID = i;
                }

                // Divide the range of hash values evenly between the nodes in the ring
                int startRange = i * (Integer.MAX_VALUE / 25);
                int endRange = (i + 1) * (Integer.MAX_VALUE / 25) - 1;

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

        epidemic = new Epidemic((ArrayList<Node>)nodes.clone(), myID, 200, 20000 + myID, 5);

        try {
            epidemic.startEpidemic();
        } catch (Exception e){
            System.out.println("Could not start epidemic protocol on this node");
        }
    }

    // Method to get the list of nodes in the ring
    public List<Node> getNodes() {
        for(int i = 0; i < nodes.size(); i++) {
            if (!epidemic.isAlive(nodes.get(i).getNodeID())) {
                nodes.remove(i);
                i -= 1;
            }
        }
        return nodes;
    }

    // Method to get the node responsible for a given key
    public Node getNodeForKey(byte[] key_byte_array) {
        // Calculate the hash value of the key using the Murmur3 hash function
        int hash = HashUtils.hash(key_byte_array);

        for (int i = 0; i < nodes.size(); i++) {
            //Node i contains the hash value, now find the next node that is alive (including node i) to handle the request.
            if(nodes.get(i).inRange(hash)) {
                for(int j = i; j < nodes.size(); j++) {
                    Node nextAvailableNode = nodes.get(j);
                    if(epidemic.isAlive(nextAvailableNode.getNodeID())) {
                        return nextAvailableNode;
                    } else {
                        System.out.println("Entered node dead");
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
