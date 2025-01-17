package pt.up.fc.dcc.ssd.a.node;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.blockchain.*;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;
import pt.up.fc.dcc.ssd.a.p2p.Gossip;
import pt.up.fc.dcc.ssd.a.p2p.Network;
import pt.up.fc.dcc.ssd.a.p2p.Node;

import java.util.logging.Logger;

public class BlockService  extends BlockChainServiceGrpc.BlockChainServiceImplBase {
    BlockChain m;
    Network n;

    private static final Logger logger = Logger.getLogger(BlockService.class.getName());

    BlockService(BlockChain m, Network n){
        this.m = m;
        this.n = n;
    }

    @Override
    public void helloNode(Hello request, StreamObserver<Hello> responseObserver) {
        Hello myHello = n.getHello();
        responseObserver.onNext(myHello);
        responseObserver.onCompleted();
        logger.info("Receive new hello from:" + request.getHello().getIpv4());
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

        if(bloco != null) {
            responseObserver.onNext(bloco);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void newLog(LogGossip request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();

        //logger.info("Novo log recebido");
        boolean added = m.addLogToPool(request.getLog());
        if(added){
            new Thread(new Gossip(n,request.getLog())).start();
        }
       /* else
            logger.info("Log já tinha sido recebido");*/
    }

    @Override
    public void newBlock(BlockGossip request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();

        final BlockGossip mRequest = request;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Node nodeCand = n.getNodeID(mRequest.getNodeID());
                if(nodeCand != null) {
                    boolean ver = nodeCand.verifyAssin(mRequest.getBlock().toByteArray(),mRequest.getAssin().toByteArray());

                    if(ver) {
                        //logger.info("Novo bloco recebido");
                        if (!m.contains(mRequest.getBlock().getHash())) {


                            boolean added = m.addNewBlock(mRequest.getBlock(), nodeCand);
                            if (added) {
                                new Thread(new Gossip(n, mRequest.getBlock())).start();
                            }
                            else {
                                // Mau pedido
                                nodeCand.changeMistrust(Config.REJECTED_BLOCK);
                            }
                        }
                        else{
                            //Bloco ja existe confirmaste a sua existencia
                            nodeCand.changeMistrust(Config.CONFIRM_BLOCK);
                        }
                    }
                }
            }
        }).start();
    }

}
