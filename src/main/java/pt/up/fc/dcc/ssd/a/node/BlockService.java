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
    public void newLog(LogType request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();

        boolean added = m.addLogToPool(request);
        if(added){
            n.gossipLog(request);
        }
    }

    @Override
    public void newBlock(BlockType request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();

        if(m.contains(request.getHash().toByteArray())) {
            /*TODO
                Confiança
             */

            boolean added = m.addNewBlock(request);
            if (added) {
                n.gossipBlock(request);
            }
        }
    }
}
