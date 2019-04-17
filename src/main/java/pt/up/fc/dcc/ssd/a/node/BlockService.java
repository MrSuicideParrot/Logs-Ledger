package pt.up.fc.dcc.ssd.a.node;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.blockchain.*;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;

public class BlockService  extends BlockChainServiceGrpc.BlockChainServiceImplBase {
    BlockChain m;

    BlockService(BlockChain m){
        this.m = m;
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
    public void newLog(LogType request, StreamObserver<Type.Empty> responseObserver) {
        m.addLogToPool(request);
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void newBlock(BlockType request, StreamObserver<Type.Empty> responseObserver) {
        m.newBlockAnnounc(request);
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
