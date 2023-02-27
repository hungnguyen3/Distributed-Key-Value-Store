package com.g6.CPEN431.A7;

import java.util.concurrent.ConcurrentHashMap;

public class App {
    public static void main(String[] args) {
        // Check if the correct number of command line arguments was provided
        System.out.println("startup");
        if (args.length < 2) {
            System.err.println("Usage: java -jar A7.jar <port> <cacheSize>");
            System.exit(1);
        }

        // Parse the command line arguments
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        int cacheSize = Integer.parseInt(args[2]);

        // Create a new storage layer, cache, isAlive map, and hash ring
        StorageLayer storage = new StorageLayer();
        Cache cache = new Cache(cacheSize);
        ConcurrentHashMap<String, Boolean> isNodeAlive = new ConcurrentHashMap<>();
        HashRing hashRing = new HashRing("servers.txt", address, port);

        // Create a new request handling layer and network layer using the above objects
        RequestHandlingLayer requestHandler = new RequestHandlingLayer(storage, cache);
        NetworkLayer network = new NetworkLayer(address, port, requestHandler, hashRing);

        // Start the network layer
        network.run();
    }
}