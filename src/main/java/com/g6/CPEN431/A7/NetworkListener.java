package com.g6.CPEN431.A7;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse.KVResponse;
import ca.NetSysLab.ProtocolBuffers.Message.Msg;

public class NetworkListener implements Runnable {
    private int port;
    private String address;
    private RequestHandlingLayer requestHandlingLayer;
    private HashRing hashRing;
    private final int REDIRECT_MAX_COUNT = 3;
    private final int REMOVE_MAX_COUNT = 5;
    private final int SUCCESS_CODE = 0x00;
    private final int KEY_TOO_LONG_CODE = 0x06;
    private final int VALUE_TOO_LONG_CODE = 0x07;
    private final KVResponse ERROR_NON_EXISTENT = KVResponse.newBuilder().setErrCode(0x01).build();
    private final KVResponse ZERO_ERR_CODE = KVResponse.newBuilder().setErrCode(0x00).build();
    private static final int BUFFER_SIZE = 16 * 1024;
    private static final int PUT_REQUEST = 0x01;
    private static final int GET_REQUEST = 0x02;
    private static final int REMOVE_REQUEST = 0x03;

    public NetworkListener(String address, int port, RequestHandlingLayer requestHandlingLayer, HashRing hashRing) {
        this.address = address;
        this.port = port;
        this.requestHandlingLayer = requestHandlingLayer;
        this.hashRing = hashRing;
    }

