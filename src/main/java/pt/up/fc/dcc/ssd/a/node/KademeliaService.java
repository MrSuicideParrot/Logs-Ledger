package pt.up.fc.dcc.ssd.a.node;

import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;
import pt.up.fc.dcc.ssd.a.kademlia.*;

public class KademeliaService extends KademliaServiceGrpc.KademliaServiceImplBase {
    DHT dht;

    KademeliaService(DHT dht){
        this.dht = dht;
    }

    @Override
    public void findNode(NodeIDM request, StreamObserver<NodeM> responseObserver) {
        super.findNode(request, responseObserver);
    }

    @Override
    public void findValue(ValueIDM request, StreamObserver<NodeM> responseObserver) {
        super.findValue(request, responseObserver);
    }

    @Override
    public void store(PackToStoreM request, StreamObserver<Type.Empty> responseObserver) {
        super.store(request, responseObserver);
    }

    @Override
    public void ping(Type.Empty request, StreamObserver<Type.Empty> responseObserver) {
        responseObserver.onNext(Type.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
