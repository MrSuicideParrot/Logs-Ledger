package pt.up.fc.dcc.ssd.a.kademlia;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import pt.up.fc.dcc.ssd.a.node.KademeliaService;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class Node{
    private int ip;
    private int port;
    private byte[] id;
    private long lastSeen;
    private ManagedChannelBuilder chanelBuilder;

    Node(byte[] id, String host, int port){
        this.id = id;
        this.seenNow();
        this.port = port;
        chanelBuilder = ManagedChannelBuilder.forAddress(host, port);
        chanelBuilder.usePlaintext();
    }

    void seenNow(){
        this.lastSeen = System.currentTimeMillis();
    }

    long getLastSeen(){
        return lastSeen;
    }

    byte[] getId(){
        return id;
    }

    void findNode(){
        Channel channel = chanelBuilder.build();
        KademliaServiceGrpc.KademliaServiceStub asyncStub = KademliaServiceGrpc.newStub(channel);
    }

    void findValue(){

    }


    static byte[] xorID(byte[] id1,byte[] id2){
        byte[] result = new byte[Config.id_length/8];

        for(int i = 0; i < Config.id_length ; i++){
            result[i] = (byte)(id1[i] ^ id2[i]);
        }

        return result;
    }

    static int getDistanceID(byte[] xored){
        int count = 0;

        BitSet xoredBit = BitSet.valueOf(xored);

        for(; !xoredBit.get(count); ++count);

        return Config.id_length - count;


    }

    static int getDistanceID(byte[] id1, byte[] id2){
        return getDistanceID(xorID(id1, id2));
    }


    public int getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }



    @Override
    public boolean equals(Object o) {
        return Arrays.equals(this.id,((Node)o).id);
    }

    @Override
    public int hashCode() {
        return ByteBuffer.wrap(this.id).getInt();
    }
}
