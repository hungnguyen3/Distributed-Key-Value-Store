package com.g6.CPEN431.A7;

import ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest;
import ca.NetSysLab.ProtocolBuffers.KeyValueResponse.KVResponse;
import ca.NetSysLab.ProtocolBuffers.Message.Msg;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 10014;

    private static final int REQUEST_ID_LENGTH = 16;
    private static final int REPLY_ID_LENGTH = 16;
    private static final int REPLY_MAX_PAYLOAD_SIZE = 16 * 1024;

    private static final int CLIENT_IP_SIZE = 4;
    private static final int PORT_SIZE = 2;
    private static final int RANDOM_BYTES_SIZE = 2;
    private static final int CURRENT_TIME_SIZE = 8;

    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_TIMEOUT = 100;

    private static final int PUT_COMMAND = 0x01;
    private static final int GET_COMMAND = 0x02;
    private static final int REM_COMMAND = 0x03;
    private static final int SHUTDOWN_COMMAND = 0x04;
    private static final int WIPE_OUT_COMMAND = 0x05;
    private static final int IS_ALIVE = 0x06;
    private static final int GET_PID_COMMAND = 0x07;
    private static final int GET_MEMBERSHIP_COUNT_COMMAND = 0x08;

    // IGNORE THIS FILE. THIS IS A FILE FOR TESTING
    private static String server_ip = SERVER_IP;
    private static int server_port = SERVER_PORT;
    public static void main(String[] args) {
        if (args.length >= 2) {
            server_ip = args[0];
            server_port = Integer.parseInt(args[1]);
        }
        while (true) {
            KVRequest is_alive_requestPayloadBytes = KVRequest.newBuilder().setCommand(IS_ALIVE).build();
            System.out.println("isAlive request:");
            if(sendRequest(is_alive_requestPayloadBytes)) {
                break;
            }
        }

        KVRequest put_requestPayloadBytes = KVRequest.newBuilder().setCommand(PUT_COMMAND).build();
        KVRequest get_requestPayloadBytes = KVRequest.newBuilder().setCommand(GET_COMMAND).build();
        KVRequest rem_requestPayloadBytes = KVRequest.newBuilder().setCommand(REM_COMMAND).build();
        KVRequest wipeOut_requestPayloadBytes = KVRequest.newBuilder().setCommand(WIPE_OUT_COMMAND).build();
        KVRequest getPid_requestPayloadBytes = KVRequest.newBuilder().setCommand(GET_PID_COMMAND).build();
        KVRequest getMembershipCount_requestPayloadBytes = KVRequest.newBuilder().setCommand(GET_MEMBERSHIP_COUNT_COMMAND).build();
        KVRequest shutdown_requestPayloadBytes = KVRequest.newBuilder().setCommand(SHUTDOWN_COMMAND).build();

        System.out.println("Put request:");
        sendRequest(put_requestPayloadBytes);

        System.out.println("Get request:");
        sendRequest(get_requestPayloadBytes);

        System.out.println("Rem request:");
        sendRequest(rem_requestPayloadBytes);

        System.out.println("Wipeout request:");
        sendRequest(wipeOut_requestPayloadBytes);

        System.out.println("getPid request:");
        sendRequest(getPid_requestPayloadBytes);

        System.out.println("GetMemberShip request:");
        sendRequest(getMembershipCount_requestPayloadBytes);

        System.out.println("Shutdown request:");
        sendRequest(shutdown_requestPayloadBytes);
    }

    private static boolean sendRequest(KVRequest requestPayload) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] requestPayloadBytes = requestPayload.toByteArray();
            // Create the request message
            byte[] requestId = createUniqueId(socket);

            Msg requestMessage = Msg.newBuilder()
                    .setMessageID(ByteString.copyFrom(requestId))
                    .setPayload(ByteString.copyFrom(requestPayloadBytes))
                    .setCheckSum(checksum(requestId, requestPayloadBytes))
                    .build();

            byte[] requestPacketBytes = requestMessage.toByteArray();
            DatagramPacket requestPacket = new DatagramPacket(requestPacketBytes, requestPacketBytes.length, InetAddress.getByName(server_ip), server_port);

            int retries = 0;
            int timeout = INITIAL_TIMEOUT;

            byte[] responsePayload = null;
            while(retries < MAX_RETRIES) {
                if(retries == 0){
                    //System.out.println("initial request");
                } else {
                    //System.out.println("retry #" + retries);
                }

                try {
                    // Send the request packet & set timeout
                    socket.send(requestPacket);
                    socket.setSoTimeout(timeout);

                    // Create a reply packet
                    byte[] replyBuffer = new byte[REPLY_ID_LENGTH + REPLY_MAX_PAYLOAD_SIZE];
                    DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);

                    try {
                        socket.receive(replyPacket);

                        // Exclude the padding
                        byte[] responseBytes = Arrays.copyOfRange(replyPacket.getData(), 0, replyPacket.getLength());

                        // Use the Message class to parse the response bytes
                        Msg responseMessage = Msg.parseFrom(responseBytes);

                        // Get the reply's uniqueId
                        byte[] responseId = responseMessage.getMessageID().toByteArray();

                        if (Arrays.equals(responseId, requestId)) {
                            // Get the response payload
                            responsePayload = responseMessage.getPayload().toByteArray();
                            break;
                        } else {
                            // This will also solve message reordering because of the timestamp in the uniqueId
                            throw new IOException("Reply packet corrupted or lost, replyId is not the same with requestId");
                        }
                    } catch (SocketTimeoutException e) {
                        //System.out.println("Error receiving reply packet after " + timeout + "ms: " + e.getMessage());
                        retries++;
                        timeout *= 2;
                    }
                } catch (IOException e) {
                    //System.out.println("Error receiving packet: " + e.getMessage());
                    retries++;
                    timeout *= 2;
                }
            }

            if (responsePayload != null) {
                KVResponse response = KVResponse.parseFrom(responsePayload);

                int ErrCode = response.getErrCode();

                // Output the ErrCode from the server
                System.out.println("Errcode: " + ErrCode);

                System.out.println("Whole response: " + response);

                return true;
            } else {
                System.out.println("Error: operation failed after 3 retries");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] createUniqueId(DatagramSocket socket) throws UnknownHostException {
        byte[] requestUniqueId = new byte[REQUEST_ID_LENGTH];

        // Get the client's IP address in bytes
        byte[] clientIp = InetAddress.getLocalHost().getAddress();
        System.arraycopy(clientIp, 0, requestUniqueId, 0, CLIENT_IP_SIZE);

        // Get the client's port in bytes
        byte[] clientPort = ByteBuffer.allocate(PORT_SIZE).putShort((short) socket.getLocalPort()).array();
        System.arraycopy(clientPort, 0, requestUniqueId, CLIENT_IP_SIZE, PORT_SIZE);

        // Generate 2 random bytes
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[RANDOM_BYTES_SIZE];
        random.nextBytes(randomBytes);
        System.arraycopy(randomBytes, 0, requestUniqueId, CLIENT_IP_SIZE+PORT_SIZE, RANDOM_BYTES_SIZE);

        // Get the current time in nanoseconds in bytes
        byte[] requestTime = ByteBuffer.allocate(CURRENT_TIME_SIZE).putLong(System.nanoTime()).array();
        System.arraycopy(requestTime, 0, requestUniqueId, CLIENT_IP_SIZE+PORT_SIZE+RANDOM_BYTES_SIZE, CURRENT_TIME_SIZE);

        return requestUniqueId;
    }

    private static long checksum(byte[] messageID, byte[] payload) {
        ByteBuffer buffer = ByteBuffer.allocate(messageID.length + payload.length);
        buffer.put(messageID);
        buffer.put(payload);
        buffer.flip();
        byte[] bufferArray = buffer.array();
        CRC32 crc = new CRC32();
        crc.update(bufferArray, 0, bufferArray.length);
        return crc.getValue();
    }
}