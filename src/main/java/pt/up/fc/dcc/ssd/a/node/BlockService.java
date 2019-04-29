package pt.up.fc.dcc.ssd.a.node;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.blockchain.*;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;
import pt.up.fc.dcc.ssd.a.p2p.Network;

public class BlockService  extends BlockChainServiceGrpc.BlockChainServiceImplBase {
    BlockChain m;
    Network n;

    BlockService(BlockChain m, Network n){
        this.m = m;
        this.n = n;
    }

    @Override
    public void helloNode(Hello request, StreamObserver<Hello> responseObserver) {
        Hello myHello = n.getHello();
        responseObserver.onNext(myHello);
        responseObserver.onCompleted();

        n.addP2P(request);
    }

    @Override
    public void getMaxBlockIndex(Type.Empty request, StreamObserver<BlockID> responseObserver) {
        responseObserver.onNext(
                BlockID.newBuilder().setIndex(
                        m.getMaxIndex()
                ).build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void getBlockHash(BlockID request, StreamObserver<BlockHash> responseObserver) {
        byte[] hash =  m.getHashIndex(request.getIndex());

        if(hash != null){
            responseObserver.onNext(
                    BlockHash.newBuilder().setBlockHash(ByteString.copyFrom(hash)).build()
            );
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getBlock(BlockID request, StreamObserver<BlockType> responseObserver) {
        BlockType bloco = m.getBlock(request.getIndex());
        responseObserver.onNext(bloco);
        responseObserver.onCompleted();
    }

    @Override
    public void newLog(LogGossip request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();

        boolean added = m.addLogToPool(request.getLog());
        if(added){
            n.gossipLog(request.getLog());
        }

    }

    @Override
    public void newBlock(BlockGossip request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();

        if(m.contains(request.getBlock().getHash().toByteArray())) {
            /*TODO
                Confiança
             */

            boolean added = m.addNewBlock(request.getBlock());
            if (added) {
                n.gossipBlock(request.getBlock());
            }
        }
    }

}
