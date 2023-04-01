package com.g6.CPEN431.A7;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HashRingUpdater implements Runnable {
    private HashRing hashRing;
    private RequestHandlingLayer requestHandlingLayer;

    public HashRingUpdater(HashRing hashRing, RequestHandlingLayer requestHandlingLayer) {
        this.hashRing = hashRing;
        this.requestHandlingLayer = requestHandlingLayer;
    }

    @Override
    public void run() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        Runnable updateHashRing = new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<TransferRequest> replicaTransferRequests = hashRing.updateReplicaUponLatestEpidemicState();
                    for (TransferRequest replicaTransferRequest : replicaTransferRequests) {
                        requestHandlingLayer.performTransfer(replicaTransferRequest);
                    }
                } catch (ConcurrentModificationException e) {
                    System.out.println("ERROR: CHECKING FOR REJOINS" + e);
                } catch (Exception e) {
                    System.out.println("ERROR: General exception in updateHashRing" + e);
                }
            }
        };
        
        // Update the hash ring every 3 seconds
        executorService.scheduleAtFixedRate(updateHashRing, 0, 3, TimeUnit.SECONDS);
    }
}
