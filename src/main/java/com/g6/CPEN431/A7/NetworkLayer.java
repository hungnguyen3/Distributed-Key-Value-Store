package com.g6.CPEN431.A7;

public class NetworkLayer {
    private int port;
    private String address;
    private RequestHandlingLayer requestHandlingLayer;
    private HashRing hashRing;

    public NetworkLayer(String address, int port, RequestHandlingLayer requestHandlingLayer, HashRing hashRing) {
        this.address = address;
        this.port = port;
        this.requestHandlingLayer = requestHandlingLayer;
        this.hashRing = hashRing;
    }

    public void start() {
        NetworkListener networkListener = new NetworkListener(address, port, requestHandlingLayer, hashRing);
        HashRingUpdater hashRingUpdater = new HashRingUpdater(hashRing, requestHandlingLayer);

        Thread networkListenerThread = new Thread(networkListener);
        Thread hashRingUpdaterThread = new Thread(hashRingUpdater);

        networkListenerThread.start();
        hashRingUpdaterThread.start();
    }
}