    /**
     * Listens for incoming requests and sends back responses.
     */
    @Override
    public void run() {
        System.out.println("Starting the network listener");
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            while (true) {
                // Receive incoming request
                DatagramPacket requestMessagePacket = receiveIncomingRequest(datagramSocket);

                // Parse the incoming message as a KVRequest protobuf message
                Msg reqMsg;
                KVRequest request;
                try {
                    reqMsg = Msg.parseFrom(Arrays.copyOfRange(requestMessagePacket.getData(), 0, requestMessagePacket.getLength()));
                    request = KVRequest.parseFrom(reqMsg.getPayload());
                    // Process the request...
                } catch (InvalidProtocolBufferException e) {
                    System.out.println("ERROR: PROTOBUF PARSE" + e);
                    continue;
                }

                // Process the incoming request and get the response
                KeyValueResponse.KVResponse processedResponse = processRequest(reqMsg, request, requestMessagePacket, datagramSocket);
                if (processedResponse == null) {
                    continue;
                }

                // Send the response message back to the client
                sendResponseToClient(reqMsg, processedResponse, requestMessagePacket, datagramSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private DatagramPacket receiveIncomingRequest(DatagramSocket datagramSocket) throws IOException {
        byte[] requestMessageBuffer = new byte[BUFFER_SIZE];
        DatagramPacket requestMessagePacket = new DatagramPacket(requestMessageBuffer, requestMessageBuffer.length);
        datagramSocket.receive(requestMessagePacket);
        return requestMessagePacket;
    }

    private void sendResponseToClient(Msg reqMsg, KeyValueResponse.KVResponse processedResponse, DatagramPacket requestMessagePacket, DatagramSocket datagramSocket) throws IOException {
        byte[] responseBytes = processedResponse.toByteArray();

        // Construct the response message with the response and computed checksum
        Msg resMsg = Msg.newBuilder()
                .setMessageID(reqMsg.getMessageID())
                .setPayload(ByteString.copyFrom(responseBytes))
                .setCheckSum(checksum(reqMsg.getMessageID().toByteArray(), responseBytes))
                .build();

        // Send the response message back to the client
        byte[] responseMessageBytes = resMsg.toByteArray();

        // Determine the client host and port to send the response message back to
        InetAddress clientHost = reqMsg.getOriginalSenderHost().isEmpty() ? requestMessagePacket.getAddress() : InetAddress.getByName(reqMsg.getOriginalSenderHost().substring(1));
        int clientPort = reqMsg.getOriginalSenderPort() == 0 ? requestMessagePacket.getPort() : reqMsg.getOriginalSenderPort();

        // Send the response back to the client
        DatagramPacket responseMessagePacket = new DatagramPacket(responseMessageBytes, responseMessageBytes.length, clientHost, clientPort);
        datagramSocket.send(responseMessagePacket);
    }

    private KeyValueResponse.KVResponse processRequest(Msg reqMsg, KVRequest request, DatagramPacket requestMessagePacket, DatagramSocket datagramSocket) throws IOException {
        ByteString reqMsgId = reqMsg.getMessageID();

        // Process the incoming request and get the response
        KeyValueResponse.KVResponse processedResponse = null;

        // REDIRECT/FORWARD LOGIC
        if(this.hashRing.getMembershipCount() > 1) {
            int reqCommand = request.getCommand();
            int redirectCount = reqMsg.getRedirectCount();
            int replicateCount = reqMsg.getReplicateCount();
            int removeCount = reqMsg.getRemoveCount();
            long firstReceivedAtPrimaryTimestamp = reqMsg.getFirstReceivedAtPrimaryTimestamp();

            // Set client's host and port for the directed request
            String clientHost = reqMsg.getOriginalSenderHost().equals("")? requestMessagePacket.getAddress().toString() : reqMsg.getOriginalSenderHost();
            int clientPort = reqMsg.getOriginalSenderPort() == 0? requestMessagePacket.getPort() : reqMsg.getOriginalSenderPort();

            // Set the first received at primary node timestamp
            if(firstReceivedAtPrimaryTimestamp == 0) {
                firstReceivedAtPrimaryTimestamp = System.currentTimeMillis();
            }

            Msg.Builder forwardMsgBuilder = Msg.newBuilder()
                    .setMessageID(reqMsgId)
                    .setPayload(reqMsg.getPayload())
                    .setCheckSum(reqMsg.getCheckSum())
                    .setOriginalSenderHost(clientHost)
                    .setOriginalSenderPort(clientPort)
                    .setRedirectCount(redirectCount + 1)
                    .setFirstReceivedAtPrimaryTimestamp(firstReceivedAtPrimaryTimestamp);

            // PUT REQUEST
            if(reqCommand == PUT_REQUEST) {
                if(redirectCount == 0) {
                    // Get the node to redirect to
                    Node forwardNode = hashRing.getPrimaryNodeForKey(request.getKey().toByteArray());
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
                        return null;
                    } else if (forwardNode.getPort() == port && forwardNode.getHost().equals(address)){ // If the forwardNode is the current node, process the request
                        // Put key-value pair into storage
                        processedResponse = requestHandlingLayer.processPutRequest(request, reqMsgId, firstReceivedAtPrimaryTimestamp, requestMessagePacket);

                        // Perform chain replication
                        replicateCount = replicateCount + 1;
                        Node nextReplica = hashRing.getNextReplica();
                        if (nextReplica == null) {
                            System.out.println("FAILED TO GET THE NEXT REPLICA");
                        }
                        
                        Msg replicateMsg = forwardMsgBuilder.setReplicateCount(replicateCount).setReceivingNodeID(nextReplica.getNodeID()).build();

                        byte [] replicateMessageBytes = replicateMsg.toByteArray();

                        // Create forwarded message packet with forwardNode address and port
                        DatagramPacket replicateMessagePacket = new DatagramPacket(replicateMessageBytes, replicateMessageBytes.length, InetAddress.getByName(nextReplica.getHost()), nextReplica.getPort());

                        // Send the forwarded message packet to the forwardNode
                        datagramSocket.send(replicateMessagePacket);

                        // Continue to listen for incoming requests
                        return null;
                    } else {
                        System.out.println("ERROR: SHOULD NEVER HAPPEN");
                    }
                } else if (replicateCount != 3) { // put into primary node, 1st replica, and 2nd replica
                    // Put key-value pair into storage
                    processedResponse = requestHandlingLayer.processPutRequest(request, reqMsgId, firstReceivedAtPrimaryTimestamp, requestMessagePacket);

                    // Perform chain replication
                    replicateCount = replicateCount + 1;
                    Node nextReplica = hashRing.getNextReplica();
                    if (nextReplica == null) {
                        System.out.println("FAILED TO GET THE NEXT REPLICA");
                    }
                    
                    Msg replicateMsg = forwardMsgBuilder.setReplicateCount(replicateCount).setReceivingNodeID(nextReplica.getNodeID()).build();

                    byte [] replicateMessageBytes = replicateMsg.toByteArray();

                    // Create forwarded message packet with forwardNode address and port
                    DatagramPacket replicateMessagePacket = new DatagramPacket(replicateMessageBytes, replicateMessageBytes.length, InetAddress.getByName(nextReplica.getHost()), nextReplica.getPort());

                    // Send the forwarded message packet to the forwardNode
                    datagramSocket.send(replicateMessagePacket);

                    // Continue to listen for incoming requests
                    return null;
                } else {
                    // This is the last replication
                    processedResponse = requestHandlingLayer.processPutRequest(request, reqMsgId, firstReceivedAtPrimaryTimestamp, requestMessagePacket);
                }
            } 
            // GET REQUESTS
            else if (reqCommand == GET_REQUEST && redirectCount <= REDIRECT_MAX_COUNT) {
                // If this is never redirected, try to find the primary Node
                // TODO: if this is the primary node, execute instead of redirecting
                if (redirectCount == 0) {
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
                    return null;
                }
                
                // Try to search the key here
                processedResponse = requestHandlingLayer.processRequestOtherThanPut(request, reqMsgId);

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
                    return null;
                }
            }
            // REMOVE REQUESTS
            else if (reqCommand == REMOVE_REQUEST && redirectCount <= REMOVE_MAX_COUNT) {
                // Try to search the key here
                processedResponse = requestHandlingLayer.processRequestOtherThanPut(request, reqMsgId);

                if (processedResponse.getErrCode() != KEY_TOO_LONG_CODE
                    && processedResponse.getErrCode() != VALUE_TOO_LONG_CODE) {
                    // How many times have we removed the key
                    if (processedResponse.getErrCode() == SUCCESS_CODE) {
                       removeCount = removeCount + 1; 
                    }

                    // Get the node to redirect to
                    Node forwardNode = hashRing.getRedirectNode(request.getKey().toByteArray(), reqMsg.getReceivingNodeID(), redirectCount == 0);

                    // Create a new forward message based on the original message
                    Msg forwardMsg = forwardMsgBuilder.setReceivingNodeID(forwardNode.getNodeID()).setRemoveCount(removeCount).build();
                    
                    // Serialize the forward message to a byte array
                    byte[] forwardMessageBytes = forwardMsg.toByteArray();

                    // Create forwarded message packet with forwardNode address and port
                    DatagramPacket forwardedMessagePacket = new DatagramPacket(forwardMessageBytes, forwardMessageBytes.length, InetAddress.getByName(forwardNode.getHost()), forwardNode.getPort());

                    // Send the forwarded message packet to the forwardNode
                    datagramSocket.send(forwardedMessagePacket);

                    // Continue to listen for incoming requests
                    return null;
                }
            }
            else if (reqCommand == REMOVE_REQUEST && redirectCount == (REMOVE_MAX_COUNT + 1)) {
                // If we have finished all removes and at least remove one, return a success
                if (removeCount > 0) {
                    processedResponse = ZERO_ERR_CODE;
                }
                if (removeCount == 0) {
                    processedResponse = ERROR_NON_EXISTENT;
                }
            }
        }

        if(processedResponse == null) {
            processedResponse = requestHandlingLayer.processRequestOtherThanPut(request, reqMsgId);
        }

        return processedResponse;
    }
}

