package com.g6.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse.KVResponse;
import com.google.protobuf.ByteString;

import java.lang.management.ManagementFactory;

public class RequestHandlingLayer {
    private StorageLayer storageLayer;
    private Cache cache;
    private HashRing hashRing;
    private long MAX_MEMORY_USAGE = 505;
    private final int MAX_KEY_LENGTH = 32;
    private final int MAX_VALUE_LENGTH = 10000; //(1 << 20);
    private final int NODE_PID = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    private final KVResponse ERROR_KEY_TOO_LONG = KVResponse.newBuilder().setErrCode(0x06).build();
    private final KVResponse ERROR_VALUE_TOO_LONG = KVResponse.newBuilder().setErrCode(0x07).build();
    private final KVResponse ERROR_OUT_OF_MEMORY = KVResponse.newBuilder().setErrCode(0x02).build();
    private final KVResponse ERROR_INVALID_COMMAND = KVResponse.newBuilder().setErrCode(0x05).build();
    private final KVResponse ERROR_NON_EXISTENT = KVResponse.newBuilder().setErrCode(0x01).build();
    private final KVResponse ZERO_ERR_CODE = KVResponse.newBuilder().setErrCode(0x00).build();

    public RequestHandlingLayer(StorageLayer storageLayer, Cache cache, HashRing hashRing, int maxMemUsage) {
        System.out.println("max mem usage: " + (maxMemUsage - 8));
        this.storageLayer = storageLayer;
        this.cache = cache;
        this.hashRing = hashRing;
        this.MAX_MEMORY_USAGE = maxMemUsage - 8;
    }

    public KVResponse processPutRequest(KVRequest request, ByteString reqMsgId, long firstReceivedAtPrimaryTimestamp) {
        KVResponse cachedRes = cache.get(reqMsgId);
        if (cachedRes != null) {
            return cachedRes;
        }

        if (request.getKey().size() > MAX_KEY_LENGTH) {
            return ERROR_KEY_TOO_LONG;
        }
        if (request.getValue().size() > MAX_VALUE_LENGTH) {
            return ERROR_VALUE_TOO_LONG;
        }

        KVResponse response;
        switch (request.getCommand()) {
            case 0x01:
                if(firstReceivedAtPrimaryTimestamp == 0) {
                    System.out.println("firstReceivedAtPrimaryTimestamp is 0");
                }
                if (getMemoryUsage() >= MAX_MEMORY_USAGE) {
                    response = ERROR_OUT_OF_MEMORY;
                } else {
                    StorageLayer.ValueVersionPair currentValueVersionPair = storageLayer.get(request.getKey());
                    if (currentValueVersionPair == null) {
                        storageLayer.put(request.getKey(), request.getValue(), request.getVersion(), firstReceivedAtPrimaryTimestamp);
                        response = ZERO_ERR_CODE;
                    } else {
                        if (currentValueVersionPair.firstReceivedAtPrimaryTimestamp > firstReceivedAtPrimaryTimestamp) {
                            System.out.println("Proccess Put: Received a request with a lower timestamp than the one we already have");
                            response = ZERO_ERR_CODE;
                        } else {
                            storageLayer.put(request.getKey(), request.getValue(), request.getVersion(), firstReceivedAtPrimaryTimestamp);
                            response = ZERO_ERR_CODE;
                        }
                    }
                }
                break;
            default:
                System.out.println("Invalid command, this processRequest should not be called with anything other than a put request");
                response = ERROR_INVALID_COMMAND;
        }
        cache.put(reqMsgId, response);
        return response;
    }

    public KVResponse processRequestOtherThanPut(KVRequest request, ByteString reqMsgId) {
        KVResponse cachedRes = cache.get(reqMsgId);
        if (cachedRes != null) {
            return cachedRes;
        }

        if (request.getKey().size() > MAX_KEY_LENGTH) {
            return ERROR_KEY_TOO_LONG;
        }
        if (request.getValue().size() > MAX_VALUE_LENGTH) {
            return ERROR_VALUE_TOO_LONG;
        }

        KVResponse response;
        switch (request.getCommand()) {
            case 0x02:
                StorageLayer.ValueVersionPair valueVersionPair = storageLayer.get(request.getKey());
                if (valueVersionPair == null) {
                    response = ERROR_NON_EXISTENT;
                } else {
                    response = KVResponse.newBuilder().setErrCode(0x00).setValue(valueVersionPair.value).setVersion(valueVersionPair.version).build();
                }
                break;
            case 0x03:
                StorageLayer.ValueVersionPair remove = storageLayer.remove(request.getKey());
                response = remove == null ? ERROR_NON_EXISTENT : ZERO_ERR_CODE;
                break;
            case 0x04:
                System.exit(0);
                response = ZERO_ERR_CODE;
                break;
            case 0x05:
                storageLayer.clear();
                cache.clear();
                hashRing.clearNodeCache();
                response = ZERO_ERR_CODE;
                break;
            case 0x06:
                response = ZERO_ERR_CODE;
                break;
            case 0x07:
                response = KVResponse.newBuilder().setErrCode(0x00).setPid(NODE_PID).build();
                break;
            case 0x08:
                response = KVResponse.newBuilder().setErrCode(0x00).setMembershipCount(this.hashRing.getMembershipCount()).build();
                break;
            default:
                response = ERROR_INVALID_COMMAND;
        }
        cache.put(reqMsgId, response);
        return response;
    }

    public void performTransfer(TransferRequest transferRequest){
        storageLayer.performTransfer(transferRequest);
    }

    private static long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        return usedMemory;
    }
}
