package com.g6.CPEN431.A7;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

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
    private final Lock timestampVectorWriteLock = new ReentrantLock();

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

    private int getRandomNodeID() {
        List<Integer> deadNodes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (!isAlive(i)) {
                deadNodes.add(i);
            }
        }

        // If only 5 nodes are alive, broadcast to all nodes regardless of alive status
        if (deadNodes.size() == N - 1) {
            int randomID;
            do {
                randomID = rng.nextInt(N);
            } while (randomID == myID);
            return randomID;
        }

        // Otherwise, find a random alive node
        int randomID;
        do {
            randomID = rng.nextInt(N);
        } while (randomID == myID || !isAlive(randomID));

        return randomID;
    }


    public void startEpidemic() throws SocketException {
        final DatagramSocket datagramSocket = new DatagramSocket(port);
        final byte[] receiveBuffer = new byte[8 * N];

        //this thread will continually update its own timestamp in the timestamp vector and then
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // continually update time and send to a random node
                int k = 0;
                while(true){

                    ID = getRandomNodeID();

                    timestampVectorWriteLock.lock();
                    //set the timestamp for this node to updated to the current time
                    timestampVector.set(myID, System.currentTimeMillis());
                    timestampVectorWriteLock.unlock();

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

                    //create the packet holding the timestamp vector and send to random node
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

                    if(!isAlive(myID)){
                        System.out.println("Node " + myID + " came back alive, broadcasting to all nodes");
                        broadcast(datagramSocket);
                    }
                }
            }
        });

        //continually receive epidemic messages from other nodes and update timestamp vector accordingly
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //create a packet to hold incoming data
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
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
                        //for each node, the timestamp value will be set to the maximum of the current local timestamp for
                        //that node and the one it recieved via the epidemic protocol
                        timestampVector.set(i, Math.max(timestampVector.get(i), remote_timestamp));
                        timestampVectorWriteLock.unlock();
                    }
                }
            }
        });
        //start threads
        sendThread.start();
        receiveThread.start();
    }

    private int previousDeadNodesCount = -1;
    private long lastPrintTime = System.currentTimeMillis();
    private int printInterval = 20000; // Print every 60 seconds (60000 milliseconds)

    private void printDeadNodes() {
        List<Integer> deadNodes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (!isAlive(i)) {
                deadNodes.add(i);
            }
        }

        long currentTime = System.currentTimeMillis();
        if (deadNodes.size() != previousDeadNodesCount || (currentTime - lastPrintTime >= printInterval)) {
            System.out.println("Node " + myID + " has the following dead nodes: " + deadNodes);
            previousDeadNodesCount = deadNodes.size();
            lastPrintTime = currentTime;
        }
    }

    //Is alive is inferred by checking the timestamp for that node with the given ID, by entropy if a node is participating in
    //epidemic protocol then the timestamp for that node should be no older than what is found by the below formula
    public boolean isAlive(int nodeID) {
        if (System.currentTimeMillis() - timestampVector.get(nodeID) < delayMs * ((Math.log(N) / Math.log(2)) + safetyRounds)) {
            return true;
        } else {
            return false;
        }
    }

    //if Node has been dead then returns to being alive update the timestamp locally then broadcast to all nodes
    private void broadcast(DatagramSocket datagramSocket){
        System.out.println("BROADCASTING ON EPIDEMIC");

        timestampVectorWriteLock.lock();
        //set the timestamp for this node to updated to the current time
        timestampVector.set(myID, System.currentTimeMillis());
        timestampVectorWriteLock.unlock();

        //create a bytebuffer from the timestamp vector
        byte[] timestampByteBuffer = new byte[]{};
        for(int i = 0; i < N; i++){
            timestampByteBuffer = Bytes.concat(timestampByteBuffer, Longs.toByteArray(timestampVector.get(i)));
        }


        for(int i = 0; i < N; i++){
            Node sendNode = nodeList.get(i);
            InetAddress address = null;
            try {
                address = InetAddress.getByName(sendNode.getHost());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            //create the packet holding the timestamp vector and send to random node
            DatagramPacket timestampPush = new DatagramPacket(timestampByteBuffer, timestampByteBuffer.length, address, sendNode.getEpidemicPort());

            try {
                datagramSocket.send(timestampPush);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
