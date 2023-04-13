package com.g6.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequestWithTimestamp.KVRequestWithTimestamp;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageLayer {
    DatagramSocket transferSocket;
    public StorageLayer(int port){
        try {
            this.transferSocket = new DatagramSocket(port);
            Thread receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //continually receive messages
                    byte[] receiveBuffer = new byte[16*1024];
                    while(true){
                        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        try {
                            transferSocket.receive(packet);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        byte[] incomingPacketData = Arrays.copyOf(packet.getData(), packet.getLength());
                        try {
                            KVRequestWithTimestamp incomingRequest = KVRequestWithTimestamp.parseFrom(incomingPacketData);

                            StorageLayer.ValueVersionPair currentValueVersionPair = store.get(incomingRequest.getKey());
                            if (currentValueVersionPair == null) {
                                store.put(incomingRequest.getKey(), new ValueVersionPair(incomingRequest.getValue(), incomingRequest.getVersion(), incomingRequest.getFirstReceivedAtPrimaryTimestamp()));
                            } else {
                                if (currentValueVersionPair.firstReceivedAtPrimaryTimestamp > incomingRequest.getFirstReceivedAtPrimaryTimestamp()) {
                                    // Send a transfer request to the node with outdated data to update with the newer data
                                    KVRequestWithTimestamp updateDataReq = KVRequestWithTimestamp.newBuilder()
                                            .setKey(incomingRequest.getKey())
                                            .setValue(currentValueVersionPair.value)
                                            .setVersion(currentValueVersionPair.version)
                                            .setFirstReceivedAtPrimaryTimestamp(currentValueVersionPair.firstReceivedAtPrimaryTimestamp)
                                            .build();
                                    try {
                                        DatagramPacket updateDataPacket = new DatagramPacket(updateDataReq.toByteArray(), updateDataReq.toByteArray().length, packet.getAddress(), packet.getPort());
                                        transferSocket.send(updateDataPacket);
                                    } catch (Exception e) {
                                        System.out.println("ERROR ON SENDING INTERNAL KEY TRANSFER: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                } else {
                                    store.put(incomingRequest.getKey(), new ValueVersionPair(incomingRequest.getValue(), incomingRequest.getVersion(), incomingRequest.getFirstReceivedAtPrimaryTimestamp()));
                                }
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            receiveThread.start();
            System.out.println("CREATED TRANSFER SOCKET ON PORT " + port);
        }
        catch (Exception e){
            this.transferSocket = null;
            System.out.println("FAILED TO CREATED TRANSFER SOCKET");
        }
    }
    public class ValueVersionPair {
        public ByteString value;
        public Integer version;
        public long firstReceivedAtPrimaryTimestamp; // Timestamp at which the primary received the request

        public ValueVersionPair(ByteString value, Integer version, long firstReceivedAtPrimaryTimestamp) {
            this.value = value;
            this.version = version;
            this.firstReceivedAtPrimaryTimestamp = firstReceivedAtPrimaryTimestamp;
        }
    }

    private ConcurrentHashMap<ByteString, ValueVersionPair> store = new ConcurrentHashMap<>();

    public ValueVersionPair get(ByteString key) {
        if (!store.containsKey(key)) {
            return null;
        }

        return store.get(key);
    }

    public void put(ByteString key, ByteString value, Integer version, long firstReceivedAtPrimaryTimestamp) {
        store.put(key, new ValueVersionPair(value, version, firstReceivedAtPrimaryTimestamp ));
    }

    public ValueVersionPair remove(ByteString key) {
        return store.remove(key);
    }

    public void clear() {
        store.clear();
    }

    public void performTransfer(TransferRequest transferRequest) {
        Node destinationNode = transferRequest.getDestinationNode();
        int range = transferRequest.getRange();
        Set<ByteString> keySet = new HashSet<>(store.keySet());
        Set<ByteString> toRemove = new HashSet<>();
        for(ByteString b : keySet){
            byte[] key_byte_array = b.toByteArray();
            int hash = HashUtils.hash(key_byte_array);
            if(hash % destinationNode.getHashRingSize() == range){
                ValueVersionPair pair = store.get(b);
                KVRequestWithTimestamp outgoingReq = KVRequestWithTimestamp.newBuilder()
                        .setKey(b)
                        .setValue(pair.value)
                        .setVersion(pair.version)
                        .setFirstReceivedAtPrimaryTimestamp(pair.firstReceivedAtPrimaryTimestamp)
                        .build();
                try {
                    DatagramPacket outgoingPacket = new DatagramPacket(outgoingReq.toByteArray(), outgoingReq.toByteArray().length, InetAddress.getByName(destinationNode.getHost()), destinationNode.getPort() + 20000);
                    this.transferSocket.send(outgoingPacket);
                    toRemove.add(b);
                } catch (Exception e){
                    System.out.println("ERROR ON SENDING INTERNAL KEY TRANSFER");
                }

            }
        }
        // for(ByteString b : toRemove){
        //     store.remove(b);
        // }
    }
}
