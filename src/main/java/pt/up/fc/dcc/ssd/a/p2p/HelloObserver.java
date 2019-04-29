package pt.up.fc.dcc.ssd.a.p2p;

import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.blockchain.Hello;

public class HelloObserver implements StreamObserver<Hello> {
    Network net;

    HelloObserver(Network net){
        this.net = net;
    }

    @Override
    public void onNext(Hello hello) {
        net.addP2P(hello);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
