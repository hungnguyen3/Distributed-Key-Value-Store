package com.g6.CPEN431.A7;

public class App {
    public static void main(String[] args) {
        // Check if the correct number of command line arguments was provided
        if (args.length < 2) {
            System.err.println("Usage: java -jar A7.jar <port> <cacheSize>");
            System.exit(1);
        }

        // Parse the command line arguments
        int port = Integer.parseInt(args[0]);
        int cacheSize = Integer.parseInt(args[1]);

        // Create a new storage layer, cache, and hash ring
        StorageLayer storage = new StorageLayer();
        Cache cache = new Cache(cacheSize);
        HashRing hashRing = new HashRing("servers.txt");

        // Create a new request handling layer and network layer using the above objects
        RequestHandlingLayer requestHandler = new RequestHandlingLayer(storage, cache);
        NetworkLayer network = new NetworkLayer(port, requestHandler, hashRing);

        // Start the network layer
        network.run();
    }
}