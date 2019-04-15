package pt.up.fc.dcc.ssd.a.tracker;

import io.grpc.stub.StreamObserver;

class TrackerService extends pt.up.fc.dcc.ssd.a.tracker.TrackerServerGrpc.TrackerServerImplBase {
    TrackerService(){

    }

    @Override
    public void getAnswer(challengeAnswer request, StreamObserver<challengeValidation> responseObserver) {
        super.getAnswer(request, responseObserver);
    }

    @Override
    public void idRequest(Peer request, StreamObserver<challenge> responseObserver){
        super.idRequest(request,responseObserver);
    }
}