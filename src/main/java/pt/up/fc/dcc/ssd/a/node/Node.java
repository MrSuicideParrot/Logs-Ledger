package pt.up.fc.dcc.ssd.a.node;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.up.fc.dcc.ssd.a.kademlia.DHT;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class Node {
    final static int port = 34832;
    private byte[] nodeID;
    private InetAddress myIP;
    private DHT kadmelia;
    private ServerBuilder serverBuilder;
    private Server server;

    Node(){
        myIP = getMyIP();
        serverBuilder = ServerBuilder.forPort(port);
    }

    void start() throws IOException {
        nodeID = initialize();

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

    public static void main(String[] args) throws Exception{
        Node no = new Node();
        no.start();
        no.blockUntilShutdown();

    }
}
