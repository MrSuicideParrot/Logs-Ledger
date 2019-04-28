package pt.up.fc.dcc.ssd.a.node;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;


import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.blockchain.BlockChain;
import pt.up.fc.dcc.ssd.a.p2p.Network;
import pt.up.fc.dcc.ssd.a.tracker.*;
import pt.up.fc.dcc.ssd.a.utils.Challenge;
import pt.up.fc.dcc.ssd.a.utils.IPGetter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class Node {
    final static int port = 34832;
    private byte[] nodeID;
    static String myIP;

    private SecureModule sec;
    private ServerBuilder serverBuilder;
    private Server server;

    private Network net;
    private BlockChain block;




    public Node(){
        myIP = IPGetter.getIP();
        serverBuilder = ServerBuilder.forPort(port);

        block = new BlockChain();
        net = new Network();
    }

    void start() throws IOException {
        sec = new SecureModule();
        nodeID = initialize();
        System.out.println(Challenge.bytesToHex(nodeID));
     

        /** Add services **/


        server = serverBuilder.build();
        server.start();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Contacta bootstrap node
     * @return id do no
     */
    byte[] initialize(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(Config.trackerIp,port+1).usePlaintext().build();
        TrackerServerGrpc.TrackerServerBlockingStub blockingStub = TrackerServerGrpc.newBlockingStub(channel);

        challenge zeros = blockingStub.idRequest(empty.newBuilder().build());

        byte[] id = new Challenge(zeros.getZeros()).findID();

        challengeValidation answer = blockingStub.getAnswer(challengeAnswer.newBuilder().setIpv4(myIP).setId(ByteString.copyFrom(id)).build());

        while(!answer.getAnswer()){
            id = new Challenge(zeros.getZeros()).findID();

            answer = blockingStub.getAnswer(challengeAnswer.newBuilder().setIpv4(myIP).setId(ByteString.copyFrom(id)).build());
        }
        Config.myID = id;
        nodeID = id;

        ByteArrayInputStream byteIn = new ByteArrayInputStream(answer.getNodeMap().toByteArray());
        try {
            ObjectInputStream in = new ObjectInputStream(byteIn);
             net.initializeNodes((HashMap<byte[], String>) in.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

//    static InetAddress getMyIP(){
//
//        /*try {
//            for (NetworkInterface i : NetworkInterface.getNetworkInterfaces()) {
//                if (i.isUp() && (!i.isLoopback())) {
//                    for (InterfaceAddress j : i.getInterfaceAddresses()) {
//                        return j.getAddress();
//                    }
//                }
//            }
//        }
//        catch (SocketException e){
//
//        }*/
//        return null;
//    }


    public static void main(String[] args) throws Exception{
        Node no = new Node();
        no.start();
        no.blockUntilShutdown();

    }
}
