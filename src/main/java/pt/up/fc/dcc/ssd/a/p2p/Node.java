package pt.up.fc.dcc.ssd.a.p2p;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.blockchain.*;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;
import pt.up.fc.dcc.ssd.a.node.SecureModule;

import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Node  implements Comparable<Node>{
    private String ip;
    private int port;
    private ByteString id;
    private long firstSeen;
    private Network myNetwork;

    public int getBucketIndex() {
        return bucketIndex;
    }

    public void setBucketIndex(int bucketIndex) {
        this.bucketIndex = bucketIndex;
    }

    private int bucketIndex;

    private long mistrust;

    private ManagedChannel channel;

    private BlockChainServiceGrpc.BlockChainServiceStub asyncStub;
    private BlockChainServiceGrpc.BlockChainServiceBlockingStub blockStub;

    private Lock lock;

    private PublicKey pub;

    private static final Logger logger = Logger.getLogger(Node.class.getName());

    Node(ByteString id, String host, PublicKey pubKey, Network myNetwork) {
        this(id, host, pubKey, myNetwork, Config.port_node);
    }

    Node(ByteString id, String host, PublicKey pubKey, Network myNetwork, int port) {
        this.id = id;
        this.port = port;
        this.ip = host;
        this.firstSeen = System.currentTimeMillis();
        this.pub = pubKey;
        this.myNetwork = myNetwork;
        this.mistrust = firstSeen;
        lock = new ReentrantLock();

        ManagedChannelBuilder chanelBuilder = ManagedChannelBuilder.forAddress(host, port);
        chanelBuilder.usePlaintext();

        ManagedChannel chanel = chanelBuilder.build();
        asyncStub = BlockChainServiceGrpc.newStub(chanel);
        blockStub = BlockChainServiceGrpc.newBlockingStub(chanel);
    }


    public ByteString getId() {
        return id;
    }

    long getMistrust(){
        return mistrust;
    }

    public void changeMistrust(long val){
        lock.lock();
        mistrust += val;
        lock.unlock();
    }
    public void newLog(LogGossip request) {

        asyncStub.newLog(request, new GossipObserver(this));
    }

    public void newBlock(BlockGossip request) {

        asyncStub.newBlock(request, new GossipObserver(this));
    }

    public int getMaxBlockIndex(){
        return blockStub.getMaxBlockIndex(Type.Empty.newBuilder().build()).getIndex();
    }

    public BlockType getBlockByIndex(int index){
        return blockStub.getBlock(BlockID.newBuilder().setIndex(index).build());

    }

    @Override
    public int compareTo(Node node) {
        if(this.mistrust == node.mistrust)
            return 0;
        else if(this.mistrust > node.mistrust)
            return 1;
        else
            return -1;
    }

    public ByteString getHashBlockByIndex(int index) {
        logger.info("Searching for the block "+index);
        return blockStub.getBlockHash(BlockID.newBuilder().setIndex(index).build()).getBlockHash();
    }

    public Network getNetwork() {
        return this.myNetwork;
    }

    public boolean verifyAssin(byte[] plaintext, byte[] signature){
        return SecureModule.verifySign(plaintext, signature, pub);
    }
}