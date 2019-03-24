package pt.up.fc.dcc.ssd.a.kademlia;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.UnsupportedEncodingException;
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

    Node(NodeM noGRPC){
        this.id = noGRPC.getNodeID().toByteArray();
        this.port = noGRPC.getPort();
        this.ip = noGRPC.getIpv4();
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

    NodeM findNode(byte[] target){
        try {
            Channel channel = chanelBuilder.build();
            KademliaServiceGrpc.KademliaServiceBlockingStub stub = KademliaServiceGrpc.newBlockingStub(channel);
            NodeM response = stub.findNode(NodeIDM.newBuilder()
                    .setNodeID(ByteString.copyFrom(target))
                    .setMyNodeID(ByteString.copyFrom(pt.up.fc.dcc.ssd.a.node.Node.getIP(),"UTF-8"))
                    .build());

            this.seenNow();
            return response;
        }
        catch (UnsupportedEncodingException e){
            System.err.println("Colocaste mal o encoding!");
            return null;
        }
        catch (StatusRuntimeException e){
            //TODO tirar o no
            return null;
        }

    }

    NodeM findValue(byte[] target){
        return null;
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

    NodeM toNodeM(){
        return NodeM.newBuilder()
                .setIpv4(ip)
                .setPort(port)
                .setNodeID(ByteString.copyFrom(id))
                .build();
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
