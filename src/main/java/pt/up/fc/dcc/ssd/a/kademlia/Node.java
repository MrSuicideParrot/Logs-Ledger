package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.BitSet;

public class Node {
    private int ip;
    private int port;
    private byte[] id;
    private long lastSeen;

    Node(byte[] id){
        this.id = id;
        this.seenNow();
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
}
