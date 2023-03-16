package com.g6.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import ca.NetSysLab.ProtocolBuffers.Message.Msg;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.protobuf.ByteString;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;


public class NetworkLayer implements Runnable {
    private int port;
    private String address;
    private RequestHandlingLayer requestHandlingLayer;
    private HashRing hashRing;
    private final int REDIRECT_MAX_COUNT = 5;
    private final int SUCCESS_CODE = 0x00;
    private final int KEY_TOO_LONG_CODE = 0x06;
    private final int VALUE_TOO_LONG_CODE = 0x07;
    private final int OUT_OF_MEM_CODE = 0x02;

    /**
     * Constructor to create a new NetworkLayer.
     * @param port the port to listen on
     * @param requestHandlingLayer the request handling layer to use
     * @param hashRing the hash ring to use for node lookups
     */
    public NetworkLayer(String address, int port, RequestHandlingLayer requestHandlingLayer, HashRing hashRing) {
        this.address = address;
        this.port = port;
        this.requestHandlingLayer = requestHandlingLayer;
        this.hashRing = hashRing;
    }

    /**
     * Computes the CRC32 checksum of a message ID and payload.
     * @param messageID the message ID
     * @param payload the payload
     * @return the computed checksum value
     */
    private static long checksum(byte[] messageID, byte[] payload) {
        // Concatenate the message ID and payload into a single byte array
        ByteBuffer buffer = ByteBuffer.allocate(messageID.length + payload.length);
        buffer.put(messageID);
        buffer.put(payload);
        buffer.flip();
        byte[] bufferArray = buffer.array();

        // Compute and return the CRC32 checksum of the concatenated byte array
        CRC32 crc = new CRC32();
        crc.update(bufferArray, 0, bufferArray.length);
        return crc.getValue();
    }

