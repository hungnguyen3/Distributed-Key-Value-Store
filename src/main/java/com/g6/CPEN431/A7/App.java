package com.g6.CPEN431.A7;

public class App {
    public static void main(String[] args) {
        // Check if the correct number of command line arguments was provided
        System.out.println("startup version 10.0");
        if (args.length < 2) {
            System.err.println("Usage: java -jar A7.jar <address> <port> <cacheSize>");
            System.exit(1);
        }

        // Parse the command line arguments
        int maxMemUsage = Integer.parseInt(args[0]);
        String address = args[1];
        int port = Integer.parseInt(args[2]);
        System.out.println("Opening on port: " + port);
        int cacheSize = Integer.parseInt(args[3]);

        // Create a new storage layer, cache, isAlive map, and hash ring
        StorageLayer storage = new StorageLayer(port + 20000);
        Cache cache = new Cache(cacheSize);

        HashRing hashRing = new HashRing("servers.txt", address, port);

        // Create a new request handling layer and network layer using the above objects
        RequestHandlingLayer requestHandler = new RequestHandlingLayer(storage, cache, hashRing, maxMemUsage);
        NetworkLayer network = new NetworkLayer(address, port, requestHandler, hashRing);

        // Start the network layer
        network.run();
    }
}
