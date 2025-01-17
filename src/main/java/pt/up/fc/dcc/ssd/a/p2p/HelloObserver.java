package pt.up.fc.dcc.ssd.a.p2p;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.blockchain.Hello;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HelloObserver implements StreamObserver<Hello> {
    Network net;
    ManagedChannel channel;

    private static final Logger logger = Logger.getLogger(HelloObserver.class.getName());

    HelloObserver(Network net, ManagedChannel channel){
        this.net = net;
        this.channel = channel;
    }

    @Override
    public void onNext(Hello hello) {
        net.addP2P(hello);
    }

    @Override
    public void onError(Throwable throwable) {
        logger.warning("Error on hello");
        logger.warning(throwable.toString());
    }

    @Override
    public void onCompleted() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