    /**
     * Listens for incoming requests and sends back responses.
     */
    @Override
    public void run() {

        //Thread for to poll for rejoins every 5 seconds
        Thread detectRejoinThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //Check to see if any of the dead nodes has rejoined and update HashRing.
                    ArrayList<TransferRequest> transferRequests =  hashRing.checkAndHandleRejoins();
                    for (TransferRequest transferRequest : transferRequests) {
                        // System.out.println("Transfer request detected range: " + transferRequest.getRange() + "from node with ID: " + (port - 10000) + " to node with ID: " + transferRequest.getDestinationNode().getNodeID());
                        requestHandlingLayer.performTransfer(transferRequest);
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted");
                    }
                }
            }
        });
        detectRejoinThread.start();

        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            while (true) {
                // Receive incoming request
                byte[] requestMessageBuffer = new byte[16*1024];
                DatagramPacket requestMessagePacket = new DatagramPacket(requestMessageBuffer, requestMessageBuffer.length);
                datagramSocket.receive(requestMessagePacket);

                // Parse the incoming message as a KVRequest protobuf message
                Msg reqMsg = Msg.parseFrom(Arrays.copyOfRange(requestMessagePacket.getData(), 0, requestMessagePacket.getLength()));
                ByteString reqMsgId = reqMsg.getMessageID();
                KVRequest request = KVRequest.parseFrom(reqMsg.getPayload());

                // Process the incoming request and get the response
                KeyValueResponse.KVResponse processedResponse = null;

                //Routes the request to the correct handler. Based on UDP Protocol current node will not make sure forwarding succeeds. It will
                //let the client send retries and forward retries for the client.
                if(this.hashRing.getMembershipCount() > 1) {
                    int reqCommand = request.getCommand();
                    int redirectCount = reqMsg.getRedirectCount();

                    // Set client's host and port for the directed request
                    String clientHost = reqMsg.getOriginalSenderHost().equals("")? requestMessagePacket.getAddress().toString() : reqMsg.getOriginalSenderHost();
                    int clientPort = reqMsg.getOriginalSenderPort() == 0? requestMessagePacket.getPort() : reqMsg.getOriginalSenderPort();

                    Msg.Builder forwardMsgBuilder = Msg.newBuilder()
                            .setMessageID(reqMsgId)
                            .setPayload(reqMsg.getPayload())
                            .setCheckSum(reqMsg.getCheckSum())
                            .setOriginalSenderHost(clientHost)
                            .setOriginalSenderPort(clientPort)
                            .setRedirectCount(redirectCount + 1);

                    if(reqCommand == 0x01 && redirectCount == 0) {
                        // Get the node to redirect to
                        Node forwardNode = hashRing.getNodeForKey(request.getKey().toByteArray());
                        // If the forwardNode is not the current node, forward the request to the forwardNode
                        if (forwardNode.getPort() != port || !forwardNode.getHost().equals(address)) {
                            // Create a new forward message based on the original message
                            Msg forwardMsg = forwardMsgBuilder.setReceivingNodeID(forwardNode.getNodeID()).build();

                            // Serialize the forward message to a byte array
                            byte[] forwardMessageBytes = forwardMsg.toByteArray();

                            // Create forwarded message packet with forwardNode address and port
                            DatagramPacket forwardedMessagePacket = new DatagramPacket(forwardMessageBytes, forwardMessageBytes.length, InetAddress.getByName(forwardNode.getHost()), forwardNode.getPort());

                            // Send the forwarded message packet to the forwardNode
                            datagramSocket.send(forwardedMessagePacket);

                            // Continue to listen for incoming requests
                            continue;
                        }
                    } else if ((reqCommand == 0x02 || reqCommand == 0x03) && redirectCount < REDIRECT_MAX_COUNT) {
                        // Try to search the key here
                        processedResponse = requestHandlingLayer.processRequest(request, reqMsgId);

                        // If key not found, continue to redirect
                        if (processedResponse.getErrCode() != SUCCESS_CODE
                                && processedResponse.getErrCode() != KEY_TOO_LONG_CODE
                                && processedResponse.getErrCode() != VALUE_TOO_LONG_CODE) {
                            // Get the node to redirect to
                            Node forwardNode = hashRing.getRedirectNode(request.getKey().toByteArray(), reqMsg.getReceivingNodeID(), redirectCount == 0);

                            // Create a new forward message based on the original message
                            Msg forwardMsg = forwardMsgBuilder.setReceivingNodeID(forwardNode.getNodeID()).build();

                            // Serialize the forward message to a byte array
                            byte[] forwardMessageBytes = forwardMsg.toByteArray();

                            // Create forwarded message packet with forwardNode address and port
                            DatagramPacket forwardedMessagePacket = new DatagramPacket(forwardMessageBytes, forwardMessageBytes.length, InetAddress.getByName(forwardNode.getHost()), forwardNode.getPort());

                            // Send the forwarded message packet to the forwardNode
                            datagramSocket.send(forwardedMessagePacket);

                            // Continue to listen for incoming requests
                            continue;
                        }
                    }
                }

                if(processedResponse == null) {
                    processedResponse = requestHandlingLayer.processRequest(request, reqMsgId);
                }

                byte[] responseBytes = processedResponse.toByteArray();

                // Construct the response message with the response and computed checksum
                Msg resMsg = Msg.newBuilder()
                        .setMessageID(reqMsgId)
                        .setPayload(ByteString.copyFrom(responseBytes))
                        .setCheckSum(checksum(reqMsgId.toByteArray(), responseBytes))
                        .build();

                // Send the response message back to the client
                byte[] responseMessageBytes = resMsg.toByteArray();

                // Determine the client host and port to send the response message back to
                InetAddress clientHost = reqMsg.getOriginalSenderHost().equals("")? requestMessagePacket.getAddress() : InetAddress.getByName(reqMsg.getOriginalSenderHost().substring(1));
                int clientPort = reqMsg.getOriginalSenderPort() == 0? requestMessagePacket.getPort() : reqMsg.getOriginalSenderPort();

                // Send the response back to the client
                DatagramPacket responseMessagePacket = new DatagramPacket(responseMessageBytes, responseMessageBytes.length, clientHost, clientPort);
                datagramSocket.send(responseMessagePacket);

                /*
                if(request.getCommand() == 0x01) {
                    System.out.println("Sent PUT response to client!!! " + clientHost + ":" + clientPort + "Err code " + processedResponse.getErrCode());
                }
                if(request.getCommand() == 0x02) {
                    System.out.println("Sent GET response to client!!! " + clientHost + ":" + clientPort + "Err code " + processedResponse.getErrCode());
                }
                if(request.getCommand() == 0x03) {
                    System.out.println("Sent REM response to client!!! " + clientHost + ":" + clientPort + "Err code " + processedResponse.getErrCode());
                }
                */
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
