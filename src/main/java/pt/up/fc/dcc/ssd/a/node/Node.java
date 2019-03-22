package pt.up.fc.dcc.ssd.a.node;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.up.fc.dcc.ssd.a.kademlia.DHT;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class Node {
    byte[] nodeID;
    InetAddress myIP;
    DHT kadmelia;
    Server server;

    Node(){
        myIP = getMyIP();
    }

    void start(){
        nodeID = initialize();
        //ServerBuilder.addService();

    }

    public static void main(String[] args){
        Node no = new Node();
        no.start();

    }

    byte[] initialize(){
        return null;
    }

    static InetAddress getMyIP(){

        /*try {
            for (NetworkInterface i : NetworkInterface.getNetworkInterfaces()) {
                if (i.isUp() && (!i.isLoopback())) {
                    for (InterfaceAddress j : i.getInterfaceAddresses()) {
                        return j.getAddress();
                    }
                }
            }
        }
        catch (SocketException e){

        }*/
        return null;
    }

}
