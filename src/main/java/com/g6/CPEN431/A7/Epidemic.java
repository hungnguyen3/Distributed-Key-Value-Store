package com.g6.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueResponse.KVResponse;
import com.google.protobuf.ByteString;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.google.common.cache.*;

class Epidemic {
    private List<Node> nodeList;
    private SynchronizedList<Long> timestampVector;
    private int ID;
    private int myID;
    private int N;

    private int delayMs;
    private int port;
    private int safetyRounds;
    private Random rng = new Random();
    public Epidemic(List<Node> nodeList, int myID, int delayMs, int port, int safetyRounds){
        this.nodeList = nodeList;
        this.myID = myID;
        this.N = nodeList.length;
        this.delayMs = delayMs;
        this.port = port;
        this.safetyRounds = safetyRounds;
        timestampVector = new SynchronizedList<Integer>();
        long currentTime = System.currentTimeMillis();
        for(int i = 0; i < N; i++){
            timestampVector.add(currentTime);
        }
    }
    public startEpidemic(){
        DatagramSocket datagramSocket = new DatagramSocket(port);
        byte[] recieveBuffer = new byte[8 * N];
        Lock timestampVectorWriteLock = new ReentrantLock();

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // continually update time and send to a random node
                while(true){
                    timestampVectorWriteLock.lock();
                    timestampVector[myID] = System.currentTimeMillis();
                    timestampVectorWriteLock.unlock();
                    do {
                        ID = rng.nextInt(N);
                    } while(ID == myID);
                    //create a bytebuffer from the timestamp vector
                    byte[] timestampByteBuffer = new byte[]{};
                    for(int i = 0; i < N; i++){
                        timestampByteBuffer = Bytes.concat(timestampByteBuffer, Longs.toByteArray(timestampVector.get(i)))
                    }
                    //send the timestampvector
                    Node sendNode = nodeList.get(ID);
                    InetAddress address = InetAddress.getByName(sendNode.host);
                    DatagramPacket timestampPush = new DatagramPacket(timestampByteBuffer, timestampByteBuffer.length, address, sendNode.epidemicPort);
                    socket.send(timestampPush)

                    Thread.sleep(delayMs);
                }
            }
        });
        //continually recieve messages and update timestamp vector
        Thread recieveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.recieve(packet);
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
    public isAlive(int nodeID) {
        if (System.currentTimeMillis() - timestampVector[nodeID] < delayMs * ((Math.log(N) / Math.log(2)) + safetyRounds)) {
            return true;
        } else {
            return false;
        }
    }

}
