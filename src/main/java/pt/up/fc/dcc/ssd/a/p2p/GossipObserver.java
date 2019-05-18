package pt.up.fc.dcc.ssd.a.p2p;

import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.grpcutils.Type;
import pt.up.fc.dcc.ssd.a.utils.ArrayTools;

import java.util.logging.Logger;

public class GossipObserver implements StreamObserver<Type.Empty> {

    private Network net;
    private Node i;

    private static final Logger logger = Logger.getLogger(GossipObserver.class.getName());

    GossipObserver(Node i){
        this.i = i;
        this.net = i.getNetwork();
    }

    @Override
    public void onNext(Type.Empty empty) {

    }

    @Override
    public void onError(Throwable throwable) {
        logger.warning("Erro em gossip "+ ArrayTools.bytesToHex(i.getId()));
        //throwable.printStackTrace();
        logger.severe("Node will be removed"+ ArrayTools.bytesToHex(i.getId()));
        net.removeNodes(i.getId(),i);
    }

    @Override
    public void onCompleted() {

    }
}
