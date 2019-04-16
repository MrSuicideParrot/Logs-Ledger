package pt.up.fc.dcc.ssd.a.node;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import pt.up.fc.dcc.ssd.a.tracker.NodeService;
import pt.up.fc.dcc.ssd.a.tracker.TrackerServerGrpc;

import pt.up.fc.dcc.ssd.a.kademlia.DHT;
import pt.up.fc.dcc.ssd.a.utils.Challenge;
import pt.up.fc.dcc.ssd.a.utils.IPGetter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class Node {
    final static int port = 34832;
    private byte[] nodeID;
    static String myIP;
    private DHT kadmelia;
    private ServerBuilder serverBuilder;
    private Server server;

    static String trackerIp = "localhost";

    Node(){
        myIP = IPGetter.getIP();
        serverBuilder = ServerBuilder.forPort(port);
    }

    void start() throws IOException {
        nodeID = initialize();
        System.out.println(Challenge.bytesToHex(nodeID));

        /** Add services **/
        serverBuilder.addService(new KademeliaService(this.kadmelia));

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
        return new NodeService(myIP,"localhost",port+1).getNodeId();
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

    public static String getIP(){
        return null;
    }

    public static void main(String[] args) throws Exception{
        Node no = new Node();
        no.start();
        no.blockUntilShutdown();

    }
}
