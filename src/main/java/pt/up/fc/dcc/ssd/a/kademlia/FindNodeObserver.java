package pt.up.fc.dcc.ssd.a.kademlia;

import io.grpc.stub.StreamObserver;

import java.util.Set;

class FindNodeObserver implements StreamObserver<NodeM> {

    Set<NodeM> nodes;
    TrakerAsync tracker;

    FindNodeObserver(Set<NodeM> nodes, TrakerAsync tracker){
        this.nodes = nodes;
        this.tracker = tracker;
    }


    @Override
    public void onNext(NodeM nodeM) {
        nodes.add(nodeM);
    }

    @Override
    public void onError(Throwable throwable) {
        tracker.decrement();
    }

    @Override
    public void onCompleted() {
        tracker.decrement();
    }
}
