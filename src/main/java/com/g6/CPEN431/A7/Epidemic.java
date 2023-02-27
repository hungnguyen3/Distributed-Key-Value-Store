package com.g6.CPEN431.A7;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Epidemic {
    private List<Node> nodeList; //list of other nodes, assumed that node ID is the order in th list
    private ArrayList<Long> timestampVector; //list of timestamps where index corresponds to nodeID
    private int ID; //currently accessed nodeID
    private int myID; //ID of this node
    private int N; //total number of Nodes

    private int delayMs; //delay between rounds in milliseconds
    private int port; //port to use for epidemic protocol
    private int safetyRounds; //number of buffer rounds for checking if remote node isAlive
    private Random rng = new Random();
    public Epidemic(List<Node> nodeList, int myID, int delayMs, int port, int safetyRounds){
        this.nodeList = nodeList;
        this.myID = myID;
        this.N = nodeList.size();
        this.delayMs = delayMs;
        this.port = port;
        this.safetyRounds = safetyRounds;
        timestampVector = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for(int i = 0; i < N; i++){
            timestampVector.add(currentTime);
        }
    }
    public void startEpidemic() throws SocketException {
        final DatagramSocket datagramSocket = new DatagramSocket(port);
        final byte[] recieveBuffer = new byte[8 * N];
        final Lock timestampVectorWriteLock = new ReentrantLock();

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // continually update time and send to a random node
                while(true){
                    timestampVectorWriteLock.lock();
                    timestampVector.set(myID, System.currentTimeMillis());
                    timestampVectorWriteLock.unlock();
                    do {
                        ID = rng.nextInt(N);
                    } while(ID == myID);
                    //create a bytebuffer from the timestamp vector
                    byte[] timestampByteBuffer = new byte[]{};
                    for(int i = 0; i < N; i++){
                        timestampByteBuffer = Bytes.concat(timestampByteBuffer, Longs.toByteArray(timestampVector.get(i)));
                    }
                    //send the timestampvector to random node
                    Node sendNode = nodeList.get(ID);
                    InetAddress address = null;
                    try {
                        address = InetAddress.getByName(sendNode.getHost());
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                    DatagramPacket timestampPush = new DatagramPacket(timestampByteBuffer, timestampByteBuffer.length, address, sendNode.getEpidemicPort());

                    try {
                        datagramSocket.send(timestampPush);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Sleep thread for the delay
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Thread recieveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //continually recieve messages and update timestamp vector
                while(true){
                    DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
                    try {
                        datagramSocket.receive(packet);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    byte[] byteBuf = packet.getData();

                    //parse byte array into longs and compare timestamps to find which is most recent
                    for(int i = 0; i < N; i++){
                        byte[] currLong = Arrays.copyOfRange(byteBuf, 8 * i, 8 * i + 8);
                        long remote_timestamp = Longs.fromByteArray(currLong);
                        timestampVectorWriteLock.lock();
                        timestampVector.set(i, Math.max(timestampVector.get(i), remote_timestamp));
                        timestampVectorWriteLock.unlock();
                    }
                }
            }
        });
        sendThread.start();
        recieveThread.start();
    }
    public boolean isAlive(int nodeID) {
        if (System.currentTimeMillis() - timestampVector.get(nodeID) < delayMs * ((Math.log(N) / Math.log(2)) + safetyRounds)) {
            return true;
        } else {
            return false;
        }
    }

}
