package pt.up.fc.dcc.ssd.a.p2p;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.blockchain.BlockChainServiceGrpc;
import pt.up.fc.dcc.ssd.a.blockchain.BlockType;
import pt.up.fc.dcc.ssd.a.blockchain.LogType;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Node  implements Comparable<Node>{
    private int ip;
    private int port;
    private byte[] id;
    private long firstSeen;

    private long mistrust;

    private ManagedChannel channel;

    private BlockChainServiceGrpc.BlockChainServiceStub asyncStub;
    private BlockChainServiceGrpc.BlockChainServiceBlockingStub blockStub;

    private Lock lock;

    private static final Logger logger = Logger.getLogger(Node.class.getName());

    Node(byte[] id, String host) {
        this(id, host, Config.port);
    }

    Node(byte[] id, String host, int port) {
        this.id = id;
        this.port = port;
        this.firstSeen = System.currentTimeMillis();

        this.mistrust = firstSeen;
        lock = new ReentrantLock();

        ManagedChannelBuilder chanelBuilder = ManagedChannelBuilder.forAddress(host, port);
        chanelBuilder.usePlaintext();

        ManagedChannel chanel = chanelBuilder.build();
        asyncStub = BlockChainServiceGrpc.newStub(chanel);
        blockStub = BlockChainServiceGrpc.newBlockingStub(chanel);
    }


    byte[] getId() {
        return id;
    }

    long getMistrust(){
        return mistrust;
    }

    void changeMistrust(long val){
        lock.lock();
        mistrust += val;
        lock.unlock();
    }

    public void newLog(LogType request) {

        asyncStub.newLog(request, new StreamObserver<Type.Empty>() {
            @Override
            public void onNext(Type.Empty empty) {

            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("Erro a enviar log");
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void newBlock(BlockType request) {

        asyncStub.newBlock(request, new StreamObserver<Type.Empty>() {
            @Override
            public void onNext(Type.Empty empty) {

            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("Erro a enviar bloco");
            }

            @Override
            public void onCompleted() {

            }
        });
    }


    @Override
    public int compareTo(Node node) {
        return 0;
    }
}